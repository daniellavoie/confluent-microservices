import { UrlsService } from '../urls.service';
import { HttpClient } from '@angular/common/http';
import { Observable, Subscriber } from 'rxjs';
import { ExchangeRate } from './exchange-rate';
import { map, tap } from 'rxjs/operators';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ExchangeRateService {

  constructor(private urlsService: UrlsService, private http: HttpClient) { }

  public getExchangeRates(): Observable<ExchangeRate> {
    var service = this;

    return Observable.create(onSubscribe);

    function onSubscribe(subscriber: Subscriber<ExchangeRate>) {
      service.urlsService.getUrls().pipe(
        map(urls => {
          return new EventSource(urls.spotter + "/api/exchange-rate");
        }),
        tap(source => {
          source.addEventListener('message', message => onEvent(message, subscriber));
        }),
        tap(source => source.addEventListener('error', error => onError(error, source, subscriber))),
        tap()
      ).subscribe();

      function onError(error: Event, eventSource: EventSource, subscriber: Subscriber<ExchangeRate>) {
        if (eventSource.readyState === 0) {
          eventSource.close();
          subscriber.complete();
        } else {
          subscriber.error(error);
        }
      }

      function onEvent(message: MessageEvent, subscriber: Subscriber<ExchangeRate>) {
        var exchangeRate = new ExchangeRate();
        var data = JSON.parse(message.data);

        exchangeRate.ask = data.ask;
        exchangeRate.baseCurrency = data.baseCurrency;
        exchangeRate.bid = data.bid;
        exchangeRate.quoteCurrency = data.quoteCurrency;
        exchangeRate.timestamp = data.timestamp;

        subscriber.next(exchangeRate);
      }
    }
  }
}