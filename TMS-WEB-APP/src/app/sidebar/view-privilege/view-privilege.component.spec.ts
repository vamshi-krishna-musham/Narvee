import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewPrivilegeComponent } from './view-privilege.component';

describe('ViewPrivilegeComponent', () => {
  let component: ViewPrivilegeComponent;
  let fixture: ComponentFixture<ViewPrivilegeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewPrivilegeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewPrivilegeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
