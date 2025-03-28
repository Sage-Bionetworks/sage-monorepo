// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from '@sagebionetworks/agora/services';
import { geneMock1 } from '@sagebionetworks/agora/testing';
import { CandlestickChartComponent } from './candlestick-chart.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Chart - Candlestick', () => {
  let fixture: ComponentFixture<CandlestickChartComponent>;
  let component: CandlestickChartComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(CandlestickChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display message if not data', () => {
    expect(component.chartData?.length).toEqual(0);
    expect(element.querySelector('.chart-no-data')).toBeTruthy();
  });

  it('should render the chart', () => {
    const idSpy = jest.spyOn(component, 'initData');
    const icSpy = jest.spyOn(component, 'initChart');

    component.gene = geneMock1;
    fixture.detectChanges();

    expect(idSpy).toHaveBeenCalled();
    expect(icSpy).toHaveBeenCalled();
    expect(element.querySelector('svg')).toBeTruthy();
  });

  it('should have tooltips', () => {
    component.gene = geneMock1;
    fixture.detectChanges();
    expect(document.querySelector('.candlestick-chart-x-axis-tooltip')).toBeTruthy();
    expect(document.querySelector('.candlestick-chart-value-tooltip')).toBeTruthy();
  });
});
