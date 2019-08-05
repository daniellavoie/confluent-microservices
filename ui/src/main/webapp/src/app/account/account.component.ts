import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from "@angular/router";

import { Wallet } from '../wallet/wallet';
import { WalletService } from '../wallet/wallet.service';
import { tap } from 'rxjs/operators';

@Component({
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  public account: string;
  public wallet: Wallet;

  public loading: boolean;
  public walletNotFound: boolean;

  constructor(
    private walletService: WalletService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.route.queryParamMap

      .subscribe(params => this.processQueryParams(params, this), null, () => console.log("completed"));
  }

  public dismissErrors() {
    this.router.navigate(["account"]);
  }

  public getWallet() {
    this.router.navigate(["account"]);

    this.loading = true;

    this.walletService.getWallet(this.account)

      .subscribe(wallet => this.processWallet(wallet, this.router), null, () => this.loading = false);
  }

  processQueryParams(params: ParamMap, component: AccountComponent) {
    component.walletNotFound = params.get("notFound") != undefined;
  }

  private processWallet(wallet: Wallet, router: Router) {
    if (wallet == null) {
      this.router.navigate(["account"], { queryParams: { "notFound": "true" } });
      return;
    }

    router.navigate(["wallet", wallet.account]);
  }
}
