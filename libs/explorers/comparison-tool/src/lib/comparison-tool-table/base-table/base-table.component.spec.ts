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
import { MessageService } from 'primeng/api';
import { BaseTableComponent } from './base-table.component';

async function setup(
  viewConfig?: Partial<ComparisonToolViewConfig>,
  data: Record<string, unknown>[] = mockComparisonToolData,
) {
  const user = userEvent.setup();
  const component = await render(BaseTableComponent, {
    imports: [RouterModule],
    providers: [
      provideRouter([]),
      provideHttpClient(withInterceptorsFromDi()),
      MessageService,
      ...provideComparisonToolService({
        configs: mockComparisonToolDataConfig,
        viewConfig,
      }),
      ...provideComparisonToolFilterService({ significanceThresholdActive: false }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
    componentInputs: { data },
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

  describe('row selection and hover', () => {
    it('clicking a row calls selectRow() when rowSelectionEnabled=true', async () => {
      const { fixture, service, nativeElement, user } = await setup({
        rowSelectionEnabled: true,
        rowIdDataKey: '_id',
      });
      fixture.detectChanges();

      const selectSpy = jest.spyOn(service, 'selectRow');
      const firstRow = nativeElement.querySelector('tbody tr');
      if (firstRow) {
        await user.click(firstRow);
      }
      expect(selectSpy).toHaveBeenCalled();
    });

    it('clicking a row does nothing when rowSelectionEnabled=false', async () => {
      const { fixture, service, nativeElement, user } = await setup({ rowIdDataKey: '_id' });
      fixture.detectChanges();

      const firstRow = nativeElement.querySelector('tbody tr');
      if (firstRow) {
        await user.click(firstRow);
      }
      expect(service.selectedRowId()).toBeNull();
    });

    it('mouseenter calls setHoveredRowId() when rowHoverEnabled=true', async () => {
      const { fixture, service, nativeElement } = await setup({
        rowHoverEnabled: true,
        rowIdDataKey: '_id',
      });
      fixture.detectChanges();

      const hoverSpy = jest.spyOn(service, 'setHoveredRowId');
      const firstRow = nativeElement.querySelector('tbody tr');
      if (firstRow) {
        firstRow.dispatchEvent(new MouseEvent('mouseenter', { bubbles: true }));
      }
      expect(hoverSpy).toHaveBeenCalled();
    });

    it('mouseleave calls setHoveredRowId(null) when rowHoverEnabled=true', async () => {
      const { fixture, service, nativeElement } = await setup({
        rowHoverEnabled: true,
        rowIdDataKey: '_id',
      });
      fixture.detectChanges();

      const hoverSpy = jest.spyOn(service, 'setHoveredRowId');
      const firstRow = nativeElement.querySelector('tbody tr');
      if (firstRow) {
        firstRow.dispatchEvent(new MouseEvent('mouseleave', { bubbles: true }));
      }
      expect(hoverSpy).toHaveBeenCalledWith(null);
    });

    it('mouse events do nothing when rowHoverEnabled=false', async () => {
      const { fixture, service, nativeElement } = await setup({ rowIdDataKey: '_id' });
      fixture.detectChanges();

      const firstRow = nativeElement.querySelector('tbody tr');
      if (firstRow) {
        firstRow.dispatchEvent(new MouseEvent('mouseenter', { bubbles: true }));
        firstRow.dispatchEvent(new MouseEvent('mouseleave', { bubbles: true }));
      }
      expect(service.hoveredRowId()).toBeNull();
    });

    it('.selected class appears on the correct row', async () => {
      const { fixture, service, nativeElement } = await setup({
        rowSelectionEnabled: true,
        rowIdDataKey: '_id',
      });

      const firstRowId = String(mockComparisonToolData[0]['_id']);
      service.selectRow(firstRowId);
      fixture.detectChanges();

      const rows = nativeElement.querySelectorAll('tr.selected');
      expect(rows.length).toBe(1);
    });

    it('.selected class appears when rowIdDataKey is a numeric value', async () => {
      const numericData = [
        { id: 1, name: 'Row One' },
        { id: 2, name: 'Row Two' },
      ];
      const { fixture, service, nativeElement } = await setup(
        { rowSelectionEnabled: true, rowIdDataKey: 'id' },
        numericData,
      );

      service.selectRow('1');
      fixture.detectChanges();

      const rows = nativeElement.querySelectorAll('tr.selected');
      expect(rows.length).toBe(1);
    });

    it('.hovered class appears on the correct row', async () => {
      const { fixture, service, nativeElement } = await setup({
        rowHoverEnabled: true,
        rowIdDataKey: '_id',
      });

      const firstRowId = String(mockComparisonToolData[0]['_id']);
      service.setHoveredRowId(firstRowId);
      fixture.detectChanges();

      const rows = nativeElement.querySelectorAll('tr.hovered');
      expect(rows.length).toBe(1);
    });
  });
});
