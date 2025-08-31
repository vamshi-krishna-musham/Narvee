import { Component, inject } from '@angular/core';
import { ProjectsService } from '../../services/projects.service';
import { TaskmanagementService } from '../../services/taskmanagement.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SnackbarService } from '../../PathService/snack-bar.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
  forgotPasswordForm: FormGroup;
  private snackBarServ = inject(SnackbarService);

  constructor(
    private fb: FormBuilder,
    private apiService: TaskmanagementService,
    private dialogRef: MatDialogRef<ForgotPasswordComponent>
  ) {
    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email, Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z]{2,}\\.[a-zA-Z]{2,}$'),]],
      otp: ['', [Validators.pattern(/^[0-9]{4}$/)]],
      newPassword: ['',],
      confirmPassword: ['',]
    }, {
      validators: this.passwordMatchValidator
    });
  }
allowOnlyNumbers(event: KeyboardEvent): void {
  const allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Tab', 'Delete'];

  // Allow Ctrl/Cmd + V (paste)
  if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'v') {
    return;
  }

  if (!/^\d$/.test(event.key) && !allowedKeys.includes(event.key)) {
    event.preventDefault();
  }
}

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { mismatch: true };
  }
  hideNewPassword = true;
  hideConfirmPassword = true;

  otpSent: boolean = false;
  userId!: number;
  enteredOtp: string = '';
  otpVerified = false;

  loadingOtp = false; // Declare in your component

  requestOtp() {
    if (this.forgotPasswordForm.invalid) {
      this.forgotPasswordForm.markAllAsTouched();
      return;
    }

    this.loadingOtp = true; // Show loader

    const payload = this.forgotPasswordForm.value;

    const dataToBeSentToSnackBar = {
      message: '',
      duration: 1500,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-success'],
    };

    this.apiService.EmailVerify(payload).subscribe({
      next: (res: any) => {
        this.loadingOtp = false; // Hide loader

        if (res.status === 'success') {
          this.userId = res.data.id;
          this.otpSent = true;

          dataToBeSentToSnackBar.message = res.message || 'OTP sent successfully!';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-success'];
        } else {
          dataToBeSentToSnackBar.message = res.message || 'Failed to send OTP';
          dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
        }

        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      },
      error: (err) => {
        this.loadingOtp = false; // Hide loader

        dataToBeSentToSnackBar.message = err.error?.message || 'Failed to send OTP';
        dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
        this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
      }
    });
  }


  validatingOtp = false; // Declare at the class level

  validateOtp() {
    const otp = this.forgotPasswordForm.value.otp;
    if (!otp) return;

    this.validatingOtp = true; // Show loader

    this.apiService.validateotp(this.userId, otp).subscribe({
      next: (res: any) => {
        this.validatingOtp = false; // Hide loader

        if (res.status === 'success') {
          this.otpVerified = true;

          // Add validators dynamically
          this.forgotPasswordForm.get('newPassword')?.setValidators([
            Validators.required,
            Validators.minLength(6),
            Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/
            )
          ]);
          this.forgotPasswordForm.get('confirmPassword')?.setValidators([Validators.required]);
          this.forgotPasswordForm.get('newPassword')?.updateValueAndValidity();
          this.forgotPasswordForm.get('confirmPassword')?.updateValueAndValidity();

          this.snackBarServ.openSnackBarFromComponent({
            message: res.message || 'OTP verified successfully!',
            panelClass: ['custom-snack-success'],
            verticalPosition: 'top',
            horizontalPosition: 'center',
            duration: 2000
          });
        } else {
          this.snackBarServ.openSnackBarFromComponent({
            message: res.message || 'Invalid OTP',
            panelClass: ['custom-snack-failure'],
            duration: 2000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
          });
        }
      },
      error: () => {
        this.validatingOtp = false; // Hide loader

        this.snackBarServ.openSnackBarFromComponent({
          message: 'OTP verification failed',
          panelClass: ['custom-snack-failure'],
          duration: 2000,
          verticalPosition: 'top',
            horizontalPosition: 'center',
        });
      }
    });
  }



  submittingPassword = false; // Declare this in your component

  submitNewPassword() {
    this.submittingPassword = true;

    const payload = {
      email: this.forgotPasswordForm.value.email,
      newPassword: this.forgotPasswordForm.value.newPassword
    };

    this.apiService.SentPassword(payload).subscribe({
      next: (res: any) => {
        this.submittingPassword = false;

        this.snackBarServ.openSnackBarFromComponent({
          message: res.message || 'Password changed successfully!',
          panelClass: ['custom-snack-success'],
          duration: 2000,
          verticalPosition: 'top',
            horizontalPosition: 'center',
        });

        this.dialogRef.close(); // Close the modal/dialog
      },
      error: () => {
        this.submittingPassword = false;

        this.snackBarServ.openSnackBarFromComponent({
          message: 'Failed to change password',
          panelClass: ['custom-snack-failure'],
          duration: 2000,
          verticalPosition: 'top',
            horizontalPosition: 'center',
        });
      }
    });
  }


  onClose(): void {
    this.dialogRef.close();
  }
}
