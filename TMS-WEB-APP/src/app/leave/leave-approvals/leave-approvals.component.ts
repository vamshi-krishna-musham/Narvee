import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { LeaveService, LeaveRequest } from '../../services/leave.service';

@Component({
  selector: 'app-leave-approvals',
  templateUrl: './leave-approvals.component.html'
})
export class LeaveApprovalsComponent implements OnInit {
  // ⬇️ Templates for the two dialogs
  @ViewChild('approveDialog') approveDialogTpl!: TemplateRef<any>;
  @ViewChild('denyDialog') denyDialogTpl!: TemplateRef<any>;
  @ViewChild('commentDialog') commentDialogTpl!: TemplateRef<any>;


  pending: LeaveRequest[] = [];
  loading = false;
  isAdminOrSuperAdmin = false;

  // ✅ Use this for Angular Material table
  displayedColumns: string[] = ['user', 'type', 'startdate','enddate','duration', 'reason', 'status', 'actions'];

  constructor(
    private leave: LeaveService,
    private snack: MatSnackBar,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const roleRaw =
      localStorage.getItem('profileRole') ||
      localStorage.getItem('Role') || '';
    const role = (roleRaw || '').toUpperCase().trim();
    this.isAdminOrSuperAdmin =
      role === 'ADMIN' || role === 'SUPER ADMIN' || role === 'SUPER_ADMIN' || role === 'SUPERADMIN';

    if (!this.isAdminOrSuperAdmin) {
      this.snack.open('Access denied', 'OK', { duration: 2500 });
      this.router.navigate(['/leave']);
      return;
    }
    this.load();
  }

  load(): void {
    this.loading = true;
    const managerId = Number(localStorage.getItem('profileId'));
    this.leave.listPending(managerId).subscribe({
      next: (res) => { this.pending = res || []; this.loading = false; },
      error: () => { this.loading = false; this.snack.open('Failed to load pending leaves', 'OK', { duration: 3000 }); }
    });
  }
  getIndex(i: number): number {
  return i + 1;
}


  // ✅ Material dialog for Approve
  approve(id: number): void {
    const ref = this.dialog.open(this.approveDialogTpl, {
      width: '420px',
      disableClose: true
    });
    ref.afterClosed().subscribe(confirmed => {
      if (!confirmed) return;
      this.leave.approve(id).subscribe({
        next: () => {
          this.snack.open('Leave approved', 'OK', {
            duration: 2000, horizontalPosition: 'center', verticalPosition: 'bottom',
            panelClass: ['custom-snack-success']
          });
          this.load();
        },
        error: () => this.snack.open('Approve failed', 'OK', {
          duration: 3000, horizontalPosition: 'center', verticalPosition: 'bottom',
          panelClass: ['custom-snack-failure']
        })
      });
    });
  }

  // ✅ Material dialog for Deny
  deny(id: number): void {
    const ref = this.dialog.open(this.denyDialogTpl, {
      width: '420px',
      disableClose: true,
      data: { comment: '' }
    });
    ref.afterClosed().subscribe(comment => {
      if (!comment) return;
      this.leave.deny(id, comment).subscribe({
        next: () => {
          this.snack.open('Leave denied', 'OK', {
            duration: 2000, horizontalPosition: 'center', verticalPosition: 'bottom',
            panelClass: ['custom-snack-success']
          });
          this.load();
        },
        error: () => this.snack.open('Deny failed', 'OK', {
          duration: 3000, horizontalPosition: 'center', verticalPosition: 'bottom',
          panelClass: ['custom-snack-failure']
        })
      });
    });
  }


  trackById(_: number, r: LeaveRequest) { return r.id; }

  backToHistory(): void { this.router.navigate(['/leave/history']); }
}
