import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LeaveService, LeaveRequest } from '../../services/leave.service';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

type SummaryRow = {
  totalEligible: number;
  approved: number;
  cancelled: number;
  pending: number;
  balanceSl: number;
};

@Component({
  selector: 'app-leave-history',
  templateUrl: './leave-history.component.html',
  // Important if your dashboard styles are global/shared. If your dashboard styles
  // are component-scoped, copy those classes into this component's SCSS (see notes below).
  encapsulation: ViewEncapsulation.Emulated
})
export class LeaveHistoryComponent implements OnInit {
  displayedColumns = ['no','type', 'startdate', 'enddate','duration', 'reason', 'status', 'Admin Comment', 'actions'];

  dataSource = new MatTableDataSource<LeaveRequest>([]);
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  // role check flag
  isManager = (localStorage.getItem('profileRole') || '').toUpperCase() === 'SUPER ADMIN';

  // raw leaves
  leaves: LeaveRequest[] = [];
  loading = false;

  // summary one-row data used by cards
  summaryData: SummaryRow[] = [];

  constructor(
    private leave: LeaveService,
    private snack: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    const profileId = Number(localStorage.getItem('profileId'));
    this.load(profileId);
  }

  // --- KPI cards (same structure/look as dashboard cards) ---
  get summaryCards() {
    const s = this.summaryData?.[0] ?? {
      totalEligible: 0,
      approved: 0,
      cancelled: 0,
      pending: 0,
      balanceSl: 0
    };

    return [
      { title: 'Balance',   count: s.totalEligible, description: 'Days available to use.',           dotColor: '#1976d2' },
      { title: 'Approved',  count: s.approved,      description: 'Approved leave requests.',         dotColor: '#1e88e5' },
      { title: 'Cancelled', count: s.cancelled,     description: 'Cancelled leave requests.',        dotColor: '#42a5f5' },
      { title: 'Pending',   count: s.pending,       description: 'Awaiting approval.',               dotColor: '#90caf9' },
      { title: 'Sick',      count: s.balanceSl,     description: 'Sick leave taken.',                dotColor: '#0ea5e9' }
    ];
  }
  // ---------------------------------------------------------

  load(id: number): void {
    this.loading = true;

    this.leave.listMine(id).subscribe({
      next: (res: LeaveRequest[]) => {
        // sort newest first
        this.leaves = (res || []).sort((a, b) => {
          const ad = new Date(a.startDate).getTime();
          const bd = new Date(b.startDate).getTime();
          return bd - ad;
        });

        this.dataSource = new MatTableDataSource<LeaveRequest>(this.leaves);
        this.dataSource.paginator = this.paginator;

        // compute summary values
        const approvedLeaves = this.leaves.filter(l => l.status === 'APPROVED');
        const totalApprovedDays = approvedLeaves.reduce((sum, l: any) => sum + (l.duration || 0), 0);

        // you can replace these with real entitlements if you pull them from backend
        const ENTITLED_TOTAL = 30;

        const cancelledCount = this.leaves.filter(l => l.status === 'CANCELED').length;
        const pendingCount = this.leaves.filter(l => l.status === 'PENDING').length;

        const approvedSick = this.leaves.filter(l => l.leaveType === 'Sick' && l.status === 'APPROVED');
        const totalSickUsed = approvedSick.reduce((sum, l: any) => sum + (l.duration || 0), 0);

        // prepare one-row summary for the cards
        this.summaryData = [{
          totalEligible: Math.max(ENTITLED_TOTAL - totalApprovedDays, 0),
          approved: totalApprovedDays,
          cancelled: cancelledCount,
          pending: pendingCount,
          balanceSl: totalSickUsed
        }];

        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Failed to load leaves', 'OK', { duration: 2500 });
      }
    });
  }
    getIndex(i: number): number {
  return i + 1;
}
  private toDateOnly(d: string | Date): Date {
    const date = typeof d === 'string' ? new Date(d) : d;
    return new Date(date.getFullYear(), date.getMonth(), date.getDate());
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
        this.snack.open('Leave cancelled', 'OK', { duration: 2000, horizontalPosition: 'center', verticalPosition: 'bottom',
        panelClass: ['custom-snack-failure'] });
        this.load(profileId);
      },
      error: () => this.snack.open('Cancel failed', 'OK', { duration: 3000 })
    });
  }

  update(id: number): void {
    this.router.navigate(['/leave/update', id]);
  }

  openApplyLeave(): void {
    this.router.navigate(['/leave/apply']);
  }

  openManageLeaves(): void {
    this.router.navigate(['/leave/approvals']);
  }

  trackByTitle(_: number, c: { title: string }) {
    return c.title;
  }
}
