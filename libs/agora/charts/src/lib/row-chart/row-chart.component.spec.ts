// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from '@sagebionetworks/agora/services';
import { rowChartItemMock } from '@sagebionetworks/agora/testing';
import { RowChartComponent } from './row-chart.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Chart - Row', () => {
  let fixture: ComponentFixture<RowChartComponent>;
  let component: RowChartComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(RowChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display message if not data', () => {
    expect(component.data?.length).toEqual(0);
    expect(element.querySelector('.chart-no-data')).toBeTruthy();
  });

  it('should render the chart', () => {
    const idSpy = jest.spyOn(component, 'initData');
    const icSpy = jest.spyOn(component, 'initChart');

    component.data = [rowChartItemMock];
    fixture.detectChanges();

    expect(idSpy).toHaveBeenCalled();
    expect(icSpy).toHaveBeenCalled();
    expect(element.querySelector('svg')).toBeTruthy();
  });

  it('should have tooltips', () => {
    component.data = [rowChartItemMock];
    fixture.detectChanges();
    expect(document.querySelector('.row-chart-x-axis-tooltip')).toBeTruthy();
    expect(document.querySelector('.row-chart-value-tooltip')).toBeTruthy();
  });
});
