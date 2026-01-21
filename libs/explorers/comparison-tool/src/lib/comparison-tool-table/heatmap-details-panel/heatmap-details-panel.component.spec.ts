import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import {
  ComparisonToolService,
  HelperService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { heatmapDetailsPanelDataMock } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { HeatmapDetailsPanelComponent } from './heatmap-details-panel.component';

async function setup(showPanel = true) {
  const renderResult = await render(HeatmapDetailsPanelComponent, {
    imports: [NoopAnimationsModule],
    providers: [provideRouter([]), HelperService, ...provideComparisonToolService()],
  });

  const fixture = renderResult.fixture;
  const component = fixture.componentInstance;
  const element = fixture.nativeElement;

  // Directly set the signal to show panel data (bypasses transform function)
  const service = fixture.debugElement.injector.get(ComparisonToolService);
  if (showPanel) {
    const mockTarget = document.createElement('div');
    const mockEvent = new MouseEvent('click', { bubbles: true });
    Object.defineProperty(mockEvent, 'target', { value: mockTarget });

    service['heatmapDetailsPanelDataSignal'].set({
      data: structuredClone(heatmapDetailsPanelDataMock),
      event: mockEvent,
    });
    fixture.detectChanges();
  }

  return { renderResult, fixture, component, element, service };
}

describe('Component: Heatmap - Details Panel', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should have data', async () => {
    const { component } = await setup();
    expect(component.panelData()).toEqual(heatmapDetailsPanelDataMock);
  });

  it('should have label', async () => {
    await setup();
    expect(screen.getByText(heatmapDetailsPanelDataMock.label?.left as string)).toBeInTheDocument();
  });

  it('should have heading', async () => {
    await setup();
    expect(screen.getByText(heatmapDetailsPanelDataMock.heading as string)).toBeInTheDocument();
  });

  it('should have sub heading', async () => {
    await setup();
    expect(
      screen.getByText(heatmapDetailsPanelDataMock.subHeadings?.[0] as string),
    ).toBeInTheDocument();
  });

  it('should have values', async () => {
    await setup();
    expect(
      screen.getByText(heatmapDetailsPanelDataMock.valueLabel?.toString() as string),
    ).toBeInTheDocument();
    expect(
      screen.getByText(heatmapDetailsPanelDataMock.value?.toString() as string),
    ).toBeInTheDocument();
    expect(
      screen.getByText(heatmapDetailsPanelDataMock.pValue?.toString() as string),
    ).toBeInTheDocument();
  });

  describe('getSignificantFigures', () => {
    it('should return emdash for null value', async () => {
      const { component } = await setup(false);
      const result = component.getSignificantFigures(null, 3);
      expect(result).toBe('\u2014');
    });

    it('should return emdash for undefined value', async () => {
      const { component } = await setup(false);
      const result = component.getSignificantFigures(undefined, 3);
      expect(result).toBe('\u2014');
    });

    it('should return formatted number for valid value', async () => {
      const { component } = await setup(false);
      const result = component.getSignificantFigures(0.123456, 3);
      expect(result).toBe(0.123);
    });
  });

  describe('double buffering behavior', () => {
    it('should alternate active panel index when showing new data', async () => {
      const { component, service, fixture } = await setup();

      // Initial state after first show
      const initialIndex = component.activeIndex();
      expect(initialIndex).toBe(1); // After first show, index should be 1

      // Show new data with a different event target
      const newData = { ...heatmapDetailsPanelDataMock, heading: 'New Heading' };
      const newTarget = document.createElement('div');
      const newEvent = new MouseEvent('click', { bubbles: true });
      Object.defineProperty(newEvent, 'target', { value: newTarget });

      service['heatmapDetailsPanelDataSignal'].set({
        data: newData,
        event: newEvent,
      });
      fixture.detectChanges();

      // Index should have toggled
      expect(component.activeIndex()).toBe(0);
    });
  });
});
