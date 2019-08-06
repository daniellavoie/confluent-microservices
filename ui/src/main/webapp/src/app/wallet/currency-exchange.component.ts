import { Component, OnInit } from '@angular/core';
import { ExchangeRateService } from '../exchange-rate/exchange-rate.service';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, tap } from 'rxjs/operators';
import { WalletService } from './wallet.service';
import { Wallet } from './wallet';
import { Operation } from './operation';
import { ExchangeRequest } from '../exchange-rate/exchange-request';
import { ExchangeRate } from '../exchange-rate/exchange-rate';
import { NumberSymbol } from '@angular/common';
import { ExchangeRequestService } from '../exchange-rate/exchange-request.service';

@Component({
  templateUrl: './currency-exchange.component.html',
  styleUrls: ['./currency-exchange.component.scss']
})
export class CurrencyExchangeComponent implements OnInit {
  public exchangeRequest: ExchangeRequest = new ExchangeRequest();
  public balance: number;
  public error: boolean;
  public exchangeRates: ExchangeRate[] = new Array();
  public amounts: number[] = new Array();

  constructor(
    private exchangeRateService: ExchangeRateService,
    private exchangeRequestService: ExchangeRequestService,
    private walletService: WalletService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit() {
    this.route.paramMap.pipe(
      switchMap(params =>
        this.walletService.getWallet(params.get("account")).pipe(
          tap(wallet => this.processWallet(wallet, params.get("currency"), this)),
          switchMap(_ => this.exchangeRateService.getExchangeRates()),
          tap(exchangeRate => this.processExchangeRate(exchangeRate, this))
        )
      )
    ).subscribe(null, error => {
      console.log(error);
    }, () => {
      console.log("Terminated")
    });
  }

  public dismissErrors() {
    this.error = false;
  }

  public exchange(exchangeCurrency: string) {
    this.exchangeRequest.exchangeCurrency = exchangeCurrency;
    this.exchangeRequest.amount = this.amounts[exchangeCurrency];

    this.exchangeRequestService.submitExchangeRequest(this.exchangeRequest)

      .subscribe(
        _ => this.router.navigate(["wallet", this.exchangeRequest.account]),
        _ => this.error = true,
        null
      )
  }

  private processExchangeRate(exchangeRate: ExchangeRate, component: CurrencyExchangeComponent) {
    if (exchangeRate.baseCurrency == component.exchangeRequest.baseCurrency) {
      if (component.exchangeRates[exchangeRate.quoteCurrency] == undefined) {
        component.exchangeRates[exchangeRate.quoteCurrency] = exchangeRate;
      } else {
        this.updateExchangeRate(component.exchangeRates[exchangeRate.quoteCurrency], exchangeRate);
      }

      component.exchangeRates[exchangeRate.quoteCurrency] = exchangeRate;

      if (this.amounts[exchangeRate.quoteCurrency] == undefined) {
        this.amounts[exchangeRate.quoteCurrency] = 0;
      }
    }

    if (exchangeRate.quoteCurrency == component.exchangeRequest.baseCurrency) {
      if (component.exchangeRates[exchangeRate.baseCurrency] == undefined) {
        component.exchangeRates[exchangeRate.baseCurrency] = exchangeRate;
      } else {
        this.updateExchangeRate(component.exchangeRates[exchangeRate.baseCurrency], exchangeRate);
      }

      if (this.amounts[exchangeRate.baseCurrency] == undefined) {
        this.amounts[exchangeRate.baseCurrency] = 0;
      }
    }
  }

  private processWallet(wallet: Wallet, currency: string, component: CurrencyExchangeComponent) {
    if (wallet == null) {
      component.router.navigate(["account"], { queryParams: { "notFound": "true" } });
      return;
    }

    var containsCurrency = false;
    for (var key in wallet.entries) {
      var entry = wallet.entries[key];
      if (entry.currency == currency) {
        component.balance = entry.balance;
        containsCurrency = true;
        break;
      }
    }

    if (!containsCurrency) {
      component.router.navigate(["wallet"]);
      return;
    }

    this.exchangeRequest.account = wallet.account;
    this.exchangeRequest.baseCurrency = currency
  }

  private updateExchangeRate(existingExchangeRate: ExchangeRate, exchangeRate: ExchangeRate) {
    existingExchangeRate.baseCurrency = exchangeRate.baseCurrency;
    existingExchangeRate.quoteCurrency = exchangeRate.quoteCurrency;
    existingExchangeRate.ask = exchangeRate.ask;
    existingExchangeRate.bid = exchangeRate.bid;
    existingExchangeRate.timestamp = exchangeRate.timestamp;
  }
}