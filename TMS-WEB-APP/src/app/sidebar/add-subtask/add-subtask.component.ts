import { CommonModule } from '@angular/common';
import { Component, inject, TemplateRef, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ISnackBarData, SnackbarService } from '../../PathService/snack-bar.service';
import { Subject, takeUntil } from 'rxjs';
import { ProjectsService } from '../../services/projects.service';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { AddTeamMemberComponent } from '../sidenav/add-team-member/add-team-member.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AddTaskviewComponent } from '../add-taskview/add-taskview.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AddSubtaskviewComponent } from '../add-subtaskview/add-subtaskview.component';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { IConfirmDialogData } from '../sidenav/projects/confirm-dialog-data';
import { DialogService } from '../../PathService/dialog.service';
import { CommonDeleteComponent } from '../common-delete/common-delete.component';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClient } from '@angular/common/http';
import { ApiserviceService } from '../../PathService/apiservice.service';
import { MatBadgeModule } from '@angular/material/badge'; // Added by Manoj Madiraju for Comment Count

@Component({
  selector: 'app-add-subtask',
  standalone: true,
  imports: [
    MatTableModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSortModule,
    MatPaginatorModule,
    CommonModule,
    MatTooltipModule,
    ReactiveFormsModule,
    FormsModule,
    MatSelectModule,
    MatOptionModule,
    MatDialogModule,
    MatBadgeModule // Added by Manoj Madiraju for comment count
  ],
  templateUrl: './add-subtask.component.html',
  styleUrl: './add-subtask.component.scss'
})
export class AddSubtaskComponent {
  private destroyed$ = new Subject<void>();
  totalItems: any;
  pageEvent!: PageEvent;
  pageSize = 10;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  pageSizeOptions = [5, 10, 25];
  currentPageIndex = 0;
  itemsPerPage = 10;
  sortField = 'updateddate';
  sortOrder = 'desc';
  field = "";
  debugShowActions = true; // Added by Manoj Madiraju for subtask comment edit/delete actions
  canAddProject: boolean = true;
  canEditProject: boolean = false;
  canDeleteProject: boolean = false;
  canSeeProjectActions: boolean = false;
  private dialogServ = inject(DialogService);
  fileDisplayedColumns: string[] = ['icon', 'fileName', 'view', 'download'];

  constructor(private dialog: MatDialog, private router: Router, private route: ActivatedRoute, private apiService: ApiserviceService, private http: HttpClient) { }

  dataTobeSentToSnackBarService: ISnackBarData = {
    message: '',
    duration: 2500,
    verticalPosition: 'top',
    horizontalPosition: 'center',
    direction: 'above',
    panelClass: ['custom-snack-success'],
  };
  private snackBarServ = inject(SnackbarService);
  private projectServ = inject(ProjectsService);
  dataSource = new MatTableDataSource<any>([

  ]);
  dataTableColumns: string[] = []; // Move initialization to ngOnInit



  priorityList = [
    { label: 'High', value: 'High', color: '#f44336' },       // Red
    { label: 'Medium', value: 'Medium', color: '#ff9800' },   // Orange
    { label: 'Low', value: 'Low', color: '#4caf50' },         // Green
    { label: 'None', value: 'None', color: '#9e9e9e' }         // Grey
  ];

  statusList = [
    { label: 'Open', value: 'Open', color: '#4caf50' },
    { label: 'In Progress', value: 'In Progress', color: '#2196f3' },
    { label: 'In Review', value: 'In Review', color: '#ff9800' },
    { label: 'To be Tested', value: 'To be Tested', color: '#9c27b0' },
    { label: 'On Hold', value: 'On Hold', color: '#795548' },
    { label: 'Overdue', value: 'Overdue', color: '#f44336' },
    { label: 'Closed', value: 'Closed', color: '#607d8b' },
    { label: 'Blocked', value: 'Blocked', color: '#e91e63' }
  ];
  statusOptions = [
    { label: 'Open', value: 'Open', color: '#4caf50' },
    { label: 'In Progress', value: 'In Progress', color: '#2196f3' },
    { label: 'In Review', value: 'In Review', color: '#ff9800' },
    { label: 'To be Tested', value: 'To be Tested', color: '#9c27b0' },
    { label: 'On Hold', value: 'On Hold', color: '#795548' },
    { label: 'Overdue', value: 'Overdue', color: '#f44336' },
    { label: 'Closed', value: 'Closed', color: '#607d8b' },
    { label: 'Blocked', value: 'Blocked', color: '#e91e63' },
  ];
  getStatusColor(status: string): string {
    const found = this.statusOptions.find(opt => opt.value === status);
    return found ? found.color : '#ccc';
  }
  setEditingRow(id: number): void {
    this.editingStatusId = id;
  }

