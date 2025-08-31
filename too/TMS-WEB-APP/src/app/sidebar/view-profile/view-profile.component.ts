import { Component, ElementRef, inject, TemplateRef, ViewChild } from '@angular/core';
import { TaskmanagementService } from '../../services/taskmanagement.service';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ISnackBarData, SnackbarService } from '../../PathService/snack-bar.service';
import { Router } from '@angular/router';
import { PhoneNumber } from 'google-libphonenumber';

@Component({
  selector: 'app-view-profile',
  templateUrl: './view-profile.component.html',
  styleUrl: './view-profile.component.scss'
})
export class ViewProfileComponent {


  firstName: string = '';
  lastName: string = '';
  Email: string = '';
  Role: string = '';

  constructor(private service: TaskmanagementService, private dialog: MatDialog, private router: Router) { }
  private snackBarServ = inject(SnackbarService);

  ngOnInit() {
    const role = localStorage.getItem('profileRole');
    this.profileRole = role || '';
    this.firstName = localStorage.getItem('firstName') || '';
    this.lastName = localStorage.getItem('lastName') || '';
    this.Email = localStorage.getItem('Email') || '';
    this.Role = localStorage.getItem('profileRole') || '';
    const userId = localStorage.getItem('profileId')
    this.TeamMembergetById(userId)
  }
  organizationNameValidator(value: string): string | null {
    if (!value || value.trim() === '') {
      return `${this.fieldLabels['organizationName']} cannot be only whitespace.`;
    }

    const hasLetter = /[a-zA-Z]/.test(value);
    if (!hasLetter) {
      return `${this.fieldLabels['organizationName']} must contain at least one letter.`;
    }

    return null;
  }
  // Allow only numeric keypresses (0–9)
  allowOnlyNumber(event: KeyboardEvent) {
    const pattern = /[0-9]/;
    const inputChar = String.fromCharCode(event.keyCode || event.which);
    if (!pattern.test(inputChar)) {
      event.preventDefault();
    }
  }
  onlyNumbersPattern: RegExp = /^[0-9]+$/;


  getPattern(field: string): string | RegExp {
    switch (field) {
      case 'firstName':
      case 'lastName':
        return this.onlyLettersPattern;
      case 'middleName':
        return this.optionalLettersPattern;
      case 'email':
        return this.emailPattern;
      case 'companySize':
        return this.onlyNumbersPattern;
      default:
        return /.*/; // fallback pattern
    }
  }

  dataTobeSentToSnackBarService: ISnackBarData = {
    message: '',
    duration: 2500,
    verticalPosition: 'top',
    horizontalPosition: 'center',
    direction: 'above',
    panelClass: ['custom-snack-success'],
  };
  dialogRef!: MatDialogRef<any>;

  profileImageUrl: string = 'assets/profileimage.jpg';
  hasProfileImage: boolean = false;
  userProfile: { [key: string]: string } = {
    firstName: '',
    middleName: '',
    lastName: '',
    organisationName: '',
    companySize: '',
    OrganisationEmail: '',
    companyDomain: '',
    industry: '',
    RoleName: ''
  };
  roleId: any
  editableProfile = { ...this.userProfile }; // copy for editing
  isEditing = false;
  originalProfile = {};
  TeamMembergetById(userId: any): void {
    this.service.TeamMemberGetId(userId).subscribe((res: any) => {
      const data = res?.data;

      if (data?.profilePhoto) {
        this.profileImageUrl = `data:image/jpeg;base64,${data.profilePhoto}`;
        this.hasProfileImage = true;
      } else {
        this.profileImageUrl = 'assets/profileimage.jpg';
        this.hasProfileImage = false;
      }

      this.roleId = data?.role?.roleid;

      // Get values from localStorage
      const organizationName = localStorage.getItem('organizationName') || '';
      const companySize = localStorage.getItem('companySize') || '';
      const companyDomain = localStorage.getItem('companyDomain') || '';
      const industry = localStorage.getItem('industry') || '';

      this.userProfile = {
        firstName: data?.firstName || '',
        middleName: data?.middleName || '',
        lastName: data?.lastName || '',
        organizationName: organizationName,
        companySize: companySize,
        email: data?.email || '',
        companyDomain: companyDomain,
        industry: industry,
        RoleName: data?.role?.rolename || ''
      };

      console.log(this.userProfile, 'userprofileee');

      this.editableProfile = { ...this.userProfile };
      this.originalProfile = { ...this.userProfile };
    });
  }
  toTitleCase(value: string, field: string): string {
    if (!value) return '-';

    // Skip transforming email to Title Case
    if (field === 'email') {
      return value;
    }

    // Convert to Title Case
    return value
      .toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }


  fieldLabels: { [key: string]: string } = {
    firstName: 'First Name',
    middleName: 'Middle Name',
    lastName: 'Last Name',
    organizationName: 'Organization Name',
    companySize: 'Organisation Size',
    email: 'Organization Email',
    companyDomain: 'Organisation Website',
    industry: 'Industry',
    RoleName: 'Role Name'
  };

