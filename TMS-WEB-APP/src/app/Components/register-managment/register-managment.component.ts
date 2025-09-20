import { HttpClient } from '@angular/common/http';
import { Component, inject, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatTabGroup } from '@angular/material/tabs';
import { TaskmanagementService } from '../../services/taskmanagement.service';
import { SnackbarService } from '../../PathService/snack-bar.service';
import { ForgotPasswordComponent } from '../forgot-password/forgot-password.component';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import parsePhoneNumberFromString from 'libphonenumber-js';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { PhoneNumberUtil } from 'google-libphonenumber';

@Component({
  selector: 'app-register-managment',
  templateUrl: './register-managment.component.html',
  styleUrl: './register-managment.component.scss'
})
export class RegisterManagmentComponent {
  hideLoginPassword = true;
  hideSignUpPassword = true;
  hideConfirmPassword = true;
  private snackBarServ = inject(SnackbarService);
  @ViewChild('tabGroup') tabGroup: MatTabGroup | undefined;

  signupPassword = '';
  signupConfirmPassword = '';
  registerForm!: FormGroup
  loginForm!: FormGroup
  otpForm!: FormGroup;

  constructor(private service: TaskmanagementService, private formBuilder: FormBuilder, private http: HttpClient, private dialog: MatDialog, private router: Router) { }
  ngOnInit(): void {
    this.registerForm = this.formBuilder.group(
      {
        firstName: ['', [Validators.required, this.noLeadingTrailingOrOnlyWhitespaceValidator, Validators.minLength(2),
        Validators.maxLength(30),]],
        middleName: ['', [this.allowMiddleName, Validators.minLength(2),
        Validators.maxLength(30),]], // Optional, but still validated if user enters
        lastName: ['', [Validators.required, this.noLeadingTrailingOrOnlyWhitespaceValidator, Validators.minLength(2),
        Validators.maxLength(30),]],
        organisationName: ['', [Validators.required, this.organisationNameValidator, Validators.minLength(2),
        Validators.maxLength(50), 
]],
        companyDomain: ['', [Validators.minLength(2),
        Validators.maxLength(50),this.noOnlyWhitespaceValidator]],
        companySize: ['', [Validators.pattern(/^[0-9]*$/)]],
        industry: ['', [Validators.minLength(2),
        Validators.maxLength(50),this.noOnlyWhitespaceValidator]],
        email: [
          '',
          [
            Validators.required,
            Validators.email,
            Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z]{2,}\\.[a-zA-Z]{2,}$')
          ]],
        contactNumber: [
          '',

        ],
        // password: [
        //   '',
        //   [
        //     Validators.required,
        //     Validators.minLength(6),
        //     Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/
        //     )
        //   ]
        // ],
        addedBy: ['']
      },

    );


    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email, Validators.pattern('[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$'),]],
      // password: ['', [Validators.required]],
      loginType: ['tms']
    });

    this.otpForm = this.formBuilder.group({
  otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]

    });
  }
  // Custom validator: blocks only-white-space inputs
  noOnlyWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';
    return value.trim().length === 0 && value.length > 0 ? { whitespace: true } : null;
  }
  allowOnlyNumbers(event: KeyboardEvent): void {
    const allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Tab', 'Delete'];
    if (!/^\d$/.test(event.key) && !allowedKeys.includes(event.key)) {
      event.preventDefault();
    }
  }
  allowOnlyNumbersOtp(event: KeyboardEvent): void {
    const allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Tab', 'Delete'];

    // Allow Ctrl/Cmd + V (paste)
    if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'v') {
      return;
    }

    if (!/^\d$/.test(event.key) && !allowedKeys.includes(event.key)) {
      event.preventDefault();
    }
  }

  allowMiddleName(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';

    // Allow empty (optional field)
    if (value === '') {
      return null;
    }

    // Disallow only whitespace
    if (value.trim() === '') {
      return { whitespace: true };
    }

    // Disallow leading/trailing spaces
    if (value !== value.trim()) {
      return { whitespace: true };
    }

    // Only allow letters and single spaces between words
    const regex = /^[A-Za-z]+( [A-Za-z]+)*$/;
    return regex.test(value) ? null : { invalidChars: true };
  }
  noLeadingTrailingOrOnlyWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';

    if (value.trim() === '') {
      return { whitespace: true }; // only whitespace
    }

    // Check for leading/trailing spaces
    if (value !== value.trim()) {
      return { whitespace: true };
    }

    // Only allow letters and spaces between words
    const regex = /^[A-Za-z]+( [A-Za-z]+)*$/;
    return regex.test(value) ? null : { invalidChars: true };
  }
  organisationNameValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';

    if (value.trim() === '') {
      return { whitespace: true };
    }

    // Allow alphanumeric and special chars, but must contain at least one letter
    const hasLetter = /[a-zA-Z]/.test(value);
    if (!hasLetter) {
      return { noLetter: true };
    }

    return null; // valid
  }

  previousStep() {
    this.registerStep = 1;
  }


  onlyNumberKey(evt: any) {
    var ASCIICode = (evt.which) ? evt.which : evt.keyCode
    if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57))
      return false;
    return true;
  }
  phoneUtil = PhoneNumberUtil.getInstance();


  showOtpVerification = false;
  loadingRegister = false;

  onRegister() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.loadingRegister = false;  // Prevent loader from hanging
      return;
    }

    this.loadingRegister = true; // Start loader

    const { confirmPassword, ...formData } = this.registerForm.value;
