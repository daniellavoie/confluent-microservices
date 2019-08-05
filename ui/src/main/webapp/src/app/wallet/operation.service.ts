import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Operation } from './operation';
import { UrlsService } from '../urls.service';
import { switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class OperationService {

  constructor(private urlsService: UrlsService, private http: HttpClient) { }

  public getTransactions(account: string, currency: string): Observable<Operation[]> {
    return this.urlsService.getUrls().pipe(
      switchMap(urls => this.http.get<Operation[]>(urls.operation + "/operation/" + account + "/" + currency))
    )
  }
}
