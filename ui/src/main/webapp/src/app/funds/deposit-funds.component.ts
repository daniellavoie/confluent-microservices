import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { FundsRequest } from './funds-request';
import { FundsService } from './funds.service';
import { Wallet } from '../wallet/wallet';
import { WalletService } from '../wallet/wallet.service';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-wallet',
  templateUrl: './deposit-funds.component.html',
  styleUrls: ['deposit-funds.component.scss']
})
export class DepositFundsComponent implements OnInit {
  fundsRequest: FundsRequest;
  error: boolean;
  processing: boolean;

  constructor(
    private fundsService: FundsService,
    private walletService: WalletService,
    private route: ActivatedRoute,
    private router: Router,
  ) {
    this.fundsRequest = new FundsRequest();
  }

  ngOnInit() {
    this.route.paramMap.pipe(
      switchMap(params => this.walletService.getWallet(params.get("account")))
    ).subscribe(wallet => this.processWallet(wallet, this.router));
  }

  public depositFunds() {
    this.processing = true;

    this.fundsService.submitFundsRequest(this.fundsRequest)

      .subscribe(_ => this.router.navigate(['wallet', this.fundsRequest.account]), () => this.error = true, () => this.processing = false);
  }

  public dismissErrors() {
    this.error = false;
  }

  private processWallet(wallet: Wallet, router: Router) {
    if (wallet == null) {
      router.navigate(["account"], { queryParams: { "notFound": "true" } });
      return;
    }

    this.fundsRequest.account = wallet.account;
    this.fundsRequest.action = "deposit";
  }
}
