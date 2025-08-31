import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private snackBar: MatSnackBar, private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');

    const clonedRequest = token
      ? request.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        })
      : request;

    return next.handle(clonedRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 503) {
          this.snackBar.open(' Service Not Available', 'Close', {
            duration: 5000,
            panelClass: ['custom-snack-failure'],
            horizontalPosition: 'center',
            verticalPosition: 'top',
          });
        } else if (error.status === 401) {
          localStorage.clear(); // or call logout service
          this.snackBar.open('Session expired. Please login again.', 'Close', {
            duration: 4000,
            panelClass: ['custom-snack-failure'],
            horizontalPosition: 'center',
            verticalPosition: 'top',
          });
          this.router.navigate(['/register-login']);
        }

        return throwError(() => error); // pass the error along
      })
    );
  }
}
