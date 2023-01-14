import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { NgxTypedJsModule } from 'ngx-typed-js';

import { StatisticsViewerComponent } from './statistics-viewer.component';

describe('StatisticsViewerComponent', () => {
  let component: StatisticsViewerComponent;
  let fixture: ComponentFixture<StatisticsViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StatisticsViewerComponent],
      imports: [HttpClientModule, NgxTypedJsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StatisticsViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
