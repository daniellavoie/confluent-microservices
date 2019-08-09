import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AccountComponent } from './account/account.component';
import { WalletComponent } from './wallet/wallet.component';
import { DepositFundsComponent } from './funds/deposit-funds.component';
import { WithdrawFundsComponent } from './funds/withdraw-funds.component';
import { WalletOperationsComponent } from './wallet/wallet-operations.components';
import { CurrencyExchangeComponent } from './wallet/currency-exchange.component';

const routes: Routes = [
  { path: '', redirectTo: 'account', pathMatch: 'full'},
  { path: 'account', component: AccountComponent },
  { path: 'wallet/:account', component: WalletComponent },
  { path: 'wallet/:account/funds/deposit', component: DepositFundsComponent },
  { path: 'wallet/:account/funds/withdraw', component: WithdrawFundsComponent },
  { path: 'wallet/:account/:currency', component: WalletOperationsComponent },
  { path: 'wallet/:account/:currency/exchange', component: CurrencyExchangeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
