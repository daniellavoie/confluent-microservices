import { Injectable } from '@angular/core';
import { Urls } from './urls';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UrlsService {
  private static API_URL = "/api/urls";

  private urls: Urls

  constructor(private http: HttpClient) { }

  public getUrls(): Observable<Urls> {
    Observable.create()
    if(this.urls != undefined) {
      return of(this.urls);
    } else {
      return this.http.get<Urls>(UrlsService.API_URL)
        .pipe(tap(urls => this.urls = urls));
    }
  }
}