  getStatusClass(status: string) {
    const statusObj = this.statusList.find(s => s.value === status);
    return statusObj ? statusObj.color : '#9e9e9e'; // Default grey if status not found
  }

  getPriorityClass(priority: string) {
    const priorityObj = this.priorityList.find(p => p.value === priority);
    return priorityObj ? priorityObj.color : '#9e9e9e'; // Default grey if priority not found
  }
  goBack(): void {
    this.router.navigate(['/addtask'], {
      queryParams: {
        projectId: this.projectId,
        pid: this.pid ,
       projectname  :this.projectname
      }
    });
  }
    previewDialogRef!: MatDialogRef<any>;

  selectedFiles: any[] = [];
  @ViewChild('filesDialog') filesDialog!: TemplateRef<any>;

  openFilesDialog(files: any[]): void {
    this.selectedFiles = files;
    this.dialog.open(this.filesDialog);
  }

  downloadFile(file: any) {
    this.projectServ.downloadfile(file.id).subscribe((blob: Blob) => {
      const fileName = file.fileName.split('-')[0];
      const downloadUrl = window.URL.createObjectURL(blob);

      const a = document.createElement('a');
      a.href = downloadUrl;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);

      window.URL.revokeObjectURL(downloadUrl);
    });
  }


  closeDialog(): void {
    if (this.previewDialogRef) {
      this.previewDialogRef.close();
    }
  }

  @ViewChild('previewDialog') previewDialog!: TemplateRef<any>;

  previewUrl: string = '';
  previewType: 'image' | 'text' = 'image';

  viewFile(file: any): void {
    const url = `${this.apiService.apiUrl}task/project/download-file/${file.id}`;

    this.http.get(url, { responseType: 'blob' }).subscribe(blob => {
      const fileType = blob.type;
      const blobUrl = URL.createObjectURL(blob);

      if (fileType === 'application/pdf') {
        window.open(blobUrl, '_blank');
      } else if (fileType.startsWith('image/')) {
        this.previewUrl = blobUrl;

        this.previewDialogRef = this.dialog.open(this.previewDialog, {
          width: '60%',
          maxWidth: '50vw',
          height: 'auto',
          panelClass: 'custom-image-dialog'
        });

      } else if (fileType.startsWith('text/')) {
        window.open(blobUrl, '_blank');
      } else {
        const newWindow = window.open();
        newWindow?.document.write(`
            <html>
              <head>
                <title>No Preview</title>
                <style>
                  body {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: 100vh;
                    margin: 0;
                    background-color:rgb(37, 36, 36);
                    font-family: Arial, sans-serif;
                  }
                  h2 {
                    color: white;
                  }
                </style>
              </head>
              <body>
                <h2>No preview available for this file type.</h2>
              </body>
            </html>
          `);
      }
    }, error => {
      console.error('File view failed', error);
    });
  }
  projectId!: string;
  pid!: any;
  taskid!: any;
  ticketid: any;
  targetdate: any;
  startdate:any;
  status:any;
  projectStatus:any;
  taskname:any;
  projectname:any;
  // Added by manoj madiraju for comment ownership and editing
  currentUserId: string | number = '';
  /** ✅ Holds the resolved, full display name for the current user */
  currentUserDisplayName: string = '';
  editingComment?: any;
  // prevent accidental double submit
  isSubmitting = false;
  ngOnInit() {
    const privileges = JSON.parse(localStorage.getItem('rolePrivileges') || '[]');
    this.canAddProject = privileges.includes('CREATE_SUB_TASK');
    this.canEditProject = privileges.includes('EDIT_SUB_TASK');
    this.canDeleteProject = privileges.includes('DELETE_SUB_TASK');
    this.canSeeProjectActions = privileges.includes('SUB_TASK_ACTION_VISIBLE');
    this.dataTableColumns = [
      'SerialNum',
      'SubTaskId',
      'SubTaskName',
      'AssignedTo',
      'Status',
      'StartDate',
      'Priority',
      'DueDate',
      'Duration',
      'UpdatedBy',
      ...(this.canSeeProjectActions ? ['Action'] : []),
      'ViewFiles',
      'MoreInfo'
    ];
    this.route.queryParams.subscribe(params => {
      this.taskid = params['taskid'];
      this.projectId = params['projectId'];
      this.pid = params['pid'];
      this.ticketid = params['ticketid'];
      this.targetdate = params['targetdate'];
      this.status =params['status'];
      this.projectStatus=params['projectStatus'];
      this.taskname = params['taskName'];
      this.projectname = params['projectname'];
      this.startdate=params['startdate'];
      // Added by manoj madiraju to set current user id once
      this.currentUserId = this.getCurrentUserId();
      /** ✅ Resolve once and reuse everywhere so we never fall back to "You" */
      this.currentUserDisplayName = this.resolveCurrentUserDisplayName();
      console.log(this.targetdate, 'this.targetdatengOnInit');
      console.log(this.startdate, 'this.startdatedatengOnInit');
      console.log(this.taskid, 'taskid from route');
      console.log(this.projectId, 'projectId from route');
      console.log(this.pid, 'pid from route');
      console.log(this.status,'statusssss');
      console.log(this.projectStatus,'projectStatus');
      console.log(this.taskname,'taskname');
      console.log(this.projectname,'projectname');
    });
  this.getsubTaskDetailsAll();
  }
