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

async function setup() {
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
      shouldPaginate: false,
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
      .filter((link) => link.getAttribute('href')?.includes('comparison/correlation?model=5xFAD'));
    expect(resultsLinks.length).toBe(1);
  });

  it('should render heatmap circles for heat map columns', async () => {
    const { nativeElement } = await setup();
    const heatmapCircles = nativeElement.querySelectorAll('explorers-heatmap-circle');
    expect(heatmapCircles.length).toBeGreaterThan(0);
  });

  describe('getLinkText', () => {
    it('should return string value when cellData is a string', async () => {
      const { fixture } = await setup();
      const componentInstance = fixture.componentInstance;
      const result = componentInstance.getLinkText('Test Model', {});
      expect(result).toBe('Test Model');
    });

    it('should return link_text from object when cellData is an object', async () => {
      const { fixture } = await setup();
      const componentInstance = fixture.componentInstance;
      const result = componentInstance.getLinkText(
        { link_text: 'Link Text', link_url: '/url' },
        { link_text: 'Fallback' },
      );
      expect(result).toBe('Link Text');
    });

    it('should use column link_text as fallback when object has no link_text', async () => {
      const { fixture } = await setup();
      const componentInstance = fixture.componentInstance;
      const result = componentInstance.getLinkText({ link_url: '/url' }, { link_text: 'Fallback' });
      expect(result).toBe('Fallback');
    });
  });

  describe('getLinkUrl', () => {
    it('should construct model URL when cellData is string and type is link_internal', async () => {
      const { fixture } = await setup();
      const componentInstance = fixture.componentInstance;
      const result = componentInstance.getLinkUrl('5xFAD (IU/Jax/Pitt)', {
        type: 'link_internal',
      });
      expect(result).toEqual(['/models', '5xFAD (IU/Jax/Pitt)']);
    });

    it('should use column link_url when cellData is string and link_url is provided', async () => {
      const { fixture } = await setup();
      const componentInstance = fixture.componentInstance;
      const result = componentInstance.getLinkUrl('Test', {
        type: 'link_internal',
        link_url: '/custom/url',
      });
      expect(result).toBe('/custom/url');
    });

    it('should return link_url from object when cellData is an object', async () => {
      const { fixture } = await setup();
      const componentInstance = fixture.componentInstance;
      const result = componentInstance.getLinkUrl(
        { link_text: 'Link', link_url: '/object/url' },
        { link_url: '/fallback' },
      );
      expect(result).toBe('/object/url');
    });

    it('should use column link_url as fallback when object has no link_url', async () => {
      const { fixture } = await setup();
      const componentInstance = fixture.componentInstance;
      const result = componentInstance.getLinkUrl({ link_text: 'Link' }, { link_url: '/fallback' });
      expect(result).toBe('/fallback');
    });
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
        heatmapCircleClickTransform: () => heatmapDetailsPanelData,
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
        heatmapCircleClickTransform: () => heatmapDetailsPanelData,
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
});
