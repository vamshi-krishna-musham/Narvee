import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaveNavComponent } from './leave-nav.component';

describe('LeaveNavComponent', () => {
  let component: LeaveNavComponent;
  let fixture: ComponentFixture<LeaveNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LeaveNavComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LeaveNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