if (formData.email) {
  formData.email = formData.email.toLowerCase();
}
    const rawPhoneObj = formData.contactNumber;
    let formattedPhone = '';

    const phoneUtil = PhoneNumberUtil.getInstance();

    if (rawPhoneObj && rawPhoneObj.internationalNumber) {
      try {
        const parsedNumber = phoneUtil.parse(rawPhoneObj.internationalNumber, rawPhoneObj.countryCode?.toUpperCase());
        const isValid = phoneUtil.isValidNumber(parsedNumber);

        if (!isValid) {
          this.loadingRegister = false;
          this.snackBarServ.openSnackBarFromComponent({
            message: 'Invalid phone number for the selected country.',
            duration: 2000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
            panelClass: ['custom-snack-failure'],
          });
          return;
        }

        formattedPhone = phoneUtil.format(parsedNumber, 1); // INTERNATIONAL format

      } catch (error) {
        this.loadingRegister = false;
        this.snackBarServ.openSnackBarFromComponent({
          message: 'Invalid phone number format.',
          duration: 2000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        });
        return;
      }
    }

    const payload = {
      ...formData,
      contactNumber: formattedPhone // '' if nothing is entered
    };

    const dataToBeSentToSnackBar = {
      message: '',
      duration: 1500,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-success'],
    };

    this.service.managementregister(payload).subscribe({
      next: (response: any) => {
        this.loadingRegister = false;

        if (response.status === 'success') {
          dataToBeSentToSnackBar.message = response.message || 'Registration successful!';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-success'];

          this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
          this.registerForm.reset();
          this.registerStep = 1;

          if (this.tabGroup) this.tabGroup.selectedIndex = 0;
        } else {
          dataToBeSentToSnackBar.message = response.data || 'Registration failed. Please try again.';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
          this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
        }
      },
      error: (err) => {
        this.loadingRegister = false;
        dataToBeSentToSnackBar.message = err?.error?.message || 'An error occurred. Please try again later.';
        dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      }
    });
  }




  otpId: any;
  loading: boolean = false;
  loadingResendOtp = false;

  login() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.otpForm.get('otp')?.setValue('');
    this.loadingResendOtp = true;

    const loginPayload = this.loginForm.value;

    const dataToBeSentToSnackBar = {
      message: '',
      duration: 1500,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-success'],
    };

    this.service.managementlogin(loginPayload).subscribe({
      next: (response: any) => {
        this.loadingResendOtp = false;

        if (response.status === 'success') {
          this.otpId = response.data;
          this.showOtpVerification = true;
          dataToBeSentToSnackBar.message = response.message || 'Login successful!';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-success'];
        } else {
          dataToBeSentToSnackBar.message = response.message || 'Login failed. Please try again.';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
        }

        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      },
      error: (err) => {
        this.loadingResendOtp = false;
        dataToBeSentToSnackBar.message = err.message || 'An error occurred. Please try again later.';
        dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      }
    });
  }


  loadingOtp = false;


  verifyOtp() {
    if (this.otpForm.invalid) {
      this.otpForm.markAllAsTouched();
      return;
    }

    this.loadingOtp = true; // Show loader

    const otp = Object.values(this.otpForm.value).join('');
    const { email, password } = this.loginForm.value;

    const payload = {
      email,
      password,
      otp: this.otpForm.value.otp,
      otpId: this.otpId,
    };

    this.service.verifyOtp(payload).subscribe({
      next: (response: any) => {
        this.loadingOtp = false; // Hide loader

        const message = response.message || 'OTP verified successfully!';
        const dataToBeSentToSnackBar = {
          message,
          duration: 2000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: response.status === 'success' ? ['custom-snack-success'] : ['custom-snack-failure'],
        };

        if (response.status === 'success') {
          const { token, userId, firstName, lastName, middleName, email, userRole, adminId, rolePrivileges, profilePic ,companyDomain ,companySize ,organizationName ,industry,contactNumber,position } = response.data || {};

          if (token) localStorage.setItem('token', token);
          if (userId) localStorage.setItem('profileId', userId.toString());
          if (firstName) localStorage.setItem('firstName', firstName);
          if (lastName) localStorage.setItem('lastName', lastName);
          if (middleName) localStorage.setItem('middleName', middleName);
          if (email) localStorage.setItem('profileEmail', email);
          if (userRole) localStorage.setItem('profileRole', userRole);
          if (adminId) localStorage.setItem('adminId', adminId);
          if (rolePrivileges) localStorage.setItem('rolePrivileges', JSON.stringify(rolePrivileges));
          if (profilePic) localStorage.setItem('profilePic', profilePic)
          if(companyDomain)localStorage.setItem('companyDomain',companyDomain)
          if(companySize)localStorage.setItem('companySize',companySize)
            if(organizationName)localStorage.setItem('organizationName',organizationName)
           if(industry)localStorage.setItem('industry',industry)
            if(contactNumber)localStorage.setItem('contactNumber',contactNumber)
              if(position)localStorage.setItem('position',position)
          this.router.navigate(['/Dashboard']);
        }

        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      },
      error: (err) => {
        this.loadingOtp = false; // Hide loader on error
        this.snackBarServ.openSnackBarFromComponent({
          message: err.message || 'OTP verification failed',
          duration: 2000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        });
      }
    });
  }


  autoFocusNext(event: KeyboardEvent, nextInput: HTMLInputElement | null) {
    const input = event.target as HTMLInputElement;

    if (input.value.length === 1 && nextInput) {
      nextInput.focus();
    }
  }
  onForgotPassword() {
    this.dialog.open(ForgotPasswordComponent, {
      width: '600px',
      disableClose: true,
    });
  }
  activeTabIndex = 0;

  onTabChange(event: MatTabChangeEvent) {
    this.activeTabIndex = event.index;
  }
  registerStep: number = 1;

  nextStep() {
    this.registerStep = 2;
  }

  isStepOneValid(): boolean {
    return (
      !!this.registerForm.get('firstName')?.valid &&
      !!this.registerForm.get('lastName')?.valid &&
      !!this.registerForm.get('email')?.valid &&
      !!this.registerForm.get('organisationName')?.valid
    );
  }



}
