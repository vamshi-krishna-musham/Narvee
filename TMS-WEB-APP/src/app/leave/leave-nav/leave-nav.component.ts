import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ApplyLeaveComponent } from '../apply-leave/apply-leave.component';

@Component({
  selector: 'app-leave-nav',
  templateUrl: './leave-nav.component.html'
})
export class LeaveNavComponent {
  isAdminOrSuperAdmin = false;
  constructor(public router: Router, private dialog: MatDialog) {
    const role = (localStorage.getItem('profileRole') || localStorage.getItem('Role') || '').toUpperCase();
    this.isAdminOrSuperAdmin = ['ADMIN','SUPER ADMIN','SUPER_ADMIN','SUPERADMIN'].includes(role);
  }

  isActive(url: string) { return this.router.url === url; }

  openApply(): void {
    const ref = this.dialog.open(ApplyLeaveComponent, {
      width: '720px',
      maxWidth: '95vw',
      autoFocus: false,
      disableClose: true
    });

    ref.afterClosed().subscribe(result => {
      if (result === 'submitted') {
        // After submit, land on history and refresh it
        if (this.router.url !== '/leave/history') {
          this.router.navigate(['/leave/history']);
        } else {
          this.router.navigateByUrl('/', { skipLocationChange: true })
              .then(() => this.router.navigate(['/leave/history']));
        }
      }
    });
  }
}
