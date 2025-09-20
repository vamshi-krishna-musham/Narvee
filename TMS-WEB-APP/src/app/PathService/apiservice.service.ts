import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, catchError, of, retry, Observable, TimeoutError } from 'rxjs';
import { HandledError } from './handled-error';
import { HttpErrors } from './http-errors';

@Injectable({
  providedIn: 'root'
})
export class ApiserviceService {

  
  readonly apiUrl: string;

  constructor(private http: HttpClient) {
    const protocol = window.location.protocol; // http: or https:
    const hostname = window.location.hostname; // localhost, LAN IP, or domain
    const port = 1122; // backend port

    // Automatically set API base URL based on environment
    if (hostname === 'localhost' || hostname === '127.0.0.1') {
      this.apiUrl = `${protocol}//localhost:${port}/`;
    } else {
      this.apiUrl = `${protocol}//${hostname}:${port}/`;
    }

    console.log('Detected API URL:', this.apiUrl);
  }
  get(url: string, options?: any): Observable<any> {
    return this.http.get(this.apiUrl + url, options).pipe(
      map((x) => x),
      catchError(x => of({ message: this.handleHttpError(x) }))
    );
  }
  

  post(url: string, data: any, options?: any) {
    return this.http.post(this.apiUrl + url, data, options).pipe(
      map((x) => x),
      catchError((x) => of({ message: this.handleHttpError(x) }))
    );
  }
  
  put(url: string, data: any) {
    return this.http.put(this.apiUrl + url, data).pipe(
      map((x) => x),
      retry(1),
      catchError(x=> of({message: this.handleHttpError(x)}))
    );
  }

  patch(url: string, data: any) {
    return this.http.patch(this.apiUrl + url, data).pipe(
      map((x) => x),
      retry(1),
      catchError(x=> of({message: this.handleHttpError(x)}))
    );
  }

  delete(url: string, data?: any) {
    return this.http.delete(this.apiUrl + url).pipe(
      map((x) => x),
      catchError(x=> of({message: this.handleHttpError(x)}))
    );
  }

  private responseHandler(x: any) {
    if (x.hasOwnProperty('didError') && !x['didError']) {
      return x['model'];
    } else {
      this.errorHandler(x);
    }
  }

  private errorHandler(x: any) {
    if (x.hasOwnProperty('message') && !x['errorMessage']) {
      throw new HandledError(x['errorMessage']);
    } else {
      throw new HandledError('unhandled error');
    }
  }

  getJson(path: string): Observable<any> {
    return this.http.get(path);
  }

 
  getFakeAPI(url: string): Observable<unknown> {
    return this.http.get(url);
  }
  // handle errors
  handleHttpError(error: HttpErrorResponse) {
    if (error instanceof TimeoutError) {
      return  'TimeoutError';
    }
    switch (error.status) {
      case 400: {
        if (error.error === 'invalid_username_or_password') {
          return `${HttpErrors[400]}: Invalid Credentials`;
        }
        return `${HttpErrors[400]}: Please re-check the api endpoint`;
      }
      case 401: {
        return `Authentication Error`;
      }
      case 403: {
        return `You don't have the required permissions`;
      }
      case 404: {
        return `Resource not found`;
      }
      case 422: {
        return ` Invalid data provided`;
      }
      case 500:
      case 501:
      case 502:
      case 503: {
        return `An internal server error occurred`;
      }
      case -1: {
        return `You appear to be offline. Please check your internet connection and try again`;
      }
      case 0: {
        return `Network Connection Failed`;
      }
      default: {
        return `An unknown error occurred`;
      }
    }
  }
}
