// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from '@sagebionetworks/agora/services';
import { geneMock1 } from '@sagebionetworks/agora/testing';
import { MedianChartComponent } from './median-chart.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Chart - Median', () => {
  let fixture: ComponentFixture<MedianChartComponent>;
  let component: MedianChartComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(MedianChartComponent);
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

    component.data = geneMock1.median_expression;
    fixture.detectChanges();

    expect(idSpy).toHaveBeenCalled();
    expect(icSpy).toHaveBeenCalled();
    expect(element.querySelector('svg')).toBeTruthy();
  });

  it('should have tooltips', () => {
    component.data = geneMock1.median_expression;
    component.addXAxisTooltips();
    fixture.detectChanges();
    expect(document.querySelector('.median-chart-x-axis-tooltip')).toBeTruthy();
  });
});
