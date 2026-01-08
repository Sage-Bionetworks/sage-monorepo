import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { HelperService } from '@sagebionetworks/explorers/services';
import { heatmapDetailsPanelDataMock } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { HeatmapDetailsPanelComponent } from './heatmap-details-panel.component';

async function setup() {
  const renderResult = await render(HeatmapDetailsPanelComponent, {
    imports: [NoopAnimationsModule],
    providers: [provideRouter([]), HelperService],
  });

  const fixture = renderResult.fixture;
  const component = fixture.componentInstance;
  const element = fixture.nativeElement;

  component.show(new MouseEvent('click'), structuredClone(heatmapDetailsPanelDataMock));
  fixture.detectChanges();

  return { renderResult, fixture, component, element };
}

describe('Component: Heatmap - Details Panel', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should have data', async () => {
    const { component } = await setup();
    expect(component.data()).toEqual(heatmapDetailsPanelDataMock);
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
});