addsubtaskview() {
  if (this.projectStatus === 'Completed') {
    const snackBarData: ISnackBarData = {
      message: 'Cannot add a subtask. Project is completed.',
      duration: 3000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure'],
    };
    this.snackBarServ.openSnackBarFromComponent(snackBarData);
    return; // Prevent dialog from opening
  }

  if (this.status === 'Closed') {
    const snackBarData: ISnackBarData = {
      message: 'Cannot add a subtask. Task  is closed.',
      duration: 3000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure'],
    };
    this.snackBarServ.openSnackBarFromComponent(snackBarData);
    return; // Prevent dialog from opening
  }

  const dialogRef = this.dialog.open(AddSubtaskviewComponent, {
    width: '100%',
    maxWidth: '700px',
    disableClose: true,
    data: {
      projectId: this.projectId,
      pid: this.pid,
      taskid: this.taskid,
      targetdate: this.targetdate,
      startdate: this.startdate
    }
  });

  dialogRef.afterClosed().subscribe((result) => {
    if (result === true) {
      this.getsubTaskDetailsAll(this.currentPageIndex + 1);
    }
  });
}

  edittaskview(task: any) {
    const dialogRef = this.dialog.open(AddSubtaskviewComponent, {
      width: '100%',
      maxWidth: '700px',
      disableClose: true,
      data: {
        projectId: this.projectId,
        pid: this.pid,
        taskid: this.taskid,
        targetdate: this.targetdate,
       startdate:this.startdate,

        subtaskId: task.subTaskId

      }

    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.getsubTaskDetailsAll(this.currentPageIndex + 1); // refresh table data
      }
    });
  }
  private getDialogConfigData(
    dataToBeSentToDailog: Partial<IConfirmDialogData>,
    action: { delete: boolean; edit: boolean; add: boolean; updateSatus?: boolean }
  ) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = action.edit || action.add ? '40vw' : action.delete ? '600px' : '400px'; // ⬅️ increased from 'fit-content' to '600px'
    dialogConfig.maxHeight = '90vh'; // ⬅️ limit max height to avoid unnecessary scroll
    dialogConfig.autoFocus = false;
    dialogConfig.disableClose = false;
    dialogConfig.panelClass = dataToBeSentToDailog.actionName;
    dialogConfig.data = dataToBeSentToDailog;
    return dialogConfig;
  }
  deleteTask(task: any) {
    const dataToBeSentToDailog: Partial<IConfirmDialogData> = {
      title: 'Confirmation',
      message: 'Are you sure you want to delete?',
      confirmText: 'Yes',
      cancelText: 'No',
      actionName: 'delete-Project'
    };

    const dialogConfig = this.getDialogConfigData(dataToBeSentToDailog, { delete: true, edit: false, add: false });

    const dialogRef = this.dialogServ.openDialogWithComponent(
      CommonDeleteComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe({
      next: () => {
        if (dialogRef.componentInstance.allowAction) {
          this.projectServ.deletesubTask(task.subTaskId).pipe(takeUntil(this.destroyed$)).subscribe({
            next: (response: any) => {
              if (response.status === 'success') {
                this.getsubTaskDetailsAll();
                this.dataTobeSentToSnackBarService.message = response.message || 'Sub Task Deleted Successfully';
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-success'];
              } else {
                this.dataTobeSentToSnackBarService.message = response.message || 'Sub Task Deletion Failed';
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
              }
              this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
            },
            error: (err: any) => {
              this.dataTobeSentToSnackBarService.message = err.message || 'Sub Task Deletion Failed';
              this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
              this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
            },
          });
        }
      },
    });
  }
  getsubTaskDetailsAll(pagIdx = 1) {
    const pagObj = {
      pageNumber: pagIdx,
      pageSize: this.itemsPerPage,
      sortField: this.sortField,
      sortOrder: this.sortOrder,
      keyword: this.field || 'empty', // search keyword
      ticketId: this.ticketid
    };

    return this.projectServ.getSubTaskAll(pagObj)
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (response: any) => {
          this.totalItems = response.data.subtasks.totalElements || 0;
          this.dataSource.data = response.data.subtasks.content.map((x: any, i: number) => ({
            ...x,
            commentCount: x.commentCount ?? 0, // Added by Manoj Madiraju for Comment Count chip
            serialNum: (pagIdx - 1) * this.itemsPerPage + i + 1
          }));
        },
        error: (err: any) => {
          this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
          this.dataTobeSentToSnackBarService.message =
            err?.error?.message || 'Failed to fetch team members.';
          this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
        }
      });
  }


  projects: { assignUsers: { fullname: string }[] }[] = [];

  // This method returns the first capitalized letter of the fullname
  getInitial(user: any): string {
    return user?.fullname?.charAt(0)?.toUpperCase() || '';
  }



  getDisplayNames(users: { fullname: string }[]): string {
    if (!users || users.length === 0) return '-';
    return users.map(u => u.fullname).join(', ');
  }

  getAvatarColor(index: number): string {
    const colors = ['#f44336', '#2196f3', '#4caf50', '#ff9800', '#9c27b0'];
    return colors[index % colors.length];
  }

  getColor(index: number): string {
    const colors = ['#4caf50', '#2196f3', 'rgb(223 59 215) ', '#9c27b0', '#f44336'];
    return colors[index % colors.length];
  }

  getFullNameTooltip(users: any[]): string {
    return users?.map(u => ` ${u.fullname}`).join('\n') || '';
  }
  editingStatusId: number | null = null;

  onStatusChange(subtaskId: number, status: string): void {
    this.editingStatusId = null;

    const updatedby = localStorage.getItem('profileId'); // Fetch from localStorage

    if (!updatedby) {
      const snackBarData: ISnackBarData = {
        message: 'User not authorized.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        direction: 'above',
        panelClass: ['custom-snack-failure'],
      };
      this.snackBarServ.openSnackBarFromComponent(snackBarData);
      return;
    }



    this.projectServ.updatesubtaskstatus(subtaskId, status, updatedby).subscribe({
      next: (res: any) => {
        const isSuccess = res.status === 'success';

        const snackBarData: ISnackBarData = {
          message: res.message || (isSuccess ? 'Status updated successfully!' : 'Update failed.'),
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure'],
        };

        this.snackBarServ.openSnackBarFromComponent(snackBarData);
    this.getsubTaskDetailsAll();

      },
      error: (err: any) => {
        const snackBarData: ISnackBarData = {
          message: err?.error?.message || 'Something went wrong. Please try again.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        };
        this.snackBarServ.openSnackBarFromComponent(snackBarData);
      }
    });
  }


  searchTimeout: any;

  onSearchChange() {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.currentPageIndex = 0; // Reset to first page when searching
      this.getsubTaskDetailsAll(1);
    }, 300); // Delay in ms
  }

  handlePageEvent(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.itemsPerPage = event.pageSize;
    this.currentPageIndex = event.pageIndex;
    this.getsubTaskDetailsAll(this.currentPageIndex + 1); // 1-based index for API
  }

  sortData(event: Sort) {
    this.sortField = event.active;
    this.sortOrder = event.direction || 'asc';
    this.getsubTaskDetailsAll(this.currentPageIndex + 1);
  }
  @ViewChild('descriptionDialog') descriptionDialog!: TemplateRef<any>;

  showDescription(row: any): void {
    const description = row.subTaskDescription || 'No description available';

    this.dialog.open(this.descriptionDialog, {
      data: { description },
      width: '600px'
    });
  }
  @ViewChild('commentsDialog') commentsDialog!: TemplateRef<any>;

  selectedTaskId!: any;
  selectedTicketId!: number;
  commentText: string = '';
  selectedStatus: any
  commentsList: any[] = [];
  // Added by Manoj Madiraju for inline edit state
  editingCommentId: number | null = null;
  editCommentText = '';

  showComments(row: any): void {
    this.selectedTaskId = row.subTaskId;
    this.selectedTicketId = row.ticketid;
    this.selectedStatus = row.status; // store status here

    this.getcommentSubTask();
    this.dialog.open(this.commentsDialog);
  }

  // Added the helper by Manoj Madiraju
  private bumpCommentCount(subtaskId: number, delta: number): void {
    const rows = this.dataSource.data;
    const idx = rows.findIndex((r: any) => String(r.subTaskId ?? r.subtaskId) === String(subtaskId));
    if (idx > -1) {
      const curr = Number(rows[idx].commentCount || 0);
      rows[idx] = { ...rows[idx], commentCount: Math.max(0, curr + delta) };
      // reassign to trigger mat-table change detection
      this.dataSource.data = [...rows];
    }
  }

  // Sets the exact count (used after fetching comments fresh)
  private setCommentCountExact(subtaskId: number, count: number): void {
    const rows = this.dataSource.data;
    const idx = rows.findIndex((r: any) => String(r.subTaskId ?? r.subtaskId) === String(subtaskId));
    if (idx > -1) {
      rows[idx] = { ...rows[idx], commentCount: Math.max(0, Number(count) || 0) };
      this.dataSource.data = [...rows];
    }
  }

  submitComment(): void {
    const commentTextarea = document.querySelector('textarea[name="comment"]') as HTMLTextAreaElement;

    if (!this.commentText || this.commentText.trim() === '') {
      // mark the field as touched to trigger mat-error
      if (commentTextarea) commentTextarea.dispatchEvent(new Event('blur'));
      return;
    }

    if (this.isSubmitting) return;
    this.isSubmitting = true;

    const userId = localStorage.getItem('profileId');
    const payload = {
      subTaskId: this.selectedTaskId,
      ticketid: this.selectedTicketId,
      updatedby: Number(userId),
      comments: this.commentText.trim(),
      status: this.selectedStatus,

    };

    this.projectServ.commentAddSubtask(payload).subscribe({
      next: () => {
        // Code Added by Manoj Madiraju - For immediate UX feedback (optimistic insert)
        const optimistic = {
          temp: true,
          id: `tmp-${Date.now()}-${Math.random().toString(36).slice(2)}`,
          fullname: this.currentUserDisplayName || '',
          createddate: this.formatDate(new Date()),
          description: this.commentText.trim(),
          userId: Number(userId),
        };
        this.pushNewCommentToTop(optimistic);
        // ✅ Immediately update the count beside the icon
        this.bumpCommentCount(Number(this.selectedTaskId), +1);
        this.snackBarServ.openSnackBarFromComponent({
          message: 'Comment added successfully!',
          duration: 2500,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-success']
        });
        this.commentText = '';
        this.isSubmitting = false;
        // Code Added by Manoj Madiraju For authoritative refresh from server to avoid duplicates
        this.getcommentSubTask();
      },
      error: (err: any) => {
        this.isSubmitting = false;
        this.snackBarServ.openSnackBarFromComponent({
          message: err?.error?.message || 'Failed to add comment.',
          duration: 2500,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure']
        });
      }
    });
  }

  // Normalizers used by signature/dedup (fixes temp vs server duplicates)
  private normalizeUserId(raw: any): string {
    const n = Number(raw);
    return Number.isFinite(n) ? String(n) : String(raw ?? '').trim();
  }
  private normalizeDate(raw: any): string {
    if (!raw) return '';
    const s = String(raw).trim();
    // If ISO or contains time, take Y-M-D
    const isoHead = s.match(/^\d{4}-\d{2}-\d{2}/);
    if (isoHead) return isoHead[0];
    // Try Date parsing (fallback)
    const d = new Date(s);
    if (!isNaN(d.getTime())) return this.formatDate(d);
    return s;
  }
  private normalizeText(raw: any): string {
    return String(raw ?? '')
     .replace(/\s+/g, ' ')
     .trim();
  }

  // Build a stable signature for de-duping across optimistic vs server items
  private commentSig(c: any): string {
    const ownerId =
      c?.userId ??
      c?.userid ??
      c?.updatedby ??
      c?.updatedBy ??
      c?.createdBy ??
      c?.createdby ??
      '';

    const normId = this.normalizeUserId(ownerId);
    const normDate = this.normalizeDate(c?.createddate);
    const normText = this.normalizeText(c?.description);

    return `${normId}|${normDate}|${normText}`;
  }

