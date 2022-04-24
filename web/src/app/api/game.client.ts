import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Observable} from 'rxjs';
import {Game} from "./model/game";
import {CreateGameRequest} from "./model/create.game.request";


@Injectable({
  providedIn: 'root'
})
export class GameClient {

  protected basePath = 'http://localhost:8081';
  protected defaultHeaders = new HttpHeaders();

  constructor(protected readonly httpClient: HttpClient) {
  }

  public create(createGameRequest: CreateGameRequest): Observable<Game> {
    if (createGameRequest === null || createGameRequest === undefined) {
      throw new Error('Required parameter NewGameCommand was null or undefined when calling newGame.');
    }
    const url = this.basePath + `/api/v1/mancala`;
    return this.httpClient.post<Game>(url, createGameRequest, {headers: this.prepareDefaultHeaders()});
  }

  public retrieve(gameId: string, observe: any = 'body', reportProgress: boolean = false): Observable<Game> {
    if (gameId === null || gameId === undefined) {
      throw new Error('Required parameter gameId was null or undefined when calling retrieveGame.');
    }
    const url = this.basePath + `/api/v1/mancala/${encodeURIComponent(String(gameId))}`;
    return this.httpClient.get<Game>(url, {headers: this.prepareDefaultHeaders()});
  }

  public play(gameId: string, position: number): Observable<Game> {
    if (gameId === null || gameId === undefined) {
      throw new Error('Required parameter gameId was null or undefined when calling play.');
    }
    if (position === null || position === undefined) {
      throw new Error('Required parameter move was null or undefined when calling play.');
    }
    const url = this.basePath + `/api/v1/mancala/${encodeURIComponent(String(gameId))}/move/` + position;
    return this.httpClient.put<Game>(url, null, {headers: this.prepareDefaultHeaders()});
  }


  private prepareDefaultHeaders() {
    let headers = this.defaultHeaders;

    const httpHeaderAccepts: string[] = [
      'application/json'
    ];
    const httpHeaderAcceptSelected: string | undefined = this.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    const consumes: string[] = [
      'application/json'
    ];
    const httpContentTypeSelected: string | undefined = this.selectHeaderContentType(consumes);
    if (httpContentTypeSelected !== undefined) {
      headers = headers.set('Content-Type', httpContentTypeSelected);
    }
    return headers;
  }

  public selectHeaderAccept(accepts: string[]): string | undefined {
    if (accepts.length === 0) {
      return undefined;
    }

    const type = accepts.find((x: string) => this.isJsonMime(x));
    if (type === undefined) {
      return accepts[0];
    }
    return type;
  }

  public selectHeaderContentType(contentTypes: string[]): string | undefined {
    if (contentTypes.length === 0) {
      return undefined;
    }

    const type = contentTypes.find((x: string) => this.isJsonMime(x));
    if (type === undefined) {
      return contentTypes[0];
    }
    return type;
  }

  public isJsonMime(mime: string): boolean {
    const jsonMime: RegExp = new RegExp('^(application\/json|[^;/ \t]+\/[^;/ \t]+[+]json)[ \t]*(;.*)?$', 'i');
    return mime !== null && (jsonMime.test(mime) || mime.toLowerCase() === 'application/json-patch+json');
  }

}
