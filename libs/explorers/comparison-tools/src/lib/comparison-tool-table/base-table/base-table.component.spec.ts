import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter, RouterModule } from '@angular/router';
import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfig,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { BaseTableComponent } from './base-table.component';

async function setup() {
  const component = await render(BaseTableComponent, {
    imports: [RouterModule],
    providers: [
      provideRouter([]),
      provideHttpClient(withInterceptorsFromDi()),
      ...provideComparisonToolService({ configs: mockComparisonToolDataConfig }),
      ...provideComparisonToolFilterService({ significanceThresholdActive: false }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
    componentInputs: {
      data: mockComparisonToolData,
      shouldPaginate: false,
    },
  });

  const fixture = component.fixture;
  const nativeElement = fixture.nativeElement as HTMLElement;

  return { component, fixture, nativeElement };
}

describe('BaseTableComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render table rows with text content', async () => {
    await setup();

    const modelTypes = screen.getAllByText('Familial AD');
    expect(modelTypes.length).toBeGreaterThan(0);

    const ageCells = screen.getAllByText('12 months');
    expect(ageCells.length).toBeGreaterThan(0);

    expect(screen.getByText('C57BL/6J, 5xFAD')).toBeInTheDocument();
  });

  it('should render links using row data values and column config fallbacks', async () => {
    await setup();

    const centerLinks = screen.getAllByRole('link', { name: 'UCI' });
    expect(
      centerLinks.some((link) => link.getAttribute('href') === 'https://www.sagebionetworks.org/'),
    ).toBe(true);
    expect(
      centerLinks.some(
        (link) =>
          link.getAttribute('href') ===
          'http://model-ad.org/uci-disease-model-development-and-phenotyping-dmp/',
      ),
    ).toBe(true);

    const resultsLinks = screen
      .getAllByRole('link', { name: 'Results' })
      .filter((link) => link.getAttribute('href')?.includes('comparison/correlation?model=5xFAD'));
    expect(resultsLinks.length).toBe(1);
  });

  it('should render heatmap circles for heat map columns', async () => {
    const { nativeElement } = await setup();
    const heatmapCircles = nativeElement.querySelectorAll('explorers-heatmap-circle');
    expect(heatmapCircles.length).toBeGreaterThan(0);
  });
});