/** 🔎 Safely extract a numeric comment id (the one used by task/comment-tms/{id}) */
  private extractNumericCommentId(x: any): number | null {
    const raw =
      x?.commentId ??
      x?.commentid ??
      x?.id ??
      x?.subTaskCommentId ??
      x?.subtaskCommentId ??
      x?.trackid ??
      x?.trackId ??
      null;
    const n = Number(raw);
    return Number.isFinite(n) && n > 0 ? n : null;
  }

  getcommentSubTask(): void {
    this.projectServ.getCommentsubTask(this.selectedTaskId).subscribe({
      next: (res: any) => {
        const mapped = (res?.data || []).map((x: any) => {
          const numericId = this.extractNumericCommentId(x) // <-- prefer trackid

          // Try to read a user id from API fields first
          const ownerId =
            x.updatedby ?? x.updatedBy ?? x.userid ?? x.userId ??
            x.createdby ?? x.createdBy ?? x.profileId ?? x.profileid ?? null;

            // ✅ Prefer API fullname; if it's me and empty, use the resolved full name (NOT "You")
            const displayName =
              x.fullname ||
              (String(ownerId) === String(this.currentUserId) ? this.currentUserDisplayName : '');

        return {
          // UI fields
          id: numericId, // ← store numeric id or null,
          fullname: displayName,
          createddate: x.createddate,
          description: x.description,
          userId: ownerId,
        };
      });
        console.log(this.commentsList, 'fullname')
        // Added by Manoj Madiraju to show exactly what the server has (sorted, no merge with local)
        // const serverOnly = (mapped || []).sort(
        // (a: any, b: any) => String(b.createddate).localeCompare(String(a.createddate)));

        // this.commentsList = serverOnly;

        // Show server items newest-first
        this.commentsList = (mapped || []).sort(
          (a: any, b: any) => String(b.createddate).localeCompare(String(a.createddate)));
        // ✅ Keep count accurate in table
        this.setCommentCountExact(Number(this.selectedTaskId), this.commentsList.length);

        // debug
        console.log('✅ Comments mapped:', this.commentsList);
      },

      error: (err: any) => {
        console.error('Error fetching comments:', err);
        this.commentsList = [];
      }
    });
  }

  // 🔧 Unique-ify by stable id; else by composite signature (server-first ordering preferred)
  private dedupeComments(arr: any[]): any[] {
    const seenIds = new Set<string>();
    const seenSigs = new Set<string>();
    const out: any[] = [];

    for (const c of arr) {
      const id = c?.id != null ? `id:${String(c.id)}` : null;
      const sig = `sig:${this.commentSig(c)}`;

      if (id) {
        if (!seenIds.has(id)) {
          seenIds.add(id);
          seenSigs.add(sig); // also mark signature so temp twin won't be added later
          out.push(c);
        }
      continue;
      }

      if (!seenSigs.has(sig)) {
        seenSigs.add(sig);
        out.push(c);
      }
    }

    return out.sort((a, b) => String(b.createddate).localeCompare(String(a.createddate)));
  }


  closeCommentsDialog(): void {
    this.dialog.closeAll();
    this.commentText = '';
    this.commentsList = [];
  }

 openTemplateDialog(templateRef: TemplateRef<any>, row?: any) {
  this.selectedFilesReport = []; // 🔁 Clear previously selected files

  if (row?.subTaskId) {
    this.selectedTaskId = row.subTaskId;
    this.getSubuploadfiles(); // Fetch latest uploaded files
  }

  this.dialog.open(templateRef, {
    width: '500px'
  });
}



  selectedFilesdata: File[] = [];
  selectedFilesReport: File[] = [];
  existingFilesReport: any[] = []; // from API or database

