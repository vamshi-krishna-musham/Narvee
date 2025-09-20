import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTaskviewComponent } from './add-taskview.component';

describe('AddTaskviewComponent', () => {
  let component: AddTaskviewComponent;
  let fixture: ComponentFixture<AddTaskviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddTaskviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddTaskviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
