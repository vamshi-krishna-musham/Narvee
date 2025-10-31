import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { LeaveService, LeaveRequest } from '../../services/leave.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';

// FullCalendar imports
import { CalendarOptions } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';

@Component({
  selector: 'calendar-view',
  templateUrl: './calendar-view.component.html',
  styleUrls: ['./calendar-view.component.scss']
})
export class CalendarViewComponent implements OnInit {
  leaves: LeaveRequest[] = [];
  displayedColumns = ['no', 'name', 'startdate', 'enddate', 'duration'];
  dataSource = new MatTableDataSource<LeaveRequest>([]);
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  // calendar options
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,dayGridWeek'
    },
    height: '90vh',
    events: [], // will be populated dynamically
    eventColor: '#2196f3', // default color (will override dynamically)
  };

  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private leave: LeaveService
  ) {}

  ngOnInit(): void {
    this.loadApprovedLeaves();
  }

  private loadApprovedLeaves(): void {
    this.leave.listApproved(1).subscribe({
      next: (res) => {
        this.leaves = res;
        this.dataSource.data = res;
        this.dataSource.paginator = this.paginator;
        // assign unique colors per employee name
        // assign unique colors per employee name
        // assign unique colors per employee name
      const colorMap: { [user: string]: string } = {};
      function generateColor(name: string): string {
        // Create a more random-like hash using bitwise mixing
        let hash = 0;
        for (let i = 0; i < name.length; i++) {
          hash = (hash << 5) - hash + name.charCodeAt(i);
          hash |= 0; // Convert to 32bit integer
        }

        // Spread hue more uniformly
        const hue = Math.abs(hash * 131) % 360;  // multiply by prime for dispersion
        const saturation = 70 + (Math.abs(hash) % 20); // 70–90%
        const lightness = 50 + (Math.abs(hash) % 10);  // 50–60%

        return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
      }

      const events = this.leaves.map((l) => {
        const user = l.userName || 'Unknown';
        if (!colorMap[user]) colorMap[user] = generateColor(user);

        // Fix end date (FullCalendar is exclusive)
        const endDate = new Date(l.endDate);
        endDate.setDate(endDate.getDate() + 1);

        return {
          title: `${user} (${l.duration}d)`,
          start: l.startDate,
          end: endDate.toISOString().split('T')[0],
          color: colorMap[user],
        };
      });



        this.calendarOptions.events = events;
      },
      error: (err) => {
        console.error('Error loading leaves', err);
        this.snackBar.open('Failed to load approved leaves', 'OK', { duration: 2500 });
      }
    });
  }

  backToHistory(): void {
    this.router.navigate(['/leave/approvals']);
  }
}
