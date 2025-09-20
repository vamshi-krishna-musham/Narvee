import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterManagmentComponent } from './register-managment.component';

describe('RegisterManagmentComponent', () => {
  let component: RegisterManagmentComponent;
  let fixture: ComponentFixture<RegisterManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterManagmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
