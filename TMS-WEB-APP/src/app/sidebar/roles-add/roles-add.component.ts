import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
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
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AddTeamMemberComponent } from '../sidenav/add-team-member/add-team-member.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AddTaskviewComponent } from '../add-taskview/add-taskview.component';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { IConfirmDialogData } from '../sidenav/projects/confirm-dialog-data';
import { DialogService } from '../../PathService/dialog.service';
import { CommonDeleteComponent } from '../common-delete/common-delete.component';
import { AddNewRoleComponent } from '../add-new-role/add-new-role.component';
import { RoleService } from '../../services/role.service';
interface Role {
  serialNum: number;
  roleName: string;
  createddate: string;
  description: string;
}

@Component({
  selector: 'app-roles-add',
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
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
  ],
  templateUrl: './roles-add.component.html',
  styleUrl: './roles-add.component.scss'
})
export class RolesAddComponent {
  private destroyed$ = new Subject<void>();
  totalItems: any;
  pageEvent!: PageEvent;
  pageSize = 10;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  pageSizeOptions = [5, 10, 25];
  currentPageIndex = 0;
  itemsPerPage = 10;
  sortField = 'rolename';
  sortOrder = 'asc';
  field = "";
  dataTobeSentToSnackBarService: ISnackBarData = {
    message: '',
    duration: 2500,
    verticalPosition: 'top',
    horizontalPosition: 'center',
    direction: 'above',
    panelClass: ['custom-snack-success'],
  };
  private dialogServ = inject(DialogService);

  private snackBarServ = inject(SnackbarService);
  dataSource = new MatTableDataSource<Role>();

  constructor(private router: Router, private dialog: MatDialog, private service: RoleService) { }
  ngOnInit() {
    this.getRoles()
  }
  navigateToPrivilege(roleName: string, roleid: string): void {
    this.router.navigate(['/view-privilege'], {
      queryParams: {
        roleName: roleName,
        roleid: roleid
      }
    });
  }

  dataTableColumns = ['SerialNum', 'roleName','description', 'addedBy','updatedBy','Action'];

  addRole() {
    const dialogRef = this.dialog.open(AddNewRoleComponent, {
      width: '100%',
      maxWidth: '700px',
      disableClose: true,
      // data: {
      //   projectId: this.projectId,
      //   pid: this.pid
      // }
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result === true) {
        this.getRoles(); // refresh table data
      }
    });
  }
  roleList: any

getRoles(pagIdx = 1): void {
  const profileId = localStorage.getItem('profileId');
  const adminId = localStorage.getItem('adminId');
  const profileRole = localStorage.getItem('profileRole'); // 👈 Get the role
  const finalAdminId = adminId ? adminId : profileId;

  const pagObj = {
    pageNo: pagIdx,
    pageSize: this.itemsPerPage,
    sortField: this.sortField,
    sortOrder: this.sortOrder,
    keyword: this.field || 'empty',
    adminId: finalAdminId
  };

  this.service.getRole(pagObj).subscribe({
    next: (res: any) => {
      this.totalItems = res.data.totalElements;

      let roles = res.data.content;

      // ✅ Exclude "Super Admin" if profileRole is Admin
      if (profileRole === 'Admin') {
        roles = roles.filter((x: any) => x.rolename !== 'Super Admin');
      }

      this.roleList = roles.map((x: any, index: number) => ({
        serialNum: index + 1,
        roleName: x.rolename,
        createddate: x.createddate,
        description: x.description,
        roleid: x.roleid,
         addedByName:x.addedByName,
        updatedByName: x.updatedByName
      }));

      this.dataSource = new MatTableDataSource(this.roleList);

      // Set custom filter predicate
      this.dataSource.filterPredicate = (data: any, filter: string) =>
        data.roleName.trim().toLowerCase().includes(filter.trim().toLowerCase());
    },
    error: (err: any) => {
      console.error('Error fetching roles:', err);
      this.roleList = [];
      this.dataSource = new MatTableDataSource<Role>([]);
    }
  });
}

  handlePageEvent(event: PageEvent) {
    this.pageSize = event.pageSize;
    this.itemsPerPage = event.pageSize;
    this.currentPageIndex = event.pageIndex;
    this.getRoles(this.currentPageIndex + 1); // 1-based index for API
  }
  searchTimeout: any;


  applyFilter() {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.currentPageIndex = 0;
      this.getRoles(1);
    }, 300); // Delay in ms  }
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

  edittaskview(role: any) {
    const dialogRef = this.dialog.open(AddNewRoleComponent, {
      width: '100%',
      maxWidth: '700px',
      disableClose: true,
      data: {
        roleid: role.roleid,
        rolename: role.roleName,
        roledescription: role.description,
       

      }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.getRoles(); // refresh table data
      }
    });
  }
  sortData(event: Sort) {
    this.sortField = event.active;
    this.sortOrder = event.direction || 'asc';
    this.getRoles(this.currentPageIndex + 1);
  }
  toTitleCase(str: string): string {
  return str.replace(/\w\S*/g, txt => txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase());
}
  deleterole(role: any) {
    console.log(role, 'roleee');

    const dataToBeSentToDailog: Partial<IConfirmDialogData> = {
      title: 'Confirmation',
      message: `Are you sure you want to delete the role "${this.toTitleCase(role.roleName)}"?`, // Optional enhancement
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
          console.log('Deleting role with ID:', role.roleid); // Confirm the ID
          this.service.deleteRole(role.roleid).pipe(takeUntil(this.destroyed$)).subscribe({
            next: (response: any) => {
              if (response.status === 'success') {
                this.getRoles();
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-success'];
                this.dataTobeSentToSnackBarService.message = response.message || 'Role deleted successfully';
              } else {
                this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
                this.dataTobeSentToSnackBarService.message = response.message || 'Record deletion failed';
              }

              this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
            },

            error: (err: any) => {
              this.dataTobeSentToSnackBarService.panelClass = ['custom-snack-failure'];
              this.dataTobeSentToSnackBarService.message = err.message || 'Something went wrong';
              this.snackBarServ.openSnackBarFromComponent(this.dataTobeSentToSnackBarService);
            },
          });
        }
      },
    });
  }


}
