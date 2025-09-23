import { Component, OnInit } from '@angular/core';
import { SnackbarService } from '../../PathService/snack-bar.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss',
})
export class SidenavComponent implements OnInit {
  firstName: any = '';
  MiddleName: any = '';
  lastName: any = '';
profileName:any='';
profilePic:any='';
  isAdmin = false;
  isAdminOrSuperAdmin = false;
  constructor(private router: Router, private snackBarServ: SnackbarService) {}
Role:any
Email:any
ngOnInit(): void {
  this.firstName = localStorage.getItem('firstName') || '';
  this.lastName = localStorage.getItem('lastName') || '';
this.Role=localStorage.getItem('profileRole');
this.Email=localStorage.getItem('profileEmail');
this.profilePic=localStorage.getItem('profilePic')
  const fullName = [this.firstName,this.lastName]
    .filter(name => name && name.trim())
    .join(' ');

  this.profileName = fullName || 'Profile';

  const roleUpper = (this.Role || '').toUpperCase();
      this.isAdmin = roleUpper === 'SUPER ADMIN' || roleUpper === 'ADMIN' || roleUpper === 'SUPER_ADMIN' || roleUpper === 'SUPERADMIN';
      this.isAdminOrSuperAdmin = this.isAdmin;

}
goToApplyLeave() {
    this.router.navigate(['/leave/history']);
  }

  signOut(): void {
    localStorage.clear();
    this.snackBarServ.openSnackBarFromComponent({
      message: 'Signed out successfully!',
      duration: 1500,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-success'],
    });
    this.router.navigate(['/register-login']);
  }
  showSettings: boolean = false;

  toggleSettings() {
    this.showSettings = !this.showSettings;
  }
  viewprofile(){
        this.router.navigate(['/view-profile']);

  }
  refreshCurrentComponent() {
  const currentUrl = this.router.url;
  this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
    this.router.navigate([currentUrl]);
  });
}
}
