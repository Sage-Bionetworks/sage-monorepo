// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from '@sagebionetworks/agora/services';
import { networkChartDataMock } from '@sagebionetworks/agora/testing';
import { NetworkChartComponent } from './network-chart.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Chart - Network', () => {
  let fixture: ComponentFixture<NetworkChartComponent>;
  let component: NetworkChartComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(NetworkChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display message if not data', () => {
    expect(component.data).not.toBeDefined();
    expect(element.querySelector('.chart-no-data')).toBeTruthy();
  });

  it('should render the chart', () => {
    const icSpy = jest.spyOn(component, 'initChart');

    component.data = networkChartDataMock;
    fixture.detectChanges();

    expect(icSpy).toHaveBeenCalled();
    expect(element.querySelector('svg')).toBeTruthy();
  });
});
