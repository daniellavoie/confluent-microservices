import { UrlsService } from '../urls.service';
import { ExchangeRequest } from './exchange-request';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ExchangeRequestService {

  constructor(private urlsService: UrlsService, private http: HttpClient) {

  }

  public submitExchangeRequest(exchangeRequest: ExchangeRequest): Observable<void> {
    return this.urlsService.getUrls().pipe(
      switchMap(urls => this.http.post<void>(urls.operation + "/exchange", exchangeRequest)));
  }
}