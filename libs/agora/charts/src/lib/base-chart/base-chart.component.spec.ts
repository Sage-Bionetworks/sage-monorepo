import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BaseChartComponent } from './base-chart.component';

describe('Component: Chart - Base', () => {
  let fixture: ComponentFixture<BaseChartComponent>;
  let component: BaseChartComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(BaseChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
