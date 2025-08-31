import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RegisterManagmentComponent } from './Components/register-managment/register-managment.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CustomSnackBarComponent } from './Components/custom-snack-bar/custom-snack-bar.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { SidenavComponent } from './sidebar/sidenav/sidenav.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { RouterModule } from '@angular/router';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip'; // ✅ import this

import { ConfirmdeleteComponent } from './sidebar/confirmdelete/confirmdelete.component';
import { AuthInterceptor } from './interceptor/interceptors/auth.interceptor';
import { ForgotPasswordComponent } from './Components/forgot-password/forgot-password.component';
import { MatDialogModule } from '@angular/material/dialog';
import { ChangepasswordComponent } from './sidebar/changepassword/changepassword.component';
import { NgxMatIntlTelInputComponent } from 'ngx-mat-intl-tel-input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { ViewProfileComponent } from './sidebar/view-profile/view-profile.component';

@NgModule({
  declarations: [
    AppComponent,
    RegisterManagmentComponent,
    CustomSnackBarComponent,
    SidenavComponent,
    ConfirmdeleteComponent,
    ForgotPasswordComponent,
    ChangepasswordComponent,
    ViewProfileComponent,



  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatCardModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    RouterModule,
    MatDialogModule,
    MatMenuModule,
    MatTooltipModule, // ✅ add this here
    NgxMatIntlTelInputComponent,
    MatProgressSpinnerModule,


  ],
  providers: [ {
    provide: LocationStrategy, useClass: HashLocationStrategy,
  },
    provideAnimationsAsync(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
