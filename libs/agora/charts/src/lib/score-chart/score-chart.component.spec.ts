// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from '@sagebionetworks/agora/services';
import { distributionMock } from '@sagebionetworks/agora/testing';
import { ScoreChartComponent } from './score-chart.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Chart - Score', () => {
  let fixture: ComponentFixture<ScoreChartComponent>;
  let component: ScoreChartComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(ScoreChartComponent);
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
    const idSpy = jest.spyOn(component, 'initData');
    const icSpy = jest.spyOn(component, 'initChart');

    const distribution: any = [];

    distributionMock.overall_scores[0].bins.forEach((bin: number[], i: number) => {
      distribution.push({
        key: bin[0].toFixed(2),
        value: distributionMock.overall_scores[0].distribution[i],
        range: [bin[0], bin[1]],
      });
    });

    component.distribution = distribution;
    component.score = 1;
    fixture.detectChanges();

    expect(idSpy).toHaveBeenCalled();
    expect(icSpy).toHaveBeenCalled();
    expect(element.querySelector('svg')).toBeTruthy();
  });
});
