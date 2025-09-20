import { Component, inject } from '@angular/core';
import { TaskmanagementService } from '../../services/taskmanagement.service';
import { SnackbarService } from '../../PathService/snack-bar.service';

@Component({
  selector: 'app-changepassword',
  templateUrl: './changepassword.component.html',
  styleUrl: './changepassword.component.scss'
})
export class ChangepasswordComponent {
  oldPassword = '';
  oldPasswordTouched = false;
  isOldPasswordValid = false;
  showOldPassword: boolean = false;
showNewPassword: boolean = false;
showConfirmPassword: boolean = false;
  private snackBarServ = inject(SnackbarService);

constructor(private service : TaskmanagementService){}
  newPassword = '';
  newPasswordTouched = false;
  passwordRules = {
    minLength: false,
    uppercase: false,
    lowercase: false,
    number: false,
    specialChar: false,
  };
  isNewPasswordValid = false;
  
  confirmPassword = '';
  confirmTouched = false;
  isConfirmPasswordValid = false;
  oldPasswordErrorMessage: string = '';

  get canSubmit() {
    return this.isOldPasswordValid && this.isNewPasswordValid && this.isConfirmPasswordValid;
  }
  
  validateOldPassword() {
    this.oldPasswordTouched = true;
    const email = localStorage.getItem('profileEmail');
  
    if (!email || !this.oldPassword) return;
  
    const payload = {
      email: email,
      password: this.oldPassword
    };
  
    this.service.validatepassword(payload).subscribe({
      next: (res: any) => {
        if (res.status?.trim() === 'Success') {
          this.isOldPasswordValid = true;
          this.oldPasswordErrorMessage = '';
        } else {
          this.isOldPasswordValid = false;
          this.oldPasswordErrorMessage = res.message || 'Old password is incorrect';
        }
      },
      error: () => {
        this.isOldPasswordValid = false;
        this.oldPasswordErrorMessage = 'Something went wrong while verifying password.';
      }
    });
  }
  
  
  validateNewPassword() {
    const pwd = this.newPassword;
    this.newPasswordTouched = true;
    this.passwordRules = {
      minLength: pwd.length >= 6,
      uppercase: /[A-Z]/.test(pwd),
      lowercase: /[a-z]/.test(pwd),
      number: /\d/.test(pwd),
      specialChar: /[\W_]/.test(pwd),
    };
    this.isNewPasswordValid = Object.values(this.passwordRules).every(Boolean);
  }
  
  validateConfirmPassword() {
    this.isConfirmPasswordValid = this.confirmPassword === this.newPassword && this.newPassword.length > 0;
  }
 isLoading = false; // Add this as a class property

changeNewPassword() {
  const email = localStorage.getItem('profileEmail');
  const payload = {
    email: email,
    newPassword: this.newPassword
  };

  const dataToBeSentToSnackBar = {
    message: '',
    duration: 1500,
    verticalPosition: 'top',
    horizontalPosition: 'center',
    panelClass: ['custom-snack-success']
  };

  this.isLoading = true;  // Start loader

  this.service.SentPassword(payload).subscribe({
    next: (response: any) => {
      this.isLoading = false;  // Stop loader

      if (response.status?.trim().toLowerCase() === 'success') {
        dataToBeSentToSnackBar.message = response.message || 'Password changed successfully!';
        dataToBeSentToSnackBar.panelClass = ['custom-snack-success'];

        // Reset fields and touched flags if needed
        this.oldPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        this.resetTouchedFlags();
      } else {
        dataToBeSentToSnackBar.message = response.message || 'Password change failed. Please try again.';
        dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
      }

      this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
    },
    error: (err) => {
      this.isLoading = false;  // Stop loader

      dataToBeSentToSnackBar.message = err.message || 'Something went wrong. Please try again later.';
      dataToBeSentToSnackBar.panelClass = ['custom-snack-failure'];
      this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
    }
  });
}

  
  resetTouchedFlags() {
    this.oldPasswordTouched = false;
    this.newPasswordTouched = false;
    this.confirmTouched = false;
    this.isOldPasswordValid = false;
    this.isNewPasswordValid = false;
    this.isConfirmPasswordValid = false;
  }
  
}
