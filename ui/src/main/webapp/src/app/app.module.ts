import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WalletComponent } from './wallet/wallet.component';
import { AccountComponent } from './account/account.component';
import { DepositFundsComponent } from './funds/deposit-funds.component';
import { WithdrawFundsComponent } from './funds/withdraw-funds.component';
import { WalletOperationsComponent } from './wallet/wallet-operations.components';
import { CurrencyExchangeComponent } from './wallet/currency-exchange.component';

@NgModule({
  declarations: [
    AccountComponent,
    AppComponent,
    CurrencyExchangeComponent,
    DepositFundsComponent,
    WalletComponent,
    WalletOperationsComponent,
    WithdrawFundsComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
