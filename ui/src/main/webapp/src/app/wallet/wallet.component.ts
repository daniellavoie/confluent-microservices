import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { WalletService } from './wallet.service';
import { Wallet } from './wallet';

@Component({
  selector: 'app-wallet',
  templateUrl: './wallet.component.html',
  styleUrls: ['./wallet.component.scss']
})
export class WalletComponent implements OnInit {
  account: string;
  wallet: Wallet;

  constructor(
    private walletService: WalletService,
    private router: Router,
    private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.paramMap.pipe(
      switchMap(params => this.walletService.getWallet(params.get("account")))
    ).subscribe(wallet => this.processWallet(wallet, this.router));
  }

  public depositFunds(){
    this.router.navigate(["wallet", this.wallet.account,"funds", "deposit"]);
  }

  private processWallet(wallet: Wallet, router: Router) {
    if (wallet == null) {
      router.navigate(["account"], { queryParams: { "notFound": "true" } });
      return;
    }

    this.wallet = wallet;
  }

  public showOperations(currency: string){
    this.router.navigate(["wallet", this.wallet.account, currency]);
  }

  public trade(currency: string){
    this.router.navigate(["wallet", this.wallet.account, currency, "exchange"]);
  }

  public withdrawFunds(){
    this.router.navigate(["wallet", this.wallet.account,"funds", "withdraw"]);
  }
}
