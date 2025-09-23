import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveService, LeaveRequest } from '../../services/leave.service';

@Component({
  selector: 'app-leave-approvals',
  templateUrl: './leave-approvals.component.html'
})
export class LeaveApprovalsComponent implements OnInit {
  pending: LeaveRequest[] = [];
  loading = false;
  isAdminOrSuperAdmin = false;

  // ✅ Use this for Angular Material table
  displayedColumns: string[] = ['user', 'type', 'dates', 'reason', 'status', 'actions'];

  constructor(
    private leave: LeaveService,
    private snack: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    const roleRaw =
      localStorage.getItem('profileRole') ||
      localStorage.getItem('Role') ||
      '';
    const role = (roleRaw || '').toUpperCase().trim();
    this.isAdminOrSuperAdmin =
      role === 'ADMIN' ||
      role === 'SUPER ADMIN' ||
      role === 'SUPER_ADMIN' ||
      role === 'SUPERADMIN';

    if (!this.isAdminOrSuperAdmin) {
      this.snack.open('Access denied', 'OK', { duration: 2500 });
      this.router.navigate(['/leave']);
      return;
    }
    this.load();
  }

  load(): void {
    this.loading = true;
    this.leave.listPending().subscribe({
      next: (res) => {
        this.pending = res || [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Failed to load pending leaves', 'OK', { duration: 3000 });
      }
    });
  }

  approve(id: number): void {
    if (!confirm('Approve this leave request?')) return;
    this.leave.approve(id).subscribe({
      next: () => {
        this.snack.open('Leave approved', 'OK', { duration: 2000 });
        this.load();
      },
      error: () => this.snack.open('Approve failed', 'OK', { duration: 3000 })
    });
  }

  deny(id: number): void {
    if (!confirm('Deny this leave request?')) return;
    this.leave.deny(id).subscribe({
      next: () => {
        this.snack.open('Leave denied', 'OK', { duration: 2000 });
        this.load();
      },
      error: () => this.snack.open('Deny failed', 'OK', { duration: 3000 })
    });
  }

  trackById(_: number, r: LeaveRequest) {
    return r.id;
  }
  backToHistory(): void {
    this.router.navigate(['/leave/history']);
  }

}
