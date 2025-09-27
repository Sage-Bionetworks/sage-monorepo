// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { MedianExpression } from '@sagebionetworks/agora/api-client';
import { HelperService } from '@sagebionetworks/agora/services';
import { geneMock1 } from '@sagebionetworks/agora/testing';
import { MedianBarChartComponent } from './median-barchart.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
const XAXIS_LABEL = 'BRAIN REGION';
const YAXIS_LABEL = 'LOG2CPM';
const FULL_MOCK_DATA = geneMock1.median_expression;
const TISSUES = ['TCX', 'PHG', 'STG'];
const SMALL_MOCK_DATA = [
  {
    min: -2.54173091337051,
    first_quartile: -1.03358030635935,
    median: 0.483801733963266,
    mean: -0.517398199667356,
    third_quartile: -0.0800759845652829,
    max: 2.32290808871289,
    tissue: TISSUES[0],
  },
  {
    min: -2.44077907413711,
    first_quartile: -0.592671867557559,
    median: 0.013739530502129,
    mean: -0.0324143336865,
    third_quartile: 0.49577213202412,
    max: 2.23019575245731,
    tissue: TISSUES[1],
  },
  {
    min: -5.03189866356294,
    first_quartile: -1.02644563959975,
    median: 0.176348063122062,
    mean: -0.323038107200895,
    third_quartile: 0.391874711168331,
    max: 1.9113258251877,
    tissue: TISSUES[2],
  },
];

describe('Component: BarChart - Median', () => {
  let fixture: ComponentFixture<MedianBarChartComponent>;
  let component: MedianBarChartComponent;
  let element: HTMLElement;

  beforeEach(waitForAsync(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [HelperService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MedianBarChartComponent);
    component = fixture.componentInstance;
  });

  const setUp = (
    data: MedianExpression[] = FULL_MOCK_DATA,
    xAxisLabel: string = XAXIS_LABEL,
    yAxisLabel: string = YAXIS_LABEL,
  ) => {
    component.data = data;
    component.xAxisLabel = xAxisLabel;
    component.yAxisLabel = yAxisLabel;

    fixture.detectChanges();
    element = fixture.nativeElement;
    const chart = element.querySelector('svg g');
    return { chart };
  };

  it('should create', () => {
    setUp();
    expect(component).toBeTruthy();
  });

  it('should not render the chart if there is no data', () => {
    const { chart } = setUp([]);

    expect(component.data?.length).toEqual(0);
    expect(element.querySelector('.chart-no-data')).toBeTruthy();

    expect(chart).toBeFalsy();
  });

  it('should not render the chart if all values are negative', () => {
    const { chart } = setUp(
      SMALL_MOCK_DATA.map((obj) => {
        return { ...obj, median: -1 * obj.median };
      }),
    );

    expect(element.querySelector('.chart-no-data')).toBeTruthy();

    expect(chart).toBeFalsy();
  });

  it('should render the chart if there is positive data', () => {
    const createChartSpy = jest.spyOn(component, 'createChart');
    const { chart } = setUp();

    expect(component.data?.length).toEqual(FULL_MOCK_DATA.length);
    expect(createChartSpy).toHaveBeenCalled();
    expect(chart).toBeTruthy();

    expect(element.querySelector('.chart-no-data')).toBeFalsy();
  });

  it('should render the chart axes', () => {
    const { chart } = setUp();

    expect(chart?.querySelector('.x-axis')).toBeTruthy();
    expect(chart?.querySelector('.y-axis')).toBeTruthy();
  });

  it('should render the correct number of bars', () => {
    const { chart } = setUp();

    expect(chart?.querySelectorAll('rect').length).toEqual(FULL_MOCK_DATA.length);
  });

  it('should not render bars for negative values', () => {
    const { chart } = setUp([
      { ...SMALL_MOCK_DATA[0], median: -0.01 },
      ...SMALL_MOCK_DATA.slice(1),
    ]);

    expect(chart?.querySelectorAll('rect').length).toEqual(SMALL_MOCK_DATA.length - 1);
  });

  it('should render the bar labels', () => {
    const { chart } = setUp();

    expect(chart?.querySelectorAll('text.bar-labels').length).toEqual(FULL_MOCK_DATA.length);
  });

  it('should render the labels for both axes', () => {
    const { chart } = setUp();

    expect(chart?.querySelector('.x-axis-label')?.textContent).toEqual(XAXIS_LABEL);
    expect(chart?.querySelector('.y-axis-label')?.textContent).toEqual(YAXIS_LABEL);
  });

  it('should render the meaningful expression threshold when values are larger than threshold', () => {
    const { chart } = setUp();

    expect(chart?.querySelector('.meaningful-expression-threshold-line')).toBeTruthy();
  });

  it('should not render the meaningful expression threshold when all values are smaller than threshold', () => {
    const { chart } = setUp(SMALL_MOCK_DATA);

    expect(chart?.querySelector('.meaningful-expression-threshold-line')).toBeFalsy();
  });

  it('should alphabetize the x-axis values', () => {
    setUp(SMALL_MOCK_DATA);

    const sortedTissues = TISSUES.sort();
    const xAxisTicks = element.querySelector('svg g .x-axis')?.querySelectorAll('.tick');
    xAxisTicks?.forEach((val, index) => {
      expect(val.textContent).toEqual(sortedTissues[index]);
    });
  });

  it('should show and hide tooltip', () => {
    setUp();

    const tooltip = element.querySelector<HTMLElement>('#tooltip');
    expect(tooltip?.textContent).toBeFalsy();

    const xAxisTick = element.querySelector('svg g .x-axis .tick');
    const mouseEnterEvent = new MouseEvent('mouseenter', {
      bubbles: true,
      cancelable: true,
    });
    xAxisTick?.dispatchEvent(mouseEnterEvent);

    expect(tooltip?.style.display).toEqual('block');
    expect(tooltip?.textContent).toBeTruthy();

    const mouseLeaveEvent = new MouseEvent('mouseleave', {
      bubbles: true,
      cancelable: true,
    });
    xAxisTick?.dispatchEvent(mouseLeaveEvent);

    expect(tooltip?.style.display).toEqual('none');
  });
});
