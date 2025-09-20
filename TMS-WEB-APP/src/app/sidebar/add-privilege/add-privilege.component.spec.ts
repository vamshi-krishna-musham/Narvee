import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPrivilegeComponent } from './add-privilege.component';

describe('AddPrivilegeComponent', () => {
  let component: AddPrivilegeComponent;
  let fixture: ComponentFixture<AddPrivilegeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddPrivilegeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddPrivilegeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
