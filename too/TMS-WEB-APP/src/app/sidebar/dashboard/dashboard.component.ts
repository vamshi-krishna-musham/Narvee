import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { ChartOptions, ChartType } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TaskService } from '../../PathService/task.service';
import { MatMenuModule } from '@angular/material/menu';
import { filter } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatCardModule,
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    NgChartsModule,
    MatTableModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    MatInputModule,
    MatButtonModule,
    MatMenuModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  dropdownOpen = false;
  selectedProject: string | any = null;
  timeIntervals = ['daily', 'weekly', 'monthly', 'yearly'];
  intervalDropdownOpen = false;
  selectedInterval: string | null = null;
  selectedIntervalRange: string | null = null
  toggleDropdown() {
     this.dropdownOpen = !this.dropdownOpen;
    this.intervalDropdownOpen = false;
    this.intervalLabelDropdownOpen = false;

  }

  toggleIntervalDropdown() {

     this.intervalDropdownOpen = !this.intervalDropdownOpen;
    this.dropdownOpen = false;
    this.intervalLabelDropdownOpen = false;
  }

  constructor(private taskservice: TaskService, private elementRef: ElementRef) { }


  projects: any[] = []; // Define this at the top of your component

  getProjects() {
    const profileId = localStorage.getItem('profileId');

    this.taskservice.getProjects(profileId).subscribe(response => {
      if (response && Array.isArray(response.data)) {
        this.projects = response.data; // Assign to projects array
      }
      console.log(response, 'responseeeee');
    });
  }
