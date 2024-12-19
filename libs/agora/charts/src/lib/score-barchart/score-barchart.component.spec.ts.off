// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { ScoreBarChartComponent } from './';
import { HelperService } from '../../../../core/services';
import { distributionMock } from '../../../../testing';
import { ScoreData } from '../../../../models';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Chart - Score', () => {
  let fixture: ComponentFixture<ScoreBarChartComponent>;
  let component: ScoreBarChartComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ScoreBarChartComponent],
      imports: [RouterTestingModule],
      providers: [HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(ScoreBarChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display no data message if score is null', () => {
    const validValue1 = 0;
    const validValue2 = 1.5;

    expect(component.score).toEqual(null);
    expect(element.querySelector('.chart-no-data')).toBeTruthy();
    component.score = validValue1;
    fixture.detectChanges();
    expect(element.querySelector('.chart-no-data')).toBeFalsy();
    component.score = validValue2;
    fixture.detectChanges();
    expect(element.querySelector('.chart-no-data')).toBeFalsy();
  });

  it('should render the chart', () => {
    const data = distributionMock.overall_scores[0];
    
    component.score = 1;

    data.bins.forEach((item, index: number) => {
      if (!component._score)
        return;
      if (component._score >= item[0] && component._score < item[1]) {
        component.scoreIndex = index;
      }
    });
    data.distribution.forEach((item, index: number) => {
      component.chartData.push(
        {
          distribution: item,
          bins: component.data?.bins[index]
        } as ScoreData 
      );
    });

    fixture.detectChanges();
    expect(element.querySelector('svg')).toBeTruthy();
  });

  it('should set the correct scoreIndex to highlight the correct bar - middle', () => {
    const data = distributionMock.overall_scores[0];
    component.score = 1;
    component.setScoreIndex(data.bins);
    fixture.detectChanges();
    expect(component.scoreIndex).toBe(3);
  });

  it('should set the correct scoreIndex to highlight the correct bar - first bar', () => {
    const data = distributionMock.overall_scores[0];
    component.score = 0;
    component.setScoreIndex(data.bins);
    fixture.detectChanges();
    expect(component.scoreIndex).toBe(0);
  });

  it('should set the correct scoreIndex to highlight the correct bar - last bar', () => {
    const data = distributionMock.overall_scores[0];
    component.score = 3;
    component.setScoreIndex(data.bins);
    fixture.detectChanges();
    expect(component.scoreIndex).toBe(9);
  });
});
