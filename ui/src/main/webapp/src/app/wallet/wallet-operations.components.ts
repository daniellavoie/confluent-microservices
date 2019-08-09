import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, tap } from 'rxjs/operators';
import { OperationService } from './operation.service';
import { Wallet } from './wallet';
import { WalletService } from './wallet.service';
import { Observable, empty } from 'rxjs';
import { Operation } from './operation';

@Component({
  templateUrl: './wallet-operations.component.html',
  styleUrls: ['./wallet-operations.component.scss']
})
export class WalletOperationsComponent implements OnInit {
  public wallet: Wallet;
  public currency: string;
  public operations: Operation[];

  constructor(
    private operationService: OperationService,
    private walletService: WalletService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.route.paramMap.pipe(
      switchMap(params =>
        this.walletService.getWallet(params.get("account")).pipe(
          switchMap(wallet => this.getOperations(wallet, params.get("currency"), this))
        )
      ),
    ).subscribe(operations => this.operations = operations, null, null);
  }

  private getOperations(wallet: Wallet, currency: string, component: WalletOperationsComponent): Observable<Operation[]> {
    if (wallet == null) {
      component.router.navigate(["account"], { queryParams: { "notFound": "true" } });
      return empty();
    }

    var containsCurrency = false;
    for (var key in wallet.entries) {
      if (wallet.entries[key].currency == currency) {
        containsCurrency = true;
        break;
      }
    }

    if (!containsCurrency) {
      component.router.navigate(["wallet"]);
      return empty();
    }

    component.wallet = wallet;
    component.currency = currency;

    return component.operationService.getTransactions(wallet.account, currency);
  }
}