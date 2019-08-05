import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  private WALLET_URL = "/api/"

  constructor() { }
}
