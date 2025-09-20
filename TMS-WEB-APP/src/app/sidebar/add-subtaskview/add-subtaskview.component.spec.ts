import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddSubtaskviewComponent } from './add-subtaskview.component';

describe('AddSubtaskviewComponent', () => {
  let component: AddSubtaskviewComponent;
  let fixture: ComponentFixture<AddSubtaskviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddSubtaskviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddSubtaskviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
