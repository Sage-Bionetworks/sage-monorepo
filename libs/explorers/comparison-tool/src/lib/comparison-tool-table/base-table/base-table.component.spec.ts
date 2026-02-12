import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter, RouterModule } from '@angular/router';
import {
  ComparisonToolViewConfig,
  HeatmapDetailsPanelData,
} from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
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
import userEvent from '@testing-library/user-event';
import { BaseTableComponent } from './base-table.component';

async function setup(options: { shouldPaginate?: boolean } = {}) {
  const { shouldPaginate = false } = options;
  const user = userEvent.setup();
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
      shouldPaginate,
    },
  });

  const fixture = component.fixture;
  const nativeElement = fixture.nativeElement as HTMLElement;
  const service = fixture.debugElement.injector.get(ComparisonToolService);

  return { component, fixture, nativeElement, service, user };
}

describe('BaseTableComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render table rows with text content', async () => {
    await setup();

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
      .filter((link) => link.getAttribute('href')?.includes('comparison/correlation?models=5xFAD'));
    expect(resultsLinks.length).toBe(1);
  });

  it('should render heatmap circles for heat map columns', async () => {
    const { nativeElement } = await setup();
    const heatmapCircles = nativeElement.querySelectorAll('explorers-heatmap-circle');
    expect(heatmapCircles.length).toBeGreaterThan(0);
  });

  describe('heatmap details panel', () => {
    const heatmapDetailsPanelData: HeatmapDetailsPanelData = {
      heading: 'Test',
      subHeadings: [],
      valueLabel: 'Test Label',
      footer: 'Anything',
    };

    it('should render heatmap circles without button when no transform is configured', async () => {
      const { nativeElement } = await setup();
      const buttons = screen.queryAllByRole('button');
      const heatmapButtons = buttons.filter((btn) =>
        btn.classList.contains('heatmap-circle-button'),
      );
      expect(heatmapButtons.length).toBe(0);

      const heatmapCircles = nativeElement.querySelectorAll('explorers-heatmap-circle');
      expect(heatmapCircles.length).toBeGreaterThan(0);
    });

    it('should render heatmap circles inside buttons when transform is configured', async () => {
      const { fixture, service } = await setup();

      service.setViewConfig({
        heatmapCircleClickTransformFn: () => heatmapDetailsPanelData,
      } as Partial<ComparisonToolViewConfig>);
      fixture.detectChanges();

      const buttons = screen.getAllByRole('button');
      const heatmapButtons = buttons.filter((btn) =>
        btn.classList.contains('heatmap-circle-button'),
      );
      expect(heatmapButtons.length).toBeGreaterThan(0);

      // Verify heatmap circles are inside buttons
      const circlesInsideButtons = heatmapButtons.filter(
        (btn) => btn.querySelector('explorers-heatmap-circle') !== null,
      );
      expect(circlesInsideButtons.length).toBeGreaterThan(0);
    });

    it('should call showHeatmapDetailsPanel when button is clicked', async () => {
      const { fixture, service, user } = await setup();

      service.setViewConfig({
        heatmapCircleClickTransformFn: () => heatmapDetailsPanelData,
      } as Partial<ComparisonToolViewConfig>);
      fixture.detectChanges();

      const showSpy = jest.spyOn(service, 'showHeatmapDetailsPanel');

      const buttons = screen.getAllByRole('button', { name: '' });
      const heatmapButton = buttons.find((btn) => btn.classList.contains('heatmap-circle-button'));
      expect(heatmapButton).toBeDefined();
      await user.click(heatmapButton as HTMLElement);

      expect(showSpy).toHaveBeenCalled();
      expect(showSpy).toHaveBeenCalledWith(
        expect.any(Object),
        expect.any(Object),
        expect.any(String),
        expect.any(MouseEvent),
      );
    });
  });

  describe('pagination', () => {
    it('should not render paginator when shouldPaginate is false', async () => {
      const { nativeElement } = await setup({ shouldPaginate: false });
      const paginator = nativeElement.querySelector('.p-paginator');
      expect(paginator).toBeNull();
    });

    it('should render paginator when shouldPaginate is true', async () => {
      const { nativeElement } = await setup({ shouldPaginate: true });
      const paginator = nativeElement.querySelector('.p-paginator');
      expect(paginator).toBeTruthy();
    });

    it('should render page links when pagination is enabled', async () => {
      const { nativeElement } = await setup({ shouldPaginate: true });
      const pageLinks = nativeElement.querySelector('.p-paginator-pages');
      expect(pageLinks).toBeTruthy();
    });

    it('should render rows per page dropdown', async () => {
      const { nativeElement } = await setup({ shouldPaginate: true });
      const dropdown = nativeElement.querySelector('.p-paginator-rpp-dropdown');
      expect(dropdown).toBeTruthy();
    });

    it('should render current page report template', async () => {
      const { nativeElement } = await setup({ shouldPaginate: true });
      const currentReport = nativeElement.querySelector('.p-paginator-current');
      expect(currentReport).toBeTruthy();
    });

    it('should apply comparison-tool-paginator style class', async () => {
      const { nativeElement } = await setup({ shouldPaginate: true });
      const paginator = nativeElement.querySelector('.comparison-tool-paginator');
      expect(paginator).toBeTruthy();
    });

    it('should render navigation buttons (first, prev, next, last)', async () => {
      const { nativeElement } = await setup({ shouldPaginate: true });
      const firstBtn = nativeElement.querySelector('.p-paginator-first');
      const prevBtn = nativeElement.querySelector('.p-paginator-prev');
      const nextBtn = nativeElement.querySelector('.p-paginator-next');
      const lastBtn = nativeElement.querySelector('.p-paginator-last');

      expect(firstBtn).toBeTruthy();
      expect(prevBtn).toBeTruthy();
      expect(nextBtn).toBeTruthy();
      expect(lastBtn).toBeTruthy();
    });
  });

  describe('loading state', () => {
    it('should have isLoading false initially', async () => {
      const { service } = await setup();
      expect(service.isLoading()).toBe(false);
    });

    it('should track loading state through startFetch/setUnpinnedData', async () => {
      const { fixture, service } = await setup();

      expect(service.isLoading()).toBe(false);

      service.startFetch();
      fixture.detectChanges();
      expect(service.isLoading()).toBe(true);

      service.setUnpinnedData([]);
      fixture.detectChanges();
      expect(service.isLoading()).toBe(false);
    });

    it('should track loading state through startFetch/setPinnedData', async () => {
      const { fixture, service } = await setup();

      expect(service.isLoading()).toBe(false);

      service.startFetch();
      fixture.detectChanges();
      expect(service.isLoading()).toBe(true);

      service.setPinnedData([]);
      fixture.detectChanges();
      expect(service.isLoading()).toBe(false);
    });

    it('should bind loading state to p-table', async () => {
      const { fixture, nativeElement, service } = await setup();

      const table = nativeElement.querySelector('p-table');
      expect(table).toBeTruthy();

      // Verify initial state
      expect(service.isLoading()).toBe(false);

      // Set loading to true and verify the service state changes
      service.startFetch();
      fixture.detectChanges();
      expect(service.isLoading()).toBe(true);

      // Set loading to false and verify the service state changes
      service.setUnpinnedData([]);
      fixture.detectChanges();
      expect(service.isLoading()).toBe(false);
    });
  });
});
