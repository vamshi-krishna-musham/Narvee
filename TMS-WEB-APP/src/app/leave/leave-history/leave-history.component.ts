import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveService, LeaveRequest } from '../../services/leave.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-leave-history',
  templateUrl: './leave-history.component.html'
})
export class LeaveHistoryComponent implements OnInit {
  displayedColumns = ['type', 'dates', 'reason', 'status', 'actions'];
  leaves: LeaveRequest[] = [];
  loading = false;
  totalLeaves = 0;
  approvedLeaves = 0;
  pendingLeaves = 0;
  deniedLeaves = 0;
  canceledLeaves = 0;

  // role check flag
  isManager = localStorage.getItem('profileRole')?.toUpperCase() === 'SUPER ADMIN';

  constructor(
    private leave: LeaveService,
    private snack: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }
  
  load(): void {
    this.loading = true;
    this.leave.listMine().subscribe({
      next: (res: LeaveRequest[]) => {
        this.leaves = (res || []).sort((a: LeaveRequest, b: LeaveRequest) => {
          const ad = new Date(a.startDate).getTime();
          const bd = new Date(b.startDate).getTime();
          return bd - ad; // newest first
        });
        // summary counts
        this.totalLeaves = this.leaves.length;
        this.approvedLeaves = this.leaves.filter(l => l.status === 'APPROVED').length;
        this.pendingLeaves = this.leaves.filter(l => l.status === 'PENDING').length;
        this.deniedLeaves = this.leaves.filter(l => l.status === 'DENIED').length;
        this.canceledLeaves = this.leaves.filter(l => l.status === 'CANCELED').length;

        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Failed to load leaves', 'OK', { duration: 2500 });
      }
    });
  }
  summary(): void{
    this
  }

  isCancellable(r: LeaveRequest): boolean {
    if (!r?.startDate) return false;
    const today = this.toDateOnly(new Date());
    const start = this.toDateOnly(r.startDate);
    return (
      start.getTime() > today.getTime() &&
      r.status !== 'CANCELED' &&
      r.status !== 'REJECTED'
    );
  }

  cancel(id: number): void {
    this.leave.cancel(id).subscribe({
      next: () => {
        this.snack.open('Leave cancelled', 'OK', { duration: 2000 });
        this.load();
      },
      error: () => this.snack.open('Cancel failed', 'OK', { duration: 3000 })
    });
  }

  // Navigate to full-page Apply Leave
  openApplyLeave(): void {
    this.router.navigate(['/leave/apply']);
  }

  // Navigate to Manage Leaves (manager/super admin only)
  openManageLeaves(): void {
    this.router.navigate(['/leave/approvals']);
  }

  private toDateOnly(d: string | Date): Date {
    const date = typeof d === 'string' ? new Date(d) : d;
    return new Date(date.getFullYear(), date.getMonth(), date.getDate());
  }

  trackById(_: number, r: LeaveRequest) {
    return r.id;
  }

}