onFileSelectedReport(event: Event): void {
  const input = event.target as HTMLInputElement | null;
  if (input && input.files && input.files.length > 0) {
    const fileArray = Array.from(input.files);
    this.selectedFilesReport = [...this.selectedFilesReport, ...fileArray];
  }
}

removeSelectedFile(index: number): void {
  this.selectedFilesReport.splice(index, 1);
}

submitFiles(): void {
  console.log(this.selectedTaskId,'selectedTaskIdselectedTaskId');

  if (!this.selectedFilesReport || this.selectedFilesReport.length === 0 || !this.selectedTaskId) {
    this.snackBarServ.openSnackBarFromComponent({
      message: 'No files selected or task ID missing.',
      duration: 2500,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure']
    });
    return;
  }

  const formData = new FormData();

  for (const file of this.selectedFilesReport) {
    formData.append('files', file);
  }

  formData.append('subTaskId', this.selectedTaskId);

  this.projectServ.saveuploadfile(formData).subscribe({
    next: () => {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Files uploaded successfully.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-success']
      });

      this.selectedFilesReport = [];
      this.getsubTaskDetailsAll(); // ✅ call your method here

      // 🔁 Refresh uploaded files list
      this.getSubuploadfiles();

      // ❌ Close the dialog
      this.dialog.closeAll(); // closes all open dialogs
    },
    error: () => {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'File upload failed.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure']
      });
    }
  });
}

