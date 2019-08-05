import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Wallet } from './wallet';
import { HttpClient } from '@angular/common/http';
import { switchMap } from 'rxjs/operators';
import { UrlsService } from '../urls.service';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  constructor(private urlsService: UrlsService, private http: HttpClient) { }

  public getWallet(account: string): Observable<Wallet> {
    return this.urlsService.getUrls()
      .pipe(
        switchMap(urls => this.http.get<Wallet>(urls.wallet + "/api/wallet/" + account))
      );
  }
}
