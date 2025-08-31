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
import { identity, Subject, takeUntil } from 'rxjs';
import { ProjectsService } from '../../services/projects.service';
import { MatDialog, MatDialogConfig, MatDialogModule } from '@angular/material/dialog';
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
import { AddPrivilegeComponent } from '../add-privilege/add-privilege.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { RoleService } from '../../services/role.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';


interface Privilege {
  serialNum: number;
  roleName: string;
  createddate: string;
  description: string;
}

@Component({
  selector: 'app-view-privilege',
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
        MatDialogModule,
        MatCheckboxModule,
        MatProgressSpinnerModule
      ],
  templateUrl: './view-privilege.component.html',
  styleUrl: './view-privilege.component.scss'
})
export class ViewPrivilegeComponent {
  roleName: string = '';
  roleid:any
  dataSource = new MatTableDataSource<any>([
  
  ]);
  dataTableColumns: string[] = ['SerialNum', 'Privileges'];
  private snackBarServ = inject(SnackbarService);

  constructor(private router: Router,private route: ActivatedRoute,private dialog: MatDialog , private service :RoleService

  ) {}
  privilegelist:any
  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.roleName = params['roleName'];
      this.roleid = params['roleid'];
  
      console.log('Received roleName:', this.roleName);
      console.log('Received roleid:', this.roleid);
  
      // Call your service to load privileges based on roleid
      this.getPrivilegesForRole(this.roleid);
      this.getPrivileges()
    });
  }
  getPrivilegesForRole(roleid: string): void {
    this.service.getPrivilegesbyID(roleid).subscribe({
      next: (res: any) => {
        console.log(res.data, 'Fetched Roles');
  console.log(res);
  
        this.privilegelist = res.data.map((x: any, index: number) => ({
          serialNum: index + 1,
          roleName: x.rolename,
          createddate: x.createddate,
          description: x.description,
          roleid: x.roleid
        }));
  
        this.dataSource = new MatTableDataSource(this.privilegelist);
      },
      error: (err: any) => {
        console.error('Error fetching roles:', err);
        this.privilegelist = [];
        this.dataSource = new MatTableDataSource<Privilege>([]);
      }
    });
  }
  fullPrivilegeData: any = {}; // Store full response for later use

  getPrivileges(): void {
    this.service.getPrivileges().subscribe({
      next: (res: any) => {
        console.log(res, 'Fetched Response');
        this.fullPrivilegeData = res.data; // Save the full data
  
        const arrays = ['projects', 'tasks', 'subTasks', 'teamMember'];
  
        this.privilegelist = arrays.map((key, index) => {
          const itemArray = res.data[key];
          return {
            serialNum: index + 1,
            ticketName: itemArray && itemArray.length > 0 ? itemArray[0].name : 'N/A',
            allItems: itemArray || [], // Save the full array to access remaining items
            keyName: key // Store the original array key
          };
        });
  
        this.dataSource = new MatTableDataSource(this.privilegelist);
      },
      error: (err: any) => {
        console.error('Error fetching privileges:', err);
        this.privilegelist = [];
        this.dataSource = new MatTableDataSource<Privilege>([]);
      }
    });
  }
  
  
  goBack(): void {
    this.router.navigate(['/roles-add']);
  }
  addPrivilege(){
    const dialogRef = this.dialog.open(AddPrivilegeComponent, {
      width: '100%',
      maxWidth: '500px',
      disableClose: true,
      // data: {
      //   projectId: this.projectId,
      //   pid: this.pid
      // }
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result === true) {
        this.getPrivileges(); // refresh table data
      }
    });
  }
  @ViewChild('roleDialog') roleDialogRef!: TemplateRef<any>;
selectedRoleName: string = '';
dummyAccessList: any[] = [];
allAccessChecked: boolean = false;





selectedCategory: string = '';

openDialog(roleName: string, keyName: string): void {
  this.selectedCategory = keyName;

  const arrayData = this.fullPrivilegeData[keyName];

  if (arrayData && arrayData.length > 0) {
    this.selectedRoleName = arrayData[0].name;

    this.dummyAccessList = arrayData.slice(1).map((item: any) => ({
      name: item.name,
      id: item.id,
      checked: false
    }));
  }

  this.service.getPrivilegesbyID(this.roleid).subscribe({
    next: (res: any) => {
      console.log('Fetched Privileges by Role ID:', res);

      const selectedItems = res?.data?.[keyName]?.filter((item: any) => item.selected);
      const selectedIds = selectedItems?.map((item: any) => item.id) || [];

      this.dummyAccessList = this.dummyAccessList.map(item => ({
        ...item,
        checked: selectedIds.includes(item.id)
      }));

      this.allAccessChecked = this.dummyAccessList.some(item => item.checked);

      this.dialog.open(this.roleDialogRef, {
        maxWidth: '400px',
        width: '100%'
      });
    },
    error: (err: any) => {
      console.error('Error fetching privileges by role ID:', err);
    }
  });
}

toggleAllAccess(): void {
  this.dummyAccessList.forEach(access => access.checked = this.allAccessChecked);
}

onIndividualCheckboxChange(): void {
  const anyChecked = this.dummyAccessList.some(access => access.checked);
  this.allAccessChecked = anyChecked;
}
isLoading:any
onsave(): void {
  this.isLoading = true;  // start loader

  // ✅ Update the selected values inside the fullPrivilegeData
  const categoryArray = this.fullPrivilegeData[this.selectedCategory];

  if (categoryArray) {
    this.dummyAccessList.forEach(updatedItem => {
      const index = categoryArray.findIndex((item: any) => item.id === updatedItem.id);
      if (index !== -1) {
        categoryArray[index].selected = updatedItem.checked;
      }
    });
  }

  // ✅ Now gather all selected privilege IDs from all categories
  const allCategories = Object.keys(this.fullPrivilegeData); // dynamic instead of hardcoded
  const selectedIds: number[] = [];

  allCategories.forEach(category => {
    const categoryArray = this.fullPrivilegeData[category];
    categoryArray.forEach((item: any) => {
      if (item.selected) {
        selectedIds.push(item.id);
      }
    });
  });

  const payload = {
    roleId: this.roleid,
    privilegeIds: selectedIds
  };

  this.service.SaveRolePrivilege(payload).subscribe({
    next: (res: any) => {
      this.isLoading = false; // stop loader

      const isSuccess = res.status === 'Success';

      const dataToBeSentToSnackBar: ISnackBarData = {
        message: res.message || (isSuccess ? 'Privileges updated successfully!' : 'Operation failed.'),
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure'],
      };

      this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);

      if (isSuccess) {
        this.dialog.closeAll(); // Close dialog if needed
      }
    },
    error: (err: any) => {
      this.isLoading = false; // stop loader

      const dataToBeSentToSnackBar: ISnackBarData = {
        message: err?.error?.message || 'Something went wrong. Please try again.',
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['custom-snack-failure'],
      };
      this.snackBarServ.openSnackBarFromComponent(dataToBeSentToSnackBar);
    }
  });
}


}
