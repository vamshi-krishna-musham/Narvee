import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

// Angular Material
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatNativeDateModule } from '@angular/material/core';
import { MatOptionModule } from '@angular/material/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTableModule } from '@angular/material/table';
import { MatRippleModule } from '@angular/material/core';
import { MatPaginatorModule } from '@angular/material/paginator';



// CDK
import { TextFieldModule } from '@angular/cdk/text-field';

// App Routing
import { AppRoutingModule } from './app-routing.module';

// Components
import { AppComponent } from './app.component';
import { RegisterManagmentComponent } from './Components/register-managment/register-managment.component';
import { CustomSnackBarComponent } from './Components/custom-snack-bar/custom-snack-bar.component';
import { ForgotPasswordComponent } from './Components/forgot-password/forgot-password.component';
import { ChangepasswordComponent } from './sidebar/changepassword/changepassword.component';
import { ConfirmdeleteComponent } from './sidebar/confirmdelete/confirmdelete.component';
import { SidenavComponent } from './sidebar/sidenav/sidenav.component';
import { ViewProfileComponent } from './sidebar/view-profile/view-profile.component';

import { LeaveHomeComponent } from './leave/leave-home/leave-home.component';
import { ApplyLeaveComponent } from './leave/apply-leave/apply-leave.component';
import { LeaveHistoryComponent } from './leave/leave-history/leave-history.component';
import { LeaveApprovalsComponent } from './leave/leave-approvals/leave-approvals.component';
import { LeaveNavComponent } from './leave/leave-nav/leave-nav.component';
import { UpdateLeaveComponent } from './leave/update-leave/update-leave.component';
//import { ApplyLeaveComponent } from './leave/apply-leave/apply-leave.component';

// Interceptor
import { AuthInterceptor } from './interceptor/interceptors/auth.interceptor';

// IntlTelInput
import { NgxMatIntlTelInputComponent } from 'ngx-mat-intl-tel-input';

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
    LeaveHomeComponent,
    ApplyLeaveComponent,
    LeaveHistoryComponent,
    LeaveApprovalsComponent,
    LeaveNavComponent,
    UpdateLeaveComponent,
    
    //ApplyLeaveDialogComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule,
    MatIconModule,
    MatDialogModule,
    MatPaginatorModule,
    // Angular Material
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatDatepickerModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatNativeDateModule,
    MatOptionModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatTableModule,
    MatRippleModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatChipsModule,
    MatProgressBarModule,
    MatCardModule,
    MatSnackBarModule,

    // CDK
    TextFieldModule,

    // Intl Tel Input
    NgxMatIntlTelInputComponent,
  ],
  providers: [
    { provide: LocationStrategy, useClass: HashLocationStrategy },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
  schemas: [NO_ERRORS_SCHEMA],
})
export class AppModule {}
