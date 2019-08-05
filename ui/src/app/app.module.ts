import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WalletComponent } from './wallet/wallet.component';
import { EnvironmentService } from './environment.service';
import { WalletService } from './wallet.service';
import { OperationService } from './operation.service';

@NgModule({
  declarations: [
    AppComponent,
    WalletComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [EnvironmentService, OperationService, WalletService],
  bootstrap: [AppComponent]
})
export class AppModule { }