//existingFilesReport: any[] = [];

getSubuploadfiles(): void {
  console.log(this.selectedTaskId, 'selectedtaskid');

  this.projectServ.getSubtaskuploadfile(this.selectedTaskId).subscribe((res: any) => {
    console.log(res, 'upload files response');

    if (Array.isArray(res.data)) {
      this.existingFilesReport = res.data;
      } else if (res.data) {
        this.existingFilesReport = [res.data];
    } else {
      this.existingFilesReport = []; // in case it's a single object
    }
  });
}
DeleteUploadfile(fileId: number): void {
  this.projectServ.deleteuploadfile(fileId).subscribe({
    next: (res: any) => {
      const isSuccess = res?.status === 'success';
      const message = res?.message || (isSuccess ? 'File deleted successfully.' : 'Failed to delete file.');

      // Show snackbar
      this.snackBarServ.openSnackBarFromComponent({
        message: message,
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure']
      });

      if (isSuccess) {
        // Refresh file list after deletion
        this.getSubuploadfiles();
      }
    },
    error: (err) => {
      console.error('Error deleting file:', err);
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Something went wrong while deleting the file.',
        duration: 2500,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure']
      });
    }
  });
}

 // Added by Manoj Madiraju for helpers for comments

  // Used in template: *ngIf="isOwner(c)"
  isOwner(c: any): boolean {
    const ownerId = c && (c.userId || c.userid || c.updatedby || c.updatedBy || c.createdBy || c.createdby);
    return String(ownerId) === String(this.currentUserId);
  }

  // 🔢 Helpers to ensure we never call APIs with a temp string id
  private toNumericId(id: any): number | null {
    const n = Number(id);
    return Number.isFinite(n) ? n : null;
  }
  isNumericId(id: any): boolean {
    return this.toNumericId(id) != null;
  }

  /** ✅ Valid numeric id (> 0) required by backend */
  private validNumericId(id: any): number | null {
    const n = Number(id);
    return Number.isFinite(n) && n > 0 ? n : null;
  }

  // Click "✏️" → switch a single row into edit mode
  onEditComment(c: any): void {
    // Guard: don't allow editing until the real numeric id is known
    const numericId = this.validNumericId(c?.id);
    if (numericId == null) {
      this.snackBarServ.openSnackBarFromComponent({
      message: 'Syncing your comment… please try again in a moment.',
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure'],
    });
    this.getcommentSubTask(); // fetch to get real id
    return;
    }
    this.editingCommentId = numericId; // requires id in commentsList (you added it)
    this.editCommentText = c.description || '';   // seed with current text
  }

  // Save edited comment
  saveEditedComment(c: any): void {
    const text = (this.editCommentText || '').trim();
    if (!text) {
    // if (!this.editCommentText.trim()) {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'Comment cannot be empty.',
        duration: 2000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure'],
      });
      return;
    }

    const updatedby = Number(localStorage.getItem('profileId'));
    const numericId = this.validNumericId(c?.id);
    if (numericId == null) {
      this.snackBarServ.openSnackBarFromComponent({
        message: 'One moment… syncing this comment before editing.',
        duration: 2000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure'],
      });
    this.getcommentSubTask(); // fetch to get real id
    return;
    }

    this.projectServ.updateSubTaskComment(numericId, text, updatedby).subscribe({
      next: (res: any) => {
        this.snackBarServ.openSnackBarFromComponent({
          message: res?.message || 'Comment updated.',
          duration: 2500,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-success'],
        });

        // update local list so UI reflects immediately
        const idx = this.commentsList.findIndex((x: any) => String(x.id) === String(numericId));
        if (idx > -1) {
          this.commentsList[idx] = {
            ...this.commentsList[idx],
            description: text,
          };
        }

        this.editingCommentId = null;
        this.editCommentText = '';
      },
      error: (err: any) => {
        this.snackBarServ.openSnackBarFromComponent({
          message: err?.error?.message || 'Failed to update comment.',
          duration: 2500,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        });
      }
    });
  }

  // Cancel edit
  cancelEdit(): void {
    this.editingCommentId = null;
    this.editCommentText = '';
  }

  // Click "🗑️" → confirm, then delete
  onDeleteComment(c: any): void {
    const dataToBeSentToDailog = {
      title: 'Delete Comment',
      message: 'Are you sure you want to delete this comment?',
      confirmText: 'Yes',
      cancelText: 'No',
      actionName: 'delete-Project'
    };

    const dialogConfig = this.getDialogConfigData(dataToBeSentToDailog, { delete: true, edit: false, add: false });
    const dialogRef = this.dialogServ.openDialogWithComponent(CommonDeleteComponent, dialogConfig);

    dialogRef.afterClosed().subscribe({
      next: () => {
        if (dialogRef.componentInstance.allowAction) {
          const updatedby = Number(localStorage.getItem('profileId'));
          const numericId = this.validNumericId(c?.id);
          if (numericId == null) {
            this.snackBarServ.openSnackBarFromComponent({
              message: 'Syncing comment… try again in a second.',
              duration: 2000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['custom-snack-failure'],
            });
            this.getcommentSubTask();
            return;
          }
          this.projectServ.deleteSubTaskComment(numericId, updatedby).subscribe({
            next: (res: any) => {
              const ok = String(res?.status || '').toLowerCase() === 'success';

              if (!ok) {
                this.snackBarServ.openSnackBarFromComponent({
                message: res?.message || 'Delete failed.',
                duration: 2500,
                verticalPosition: 'top',
                horizontalPosition: 'center',
                panelClass: ['custom-snack-failure'],
                });
                return;
              }
              this.snackBarServ.openSnackBarFromComponent({
                message: res?.message || 'Comment deleted.',
                duration: 2500,
                verticalPosition: 'top',
                horizontalPosition: 'center',
                panelClass: ['custom-snack-success'],
              });
              // remove from UI
              this.commentsList = this.commentsList.filter((x: any) => String(x.id) !== String(numericId));
              this.bumpCommentCount(Number(this.selectedTaskId), -1);
              this.getcommentSubTask(); // authoritative resync (keeps count exact)
            },
            error: (err: any) => {
              this.snackBarServ.openSnackBarFromComponent({
                message: err?.error?.message || 'Failed to delete comment.',
                duration: 2500,
                verticalPosition: 'top',
                horizontalPosition: 'center',
                panelClass: ['custom-snack-failure'],
              });
            }
          });
        }
      }
    });
  }

  // Ensures a just-posted comment shows buttons immediately
  private pushNewCommentToTop(newComment: any): void {
    const hasOwnerField =
      'userId' in newComment ||
      'userid' in newComment ||
      'updatedby' in newComment ||
      'updatedBy' in newComment ||
      'createdBy' in newComment ||
      'createdby' in newComment;

    if (!hasOwnerField) {
      (newComment as any).userId = this.currentUserId;
    }

    // ✅ Guarantee a visible FULL NAME for optimistic entries (never "You")
    if (!newComment.fullname || !String(newComment.fullname).trim()) {
      newComment.fullname = this.currentUserDisplayName || '';
    }

    this.commentsList = [newComment, ...(this.commentsList || [])];
  }

  // Read current user id (adjust claim names if your token differs)
  private getCurrentUserId(): string | number {
    const token = localStorage.getItem('token');
    if (!token) return localStorage.getItem('profileId') || '';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return (
        payload.userId ||
        payload.userid ||
        payload.id ||
        payload.sub ||
        localStorage.getItem('profileId') ||
        ''
      );
    } catch {
      return localStorage.getItem('profileId') || '';
    }
  }

  // Tries many common localStorage/sessionStorage keys and JWT claims.
  private resolveCurrentUserDisplayName(): string {
    const tryKeys = (store: Storage, keys: string[]) => {
      for (const k of keys) {
        const v = (store.getItem(k) || '').trim();
        if (v) return v;
      }
      return '';
    };

    // 1) Direct single-field names commonly used in apps
    const direct = tryKeys(localStorage, [
      'fullName', 'fullname', 'profileName', 'displayName',
      'userFullName', 'user_full_name', 'userfullname',
      'name', 'username', 'userName'
    ]) || tryKeys(sessionStorage, [
      'fullName', 'fullname', 'profileName', 'displayName',
      'userFullName', 'user_full_name', 'userfullname',
      'name', 'username', 'userName'
    ]);
    if (direct) return direct;

    // 2) First/Last combos from storage
    const first =
      tryKeys(localStorage, ['firstName', 'firstname', 'given_name']) ||
      tryKeys(sessionStorage, ['firstName', 'firstname', 'given_name']);
    const last =
      tryKeys(localStorage, ['lastName', 'lastname', 'family_name']) ||
      tryKeys(sessionStorage, ['lastName', 'lastname', 'family_name']);
    const combo = [first, last].filter(Boolean).join(' ').trim();
    if (combo) return combo;

    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const jwtDirect =
          (payload.fullName && String(payload.fullName).trim()) ||
          (payload.name && String(payload.name).trim()) ||
          [payload.given_name, payload.family_name].filter(Boolean).join(' ').trim();
        if (jwtDirect) return String(jwtDirect).trim();
      } catch { /* ignore */ }
    }
    return '';
  }

  // Small util to format yyyy-MM-dd (to match your UI)
  private formatDate(d: Date): string {
    const yyyy = d.getFullYear();
    const mm = (d.getMonth() + 1).toString().padStart(2, '0');
    const dd = d.getDate().toString().padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }


}