  hovering = false;

  enableEdit() {
    this.isEditing = true;
  }

  getMinLength(field: string): number {
    const constraints: { [key: string]: number } = {
      firstName: 2,
      middleName: 2,
      lastName: 2,
      organizationName: 2,
    };
    return constraints[field] || 0;
  }

  getMaxLength(field: string): number {
    const constraints: { [key: string]: number } = {
      firstName: 30,
      middleName: 30,
      lastName: 30,
      organizationName: 50,
    };
    return constraints[field] || 100;
  }
  isSuperAdmin(): boolean {
    return this.profileRole === 'Super Admin';
  }

  profileRole: string = '';



  cancelEdit() {
    this.editableProfile = { ...this.originalProfile };
    this.isEditing = false;
  }
  onlyLettersPattern: RegExp = /^[A-Za-z]+$/; // For firstName and lastName
  optionalLettersPattern: RegExp = /^[A-Za-z]*$/; // For middleName (optional)
  // orgNamePattern: RegExp = /^(?=.*[A-Za-z])[^\s]+$/; // No spaces, at least one letter
  emailPattern: RegExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]{2,}\.[a-zA-Z]{2,}$/; // Email validation
  saveProfile() {
    const requiredFields = ['firstName', 'lastName', 'organizationName', 'email'];

    // Length constraints
    const lengthConstraints: { [key: string]: { min: number; max: number } } = {
      firstName: { min: 2, max: 30 },
      middleName: { min: 2, max: 30 },
      lastName: { min: 2, max: 30 },
      organizationName: { min: 2, max: 50 },
    };

    for (let field of requiredFields) {
      let value = this.editableProfile[field]?.trim() || '';

      if (!value) {
        this.showError(`${this.fieldLabels[field]} is required.`);
        return;
      }

      const { min, max } = lengthConstraints[field] || {};
      if (min && value.length < min) {
        this.showError(`${this.fieldLabels[field]} must be at least ${min} characters.`);
        return;
      }
      if (max && value.length > max) {
        this.showError(`${this.fieldLabels[field]} must not exceed ${max} characters.`);
        return;
      }

      if ((field === 'firstName' || field === 'lastName') && !this.onlyLettersPattern.test(value)) {
        this.showError(`${this.fieldLabels[field]} must contain only letters and no spaces.`);
        return;
      }



      if (field === 'email' && !this.emailPattern.test(value)) {
        this.showError(`Enter a valid email address.`);
        return;
      }
      // Organization Name check
      if (field === 'organizationName') {
        const error = this.organizationNameValidator(value);
        if (error) {
          this.showError(error);
          return;
        }
      }
      if (field === 'organizationName') {
        const error = this.organizationNameValidator(value);
        if (error === 'whitespace') {
          this.showError('Organization Name cannot be empty or only whitespace.');
          return;
        }
        if (error === 'noLetter') {
          this.showError('Organization Name must contain at least one letter.');
          return;
        }
        if (error === 'invalidChars') {
          this.showError('Organization Name must contain only letters, numbers, and spaces.');
          return;
        }
      }

      this.editableProfile[field] = value;
    }

    // Validate middle name (optional)
    const middle = this.editableProfile['middleName']?.trim();
    if (middle) {
      const { min, max } = lengthConstraints['middleName'];
      if (middle.length < min) {
        this.showError(`Middle Name must be at least ${min} characters.`);
        return;
      }
      if (middle.length > max) {
        this.showError(`Middle Name must not exceed ${max} characters.`);
        return;
      }
      if (!this.optionalLettersPattern.test(middle)) {
        this.showError(`Middle Name must contain only letters.`);
        return;
      }
      this.editableProfile['middleName'] = middle;
    }

    // Convert email to lowercase
    this.editableProfile['email'] = this.editableProfile['email'].toLowerCase().trim();

    const userId = localStorage.getItem('profileId');
    const adminId = localStorage.getItem('adminId');
    const contactNumber = localStorage.getItem('contactNumber');
    const position = localStorage.getItem('position')
    // Prepare payload with corrected key
    const updatedData: any = {
      ...this.editableProfile,
      organisationName: this.editableProfile['organizationName'],
      updatedBy: userId,
      userId: userId,
      roleId: this.roleId,
      adminId: adminId || null,
      contactNumber: contactNumber,
      position: position
    };

    delete updatedData.organizationName;

    // API call
    this.service.updateTeamMember(updatedData).subscribe({
      next: (res: any) => {
        const success = res.status === 'success';
        this.snackBarServ.openSnackBarFromComponent({
          message: res.message || (success ? 'Profile updated successfully!' : 'Update failed.'),
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: [success ? 'custom-snack-success' : 'custom-snack-failure'],
        });

        if (success) {
          this.userProfile = { ...this.editableProfile };
          this.originalProfile = { ...this.editableProfile };
          this.isEditing = false;
        }
      },
      error: (err: any) => {
        this.showError(err?.error?.message || 'Something went wrong. Please try again.');
      }
    });
  }



  showError(message: string) {
    this.snackBarServ.openSnackBarFromComponent({
      message,
      duration: 3000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['custom-snack-failure'],
    });
  }



  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;


  openDialog(templateRef: TemplateRef<any>) {
    if (this.hasProfileImage && this.profileImageUrl) {
      this.imagePreview = this.profileImageUrl;
    } else {
      this.imagePreview = null;
    }

    this.dialogRef = this.dialog.open(templateRef, {
      maxWidth: '400px',
      height: '450px',
      width: '100%'
    });

    this.dialogRef.afterClosed().subscribe(() => {
      this.imagePreview = null;
      this.compressedFileToUpload = null;
      if (this.fileInput) {
        this.fileInput.nativeElement.value = '';
      }
    });
  }




  imagePreview: string | null = null;
  handleFileInput(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.previewImage(file);
      this.compressAndStoreImage(file);
    }
  }

  handleDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files?.[0];
    if (file) {
      this.previewImage(file);
      this.compressAndStoreImage(file); // optional
    }
  }

  previewImage(file: File) {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.imagePreview = e.target.result; // base64 data URL
    };
    reader.readAsDataURL(file);
  }

  removeImage() {
    this.imagePreview = null;
  }

  allowDrop(event: DragEvent) {
    event.preventDefault();
  }
  compressedFileToUpload: File | null = null;

  compressAndStoreImage(file: File) {
    const img = new Image();
    const reader = new FileReader();

    reader.onload = (e: any) => {
      img.src = e.target.result;

      img.onload = () => {
        const canvas = document.createElement('canvas');
        const CANVAS_SIZE = 300;

        const scale = Math.max(CANVAS_SIZE / img.width, CANVAS_SIZE / img.height);
        const scaledWidth = img.width * scale;
        const scaledHeight = img.height * scale;

        const offsetX = (CANVAS_SIZE - scaledWidth) / 2;
        const offsetY = (CANVAS_SIZE - scaledHeight) / 2;

        canvas.width = CANVAS_SIZE;
        canvas.height = CANVAS_SIZE;

        const ctx = canvas.getContext('2d');
        if (ctx) {
          ctx.fillStyle = '#fff';
          ctx.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

          ctx.drawImage(img, offsetX, offsetY, scaledWidth, scaledHeight);
        }

        canvas.toBlob((blob) => {
          if (blob) {
            this.compressedFileToUpload = new File([blob], file.name, {
              type: 'image/jpeg',
              lastModified: Date.now(),
            });

            this.imagePreview = canvas.toDataURL('image/jpeg');
          }
        }, 'image/jpeg', 0.8);
      };
    };

    reader.readAsDataURL(file);
  }





  uploadUserImage() {
    if (!this.compressedFileToUpload) {
      console.warn('No file selected.');
      return;
    }

    const userId = localStorage.getItem('profileId');
    const formData = new FormData();

    formData.append('photo', this.compressedFileToUpload);
    if (userId) {
      formData.append('id', userId);
    }

    this.service.uploadphoto(formData).subscribe({
      next: (res: any) => {
        const isSuccess = res.status === 'success';

        const snackBarData: ISnackBarData = {
          message: res.message || (isSuccess ? 'Profile photo uploaded successfully!' : 'Upload failed.'),
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: isSuccess ? ['custom-snack-success'] : ['custom-snack-failure'],
        };
        this.snackBarServ.openSnackBarFromComponent(snackBarData);

        // Reset
        this.compressedFileToUpload = null;
        this.imagePreview = null;

        // Refresh profile
        if (userId) {
          this.TeamMembergetById(userId);

        }

        // ✅ Close dialog
        if (isSuccess && this.dialogRef) {
          this.dialogRef.close();
        }
      },
      error: (err: any) => {
        const snackBarData: ISnackBarData = {
          message: err?.error?.message || 'Something went wrong during upload.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        };
        this.snackBarServ.openSnackBarFromComponent(snackBarData);
      }
    });
  }
  RemoveuploadedImage(): void {
    const userId = localStorage.getItem('profileId');

    if (!userId) return;

    this.service.removeuserphoto(userId).subscribe({
      next: (res: any) => {
        this.profileImageUrl = 'assets/profileimage.jpg';
        this.hasProfileImage = false;

        const snackBarData: ISnackBarData = {
          message: res?.message || 'Profile image removed successfully.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-success'],
        };
        this.snackBarServ.openSnackBarFromComponent(snackBarData);
      },
      error: (err: any) => {
        const snackBarData: ISnackBarData = {
          message: err?.error?.message || 'Failed to remove profile photo.',
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['custom-snack-failure'],
        };
        this.snackBarServ.openSnackBarFromComponent(snackBarData);
      }
    });
  }


  navigateToDashboard(): void {
    this.router.navigate(['/Dashboard']); // adjust the route as needed
  }



}
