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
  casualLeaves: number = 12;
  sickLeaves: number = 10;
  paidLeaves: number = 8;
  totalEligible: number = 30;
  totalLeavesApproved: number = 0;
  CancelledLeaves: number = 0;
  pendingLeaves: number = 0;
  balanceSl: number = 0;
  balanceCl: number = 0;
  balancePl: number = 0;
  duration?: number; // <-- add this
  leaves: LeaveRequest[] = [];
  loading = false;



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
        this.casualLeaves = 12;
        this.sickLeaves = 10;
        this.paidLeaves = 8;
        this.totalEligible = 30;
        this.totalLeavesApproved = 0;
        this.CancelledLeaves = 0;
        this.pendingLeaves = 0;
        this.balanceSl = 0;
        this.balanceCl = 0;
        this.balancePl = 0;
        const approvedLeaves = this.leaves.filter(
          l => l.status === 'APPROVED'
        );


        const totalUsed = approvedLeaves.reduce(
          (sum, l) => sum + ((l as any).duration || 0),
          0
        );
        this.totalLeavesApproved = totalUsed;
        this.totalEligible = this.totalEligible-totalUsed

        this.CancelledLeaves = this.leaves.filter(l => l.status === 'CANCELED').length;
        this.pendingLeaves = this.leaves.filter(l => l.status === 'PENDING').length;

        const approvedSickLeaves = this.leaves.filter(
          l => l.leaveType === 'Sick' && l.status === 'APPROVED'
        );


        const totalSickUsed = approvedSickLeaves.reduce(
          (sum, l) => sum + ((l as any).duration || 0),
          0
        );


        this.balanceSl = this.sickLeaves - totalSickUsed;

        const approvedcasualLeaves = this.leaves.filter(
          l => l.leaveType === 'Casual' && l.status === 'APPROVED'
        );


        const totalCasualUsed = approvedcasualLeaves.reduce(
          (sum, l) => sum + ((l as any).duration || 0),
          0
        );


        this.balanceCl = this.casualLeaves - totalCasualUsed;

        const approvedPaidLeaves = this.leaves.filter(
          l => l.leaveType === 'Paid' && l.status === 'APPROVED'
        );


        const totalPaidUsed = approvedPaidLeaves.reduce(
          (sum, l) => sum + ((l as any).duration || 0),
          0
        );


        this.balancePl = this.paidLeaves - totalPaidUsed;


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
