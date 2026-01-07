import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { HelperService } from '@sagebionetworks/explorers/services';
import { heatmapDetailsPanelDataMock } from '@sagebionetworks/explorers/testing';
import { HeatmapDetailsPanelComponent } from './heatmap-details-panel.component';

describe('Component: Heatmap - Details Panel', () => {
  let fixture: ComponentFixture<HeatmapDetailsPanelComponent>;
  let component: HeatmapDetailsPanelComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [provideRouter([]), HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(HeatmapDetailsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
    component.show(new MouseEvent('click'), structuredClone(heatmapDetailsPanelDataMock));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have data', () => {
    expect(component.data()).toEqual(heatmapDetailsPanelDataMock);
  });

  it('should have label', () => {
    const label = element.querySelector('.heatmap-details-panel-label') as HTMLElement;

    expect(label).toBeTruthy();
    expect(label?.textContent.trim()).toEqual(heatmapDetailsPanelDataMock.label?.left as string);
  });

  it('should have heading', () => {
    const heading = element.querySelector('.heatmap-details-panel-heading') as HTMLElement;

    expect(heading).toBeTruthy();
    expect(heading?.textContent.trim()).toEqual(heatmapDetailsPanelDataMock.heading as string);
  });

  it('should have sub heading', () => {
    const subHeadings = element.querySelector('.heatmap-details-panel-sub-headings') as HTMLElement;

    expect(subHeadings).toBeTruthy();
    expect(subHeadings?.textContent.trim()).toEqual(
      heatmapDetailsPanelDataMock.subHeadings?.[0] as string,
    );
  });

  it('should have values', () => {
    const elements = fixture.debugElement.nativeElement.querySelectorAll(
      '.heatmap-details-panel-data > div > div',
    );
    expect(elements?.length).toEqual(4);

    expect(elements[0]?.textContent.trim()).toEqual(
      heatmapDetailsPanelDataMock.valueLabel?.toString(),
    );
    expect(elements[2]?.textContent.trim()).toEqual(heatmapDetailsPanelDataMock.value?.toString());
    expect(elements[3]?.textContent.trim()).toEqual(heatmapDetailsPanelDataMock.pValue?.toString());
  });
});
