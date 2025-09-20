import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { LeaveService, LeaveRequest } from '../../services/leave.service';
import { ApplyLeaveDialogComponent } from '../apply-leave-dialog/apply-leave-dialog.component';
import { LeaveApprovalsComponent } from '../leave-approvals/leave-approvals.component';

@Component({
  selector: 'app-leave-history',
  templateUrl: './leave-history.component.html'
})
export class LeaveHistoryComponent implements OnInit {
  displayedColumns = ['type', 'dates', 'reason', 'status', 'actions'];
  leaves: LeaveRequest[] = [];
  loading = false;

  // role check flag
  isManager = localStorage.getItem('profileRole') === 'Super Admin';

  constructor(
    private leave: LeaveService,
    private snack: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.leave.listMine().subscribe({
      next: (res: LeaveRequest[]) => {
        this.leaves = (res || []).sort((a: LeaveRequest, b: LeaveRequest) => {
          const ad = new Date(a.startDate).getTime();
          const bd = new Date(b.startDate).getTime();
          return bd - ad; // newest first
        });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Failed to load leaves', 'OK', { duration: 2500 });
      }
    });
  }

  isCancellable(r: LeaveRequest): boolean {
    if (!r?.startDate) return false;
    const today = this.toDateOnly(new Date());
    const start = this.toDateOnly(r.startDate);
    return start.getTime() > today.getTime() && r.status !== 'CANCELED' && r.status !== 'REJECTED';
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

  // NEW: open Apply Leave dialog
  openApplyLeave(): void {
    const ref = this.dialog.open(ApplyLeaveDialogComponent, {
      width: '60vw',       // responsive width (60% of viewport)
      maxWidth: '800px',   // cap at 800px
      height: 'auto',      // let content decide height
      panelClass: 'apply-leave-dialog'
    });
    ref.afterClosed().subscribe(result => {
      if (result) this.load(); // refresh list after applying leave
    });
  }

  // NEW: open Manage Leaves dialog
  openManageLeaves(): void {
    this.dialog.open(LeaveApprovalsComponent, {
      width: '100%',
      height: '100%',
      maxWidth: '100vw',
      panelClass: 'full-screen-dialog'
    });
  }

  private toDateOnly(d: string | Date): Date {
    const date = typeof d === 'string' ? new Date(d) : d;
    return new Date(date.getFullYear(), date.getMonth(), date.getDate());
  }

  trackById(_: number, r: LeaveRequest) { return r.id; }
}
