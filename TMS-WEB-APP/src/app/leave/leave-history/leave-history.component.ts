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
  displayedSummaryColumns =[
  'totalConsumed',
  'approved',
  'cancelled',
  'pending',
  'balanceSl',
  'balanceCl',
  'balancePl'
];
  leaves: LeaveRequest[] = [];
  loading = false;
  casualLeaves = 12;
  sickLeaves=10;
  paidLeaves=8;
  totalEligible=30;
  totalLeavesConsumed=0
  totalLeavesApproved=0; 
  CancelledLeaves=0;	
  pendingLeaves=0;
  balanceSl=0;
  balanceCl=0;
  balancePl=0;

  summaryData: any[] = [];
 
  

  // role check flag
  isManager = localStorage.getItem('profileRole')?.toUpperCase() === 'SUPER ADMIN';

  constructor(
    private leave: LeaveService,
    private snack: MatSnackBar,
    private router: Router
  ) {}
  ngOnInit(): void {
    const profileId = Number(localStorage.getItem('profileId'));
    this.load(profileId);
 }
  
  load(id: number): void {
    this.loading = true;
    this.leave.listMine(id).subscribe({
      next: (res: LeaveRequest[]) => {
        this.leaves = (res || []).sort((a: LeaveRequest, b: LeaveRequest) => {
          const ad = new Date(a.startDate).getTime();
          const bd = new Date(b.startDate).getTime();
          return bd - ad; // newest first
        });

        this.totalLeavesConsumed = this.leaves.length;
        this.totalEligible = this.totalEligible-this.totalLeavesConsumed
        this.totalLeavesApproved = this.leaves.filter(l => l.status === 'APPROVED').length;
        this.CancelledLeaves = this.leaves.filter(l => l.status === 'CANCELED').length;
        this.pendingLeaves = this.leaves.filter(l => l.status === 'PENDING').length;
        this.balanceSl = this.sickLeaves - this.leaves.filter(l => l.status === 'APPROVED' && l.leaveType === 'Sick Leave').length;
        this.balanceCl = this.casualLeaves - this.leaves.filter(l => l.status === 'APPROVED' && l.leaveType === 'Casual Leave').length;
        this.balancePl = this.paidLeaves - this.leaves.filter(l => l.status === 'APPROVED' && l.leaveType === 'Paid Leave').length;

        // Prepare summary data for the summary table
        
        this.summaryData = [
          {
            totalEligible: this.totalEligible,
            approved: this.totalLeavesApproved,
            cancelled: this.CancelledLeaves,
            pending: this.pendingLeaves,
            balanceSl: this.balanceSl,
            balanceCl: this.balanceCl,
            balancePl: this.balancePl
          }
        ];

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
    return (
      start.getTime() > today.getTime() &&
      r.status !== 'CANCELED' &&
      r.status !== 'REJECTED'
    );
  }

  cancel(id: number): void {
    const profileId = Number(localStorage.getItem('profileId'));
    this.leave.cancel(id).subscribe({
      next: () => {
        this.snack.open('Leave cancelled', 'OK', { duration: 2000 });
        this.load(profileId);
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