@ViewChild('intervalDropdown') intervalDropdownRef!: ElementRef;
@ViewChild('intervalLabelDropdown') intervalLabelDropdownRef!: ElementRef;
@HostListener('document:click', ['$event'])
onClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement;

  const clickedInsideProject = this.projectDropdownRef?.nativeElement.contains(target);
  const clickedInsideInterval = this.intervalDropdownRef?.nativeElement.contains(target);
  const clickedInsideLabel = this.intervalLabelDropdownRef?.nativeElement.contains(target);

  if (!clickedInsideProject && !clickedInsideInterval && !clickedInsideLabel ) {
    this.dropdownOpen = false;
    this.intervalDropdownOpen = false;
    this.intervalLabelDropdownOpen = false;
  }
}




  getSelectedProjectName(): string | null {
    const selected = this.projects.find(p => p.pid === this.selectedProject);
    return selected ? selected.projectname : null;
  }


  cards = [
    { title: 'Tasks Completed', count: 0, description: 'Tasks finished successfully.', filter: 'daily' },
    { title: 'Tasks In Progress', count: 0, description: 'These tasks are currently being worked on.', filter: 'daily' },
    { title: 'To Do Tasks', count: 0, description: 'Tasks waiting to be started.', filter: 'daily' },
    { title: 'Overdue Tasks', count: 0, description: 'These tasks are past their due date.', filter: 'daily' },
    { title: 'Blocked Tasks', count: 0, description: 'Tasks that cannot proceed due to unresolved issues or dependencies.', filter: 'daily' }
  ];
  statusToCardMap: { [key: string]: string } = {
    'Closed': 'Tasks Completed',
    'In Progress': 'Tasks In Progress',
    'Open': 'To Do Tasks',
    'Overdue': 'Overdue Tasks',
    'Blocked': 'Blocked Tasks'
  };

  selectedCardIndex: number | null = null;


  getStartOfWeek(): string {
    const now = new Date();
    const start = new Date(now.setDate(now.getDate() - now.getDay())); // Sunday
    return this.formatDate(start);
  }

  getEndOfWeek(): string {
    const now = new Date();
    const end = new Date(now.setDate(now.getDate() - now.getDay() + 6)); // Saturday
    return this.formatDate(end);
  }

  getEightWeekRangeFromCurrent(): { fromDate: string, toDate: string } {
    const today = new Date();
    const dayOfWeek = today.getDay(); // 0 (Sun) to 6 (Sat)
    const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;

    // Start of current week (Monday)
    const startOfWeek = new Date(today);
    startOfWeek.setDate(today.getDate() + diffToMonday);

    // End of 8th week (Sunday of 8th week)
    const endOf8thWeek = new Date(startOfWeek);
    endOf8thWeek.setDate(startOfWeek.getDate() + (8 * 7 - 1)); // 8 weeks total

    return {
      fromDate: this.formatDate(startOfWeek),
      toDate: this.formatDate(endOf8thWeek)
    };
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }




  intervalTitle: string = 'Monthly Productivity Overview'; // Default

  onGlobalFilterSelect(interval: string) {
    this.selectedInterval = interval;
    this.intervalDropdownOpen = false;

    const titleMap: { [key: string]: string } = {
      daily: 'Daily Productivity Overview',
      weekly: 'Weekly Productivity Overview',
      monthly: 'Monthly Productivity Overview',
      yearly: 'Yearly Productivity Overview'
    };
    this.intervalTitle = titleMap[interval] || 'Productivity Overview';

    this.getIntervalLabels(interval); // 👈 fetch dynamic interval labels

    const userId = localStorage.getItem('profileId');
    if (userId) {
      this.getCountTimeInterval(this.selectedProject, userId, interval);
      this.getTaskprojectPriorityInterval(this.selectedProject, userId, interval);

      const payload: any = {
        timeIntervel: interval,
        userId,
        pid: this.selectedProject || null
      };

      if (interval === 'daily') {
        payload.fromDate = this.getStartOfWeek();
        payload.toDate = this.getEndOfWeek();
      }
      if (interval === 'weekly') {
        const currentWeek = this.getEightWeekRangeFromCurrent();
        payload.fromDate = currentWeek.fromDate;
        payload.toDate = currentWeek.toDate;
      }


      else if (interval === 'monthly') {
        payload.year = new Date().getFullYear();
      }

      this.getBarTaskCount(payload);
      this.getTeamPerformance(this.selectedProject, userId, interval);

    }
  }



  chartLabels: string[] = [];

  chartData = [
    {
      label: 'Tasks Done',
      data: [],
      backgroundColor: '#2196f3',
      borderColor: '#2196f3',
      borderWidth: 1,
      barPercentage: 0.7,
      categoryPercentage: 0.9,
      borderRadius: 6
    }
  ];
  chartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: {
      mode: 'index',
      intersect: false
    },
    plugins: {
      tooltip: {
        enabled: true,
        mode: 'index',
        intersect: false,
        callbacks: {
          label: (ctx) => `Tasks Done: ${ctx.raw}`
        }
      },
      legend: {
        display: false
      },
      title: {
        display: false
      }
    },
    scales: {
      x: {
        ticks: {
          font: {
            size: 12
          }
        },
        grid: {
          display: false
        }
      },
      y: {
        beginAtZero: true,
        ticks: {
          font: {
            size: 12
          }
        },
        grid: {
          display: false
        }
      }
    }
  };











  teamColumns: string[] = ['sno', 'name', 'assigned', 'completed', 'ongoing', 'overdue', 'blocked', 'position'];

  ngOnInit() {
    const userId = localStorage.getItem('profileId');

    this.getProjects();
    this.getTaskCount();
    this.getTaskPriorityProject();
    this.callDefaultBarChart();

    if (userId) {
      this.getTeamPerformance(undefined, userId); // no project or interval selected yet
    }
  }

  callDefaultBarChart() {
    const userId = localStorage.getItem('profileId');

    const payload = {
      timeIntervel: 'monthly', // Default to yearly
      year: new Date().getFullYear(),
      userId: userId,
      pid: null // No project selected on init
    };

    this.getBarTaskCount(payload);
  }

  getTaskCount() {
    const userId = localStorage.getItem('profileId');
    this.taskservice.getTaskCountDeafult(userId).subscribe((res: any) => {
      const data = res?.data;
      if (!Array.isArray(data)) return;
      this.patchCardCounts(data);
    });
  }

  getTaskCountByProject() {
    const userId = localStorage.getItem('profileId');
    if (!this.selectedProject) return;

    this.taskservice.getTaskCountProject(this.selectedProject, userId).subscribe((res: any) => {
      const data = res?.data;
      if (!Array.isArray(data)) return;
      this.patchCardCounts(data);
    });
  }

  // Update cards based on response
  patchCardCounts(data: any[]) {
    const taskSummary = data.filter(item => item.type === 'Total');

    this.cards = this.cards.map(card => {
      const match = taskSummary.find(entry => this.statusToCardMap[entry.status] === card.title);
      return {
        ...card,
        count: match?.count ?? 0
      };
    });
  }
  // Filter count by project + interval
  getCountTimeInterval(pid: number, userId: string, interval: string) {
    this.taskservice.getCountTimeInterval(pid, userId, interval).subscribe((res: any) => {
      this.patchCardCounts(res?.data);
    });
  }
  // Handle filter change (daily, weekly, etc.)
  onFilterSelect(interval: string) {
    this.selectedInterval = interval;
    console.log('data');

    const profileId = localStorage.getItem('profileId');
    console.log('data');

    if (profileId) {
      // Call API regardless of whether selectedProject is set
      this.getCountTimeInterval(this.selectedProject || null, profileId, interval);
    }
  }


  selectProject(pid: number) {
    this.selectedProject = pid;
    this.dropdownOpen = false;
    const userId = localStorage.getItem('profileId');

    this.getTaskCountByProject();
    this.getTaskprojectPriority(pid, userId);

    // Also fetch bar chart data again for the new project
    const interval = this.selectedInterval || 'monthly';

    const payload: any = {
      timeIntervel: interval,
      userId: userId,
      pid: pid
    };

    if (interval === 'daily') {
      payload.fromDate = '2025-07-01'; // You may replace this dynamically
      payload.toDate = '2025-07-07';
    }

    if (interval === 'monthly' || interval === 'yearly') {
      payload.year = 2025;
    }
    this.getTeamPerformance(pid, userId, interval);

    this.getBarTaskCount(payload);
  }

  highPriorityCount: number = 0;
  mediumPriorityCount: number = 0;
  lowPriorityCount: number = 0;

  doughnutData = {
    labels: ['High', 'Medium', 'Low'],
    datasets: [
      {
        data: [0, 0, 0], // default initially
        backgroundColor: ['#e53935', '#ffb300', '#43a047'],
        cutout: '70%'
      }
    ]
  };
  doughnutOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false,

    plugins: {
      legend: {
        position: 'bottom'
      }
    }
  };

  getTaskPriorityProject() {
    const userId = localStorage.getItem('profileId');

    this.taskservice.getTaskPriorityDefault(userId).subscribe((res: any) => {
      const priorities = Array.isArray(res.data)
        ? res.data.filter((item: { type: string }) => item.type === 'Total')
        : [];

      this.highPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'High')?.count || 0;
      this.mediumPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'Medium')?.count || 0;
      this.lowPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'Low')?.count || 0;

      // ✅ Reassign entire doughnutData object to trigger chart update
      this.doughnutData = {
        labels: ['High', 'Medium', 'Low'],
        datasets: [
          {
            data: [
              this.highPriorityCount,
              this.mediumPriorityCount,
              this.lowPriorityCount
            ],
            backgroundColor: ['#e53935', '#ffb300', '#43a047'],
            cutout: '70%'
          }
        ]
      };
    });
  }



  getTaskprojectPriority(pid: any, userId: any) {
    this.taskservice.getTaskPriorityProject(pid, userId).subscribe((res: any) => {
      const priorities = Array.isArray(res.data)
        ? res.data.filter((item: any) => item.type === 'Total')
        : [];

      this.highPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'High')?.count || 0;
      this.mediumPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'Medium')?.count || 0;
      this.lowPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'Low')?.count || 0;

      // ✅ Reassign doughnutData to trigger chart update
      this.doughnutData = {
        ...this.doughnutData,
        datasets: [
          {
            ...this.doughnutData.datasets[0],
            data: [
              this.highPriorityCount,
              this.mediumPriorityCount,
              this.lowPriorityCount
            ]
          }
        ]
      };
    });
  }
  getTaskprojectPriorityInterval(pid: number, userId: string, interval: string) {
    this.taskservice.getTaskprojectCountInterval(pid, userId, interval).subscribe((res: any) => {
      const priorities = Array.isArray(res.data)
        ? res.data.filter((item: any) => item.type === 'Total')
        : [];

      this.highPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'High')?.count || 0;
      this.mediumPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'Medium')?.count || 0;
      this.lowPriorityCount = priorities.find((p: { priority: string }) => p.priority === 'Low')?.count || 0;

      // ✅ Reassign doughnutData to trigger chart update
      this.doughnutData = {
        ...this.doughnutData,
        datasets: [
          {
            ...this.doughnutData.datasets[0],
            data: [
              this.highPriorityCount,
              this.mediumPriorityCount,
              this.lowPriorityCount
            ]
          }
        ]
      };
    });
  }
  getBarTaskCount(payload: any) {
    this.taskservice.GetBarTaskCount(payload).subscribe((res: any) => {
      if (!Array.isArray(res.data)) return;

      const totalItems = res.data.filter((item: any) => item.type === 'Total');

      // Dynamically determine X-axis labels
      let labels: string[];
      if (payload.timeIntervel === 'yearly') {
        labels = totalItems.map((item: any) => item.year); // use year field
      } else {
        labels = totalItems.map((item: any) => item.period); // use period for daily/weekly/monthly
      }

      const counts = totalItems.map((item: any) => item.count);

      this.chartLabels = labels;
      this.chartData = [
        {
          label: 'Tasks Done',
          data: counts,
          backgroundColor: '#2196f3',
          borderColor: '#2196f3',
          borderWidth: 1,
          barPercentage: 0.7,
          categoryPercentage: 0.9,
          borderRadius: 6
        }
      ];
    });
  }

  intervalLabels: string[] = []; // List of labels from API
  selectedIntervalLabel: string = ''; // Currently selected label
  intervalLabelDropdownOpen: boolean = false; // For toggling custom dropdown

  getIntervalLabels(interval: string) {
    this.taskservice.DropdownInterval(interval).subscribe((res: any) => {
      if (Array.isArray(res.data)) {
        this.intervalLabels = res.data.map((item: any) => item.label);
        this.selectedIntervalLabel = this.intervalLabels[0] || ''; // default
      }
    });
  }

  toggleIntervalLabelDropdown() {
   this.intervalLabelDropdownOpen = !this.intervalLabelDropdownOpen;
    this.dropdownOpen = false;
    this.intervalDropdownOpen = false;
  }
  convertToAPIDate(dateStr: string): string {
    const [day, month, year] = dateStr.split('-');
    return `${year}-${month}-${day}`;
  }

  selectIntervalLabel(label: string) {
    this.selectedIntervalLabel = label;
    this.intervalLabelDropdownOpen = false;

    const userId = localStorage.getItem('profileId');
    const pid = this.selectedProject || null;

    const payload: any = {
      timeIntervel: this.selectedInterval,
      userId,
      pid
    };

    if (this.selectedInterval === 'monthly') {
      const year = label.slice(-4);
      payload.year = +year;
    } else if (label.includes('to')) {
      const [from, to] = label.split(' to ');
      payload.fromDate = this.convertToAPIDate(from);
      payload.toDate = this.convertToAPIDate(to);
    }

    this.getBarTaskCount(payload);
  }
@ViewChild('projectDropdown') projectDropdownRef!: ElementRef;

  teamData: any[] = []; // Make sure this is declared at the top

  getTeamPerformance(pid?: any, userId?: any, timeInterval?: string) {
    this.taskservice.getTeamPerformance(pid, userId, timeInterval).subscribe((res: any) => {
      if (res?.data && Array.isArray(res.data)) {
        // Add S.No dynamically
        this.teamData = res.data.map((item: any, index: number) => ({
          ...item,
          sno: index + 1,
          position: item.position,
          fullname: item.firstName,
          Assigned: item.totalAssignedTasks,
          Completed: item.closedTaskCount,
          Ongoing: item.inProgressTaskCount,
          Overdue: item.overDueTaskCount,
          blocked: item.blockedTaskCount



        }));
      } else {
        this.teamData = [];
      }
    });
  }







}
