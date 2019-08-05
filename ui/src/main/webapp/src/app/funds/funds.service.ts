import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { FundsRequest } from './funds-request';
import { UrlsService } from '../urls.service';

@Injectable({
  providedIn: 'root'
})
export class FundsService {
  constructor(
    private urlsService: UrlsService,
    private http: HttpClient) { }

  public submitFundsRequest(fundsRequest: FundsRequest): Observable<void> {
    return this.urlsService.getUrls().pipe(switchMap(urls => this.http.post<void>(urls.operation + "/funds", fundsRequest)));
  }
}
