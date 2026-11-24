import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import {
  ComparisonToolService,
  DEFAULT_PAGE_SIZE,
  PlatformService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  provideExplorersConfig,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import {
  mockEmptyComparisonToolQuery,
  provideLoadingIconColors,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import {
  ComparisonToolConfigService,
  ComparisonToolPage,
  Eqtl,
  EqtlPhenotypeService,
  EqtlService,
  EqtlsPage,
  ItemFilterTypeQuery,
} from '@sagebionetworks/qtl/api-client';
import { eqtlPhenotypeMock } from '@sagebionetworks/qtl/testing';
import { render, screen } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { SIDEBAR_TABS } from './components/sidebar/sidebar.component';
import { EqtlComparisonToolComponent } from './eqtl-comparison-tool.component';
import { EqtlComparisonToolService } from './services/eqtl-comparison-tool.service';

const mockEqtl: Eqtl = {
  composite_id: 'rs2120461~ENSG00000142599~astro',
  variant: 'rs2120461',
  variant_location: { chr: '1', start: 8387662, end: 8387663 },
  effect_direction: 'Up',
  gene_symbol: 'RERE',
  ensembl_gene_id: 'ENSG00000142599',
  gene_location: { chr: '1', start: 8387662, end: 8387663 },
  beta: 0.575878,
  adj_p_val: '1.05E-136',
  adj_p_val_numeric: -135.977471,
  maf: 0.3321,
  minor_allele: 'C',
  reference_allele: 'T',
  cell_type_id: 'astro',
  cell_class: 'Astrocyte',
  cell_class_short: 'Astro',
  cell_subclass: 'Astrocyte',
  cell_subclass_short: 'Astro',
};

function mockPage(rows: Eqtl[], totalElements = rows.length): EqtlsPage {
  return {
    eqtls: rows,
    page: {
      number: 0,
      size: DEFAULT_PAGE_SIZE,
      totalElements,
      totalPages: Math.ceil(totalElements / DEFAULT_PAGE_SIZE),
      hasNext: totalElements > DEFAULT_PAGE_SIZE,
      hasPrevious: false,
    },
  };
}

async function setup(page: EqtlsPage = mockPage([])) {
  const { fixture } = await render(EqtlComparisonToolComponent, {
    providers: [
      provideHttpClient(),
      provideNoopAnimations(),
      provideLoadingIconColors(),
      provideExplorersConfig({ tutorialPanes: [] }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
      provideRouter([]),
      {
        provide: PlatformService,
        useValue: { isBrowser: true, isServer: false },
      },
      {
        provide: ComparisonToolConfigService,
        useValue: {
          getComparisonToolConfig: jest.fn().mockReturnValue(of([])),
        },
      },
      {
        provide: EqtlService,
        useValue: {
          getEqtls: jest.fn().mockReturnValue(of(page)),
        },
      },
      {
        provide: EqtlPhenotypeService,
        useValue: {
          getEqtlPhenotype: jest.fn().mockReturnValue(of(eqtlPhenotypeMock)),
        },
      },
      MessageService,
      ...provideComparisonToolService(),
      ...provideComparisonToolFilterService(),
      { provide: ComparisonToolService, useExisting: EqtlComparisonToolService },
      EqtlComparisonToolService,
    ],
  });

  const component = fixture.componentInstance;
  const comparisonToolConfigService = fixture.debugElement.injector.get(
    ComparisonToolConfigService,
  );
  const comparisonToolService = fixture.debugElement.injector.get(EqtlComparisonToolService);
  const eqtlService = fixture.debugElement.injector.get(EqtlService);
  return { component, fixture, comparisonToolConfigService, comparisonToolService, eqtlService };
}

describe('EqtlComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should request the eQTLs comparison tool config', async () => {
    const { comparisonToolConfigService } = await setup();
    expect(comparisonToolConfigService.getComparisonToolConfig).toHaveBeenCalledWith(
      ComparisonToolPage.EQtls,
    );
  });

  it('should hand its view config to the comparison tool service', async () => {
    const { component, comparisonToolService } = await setup();
    expect(comparisonToolService.viewConfig()).toEqual(
      expect.objectContaining(component.viewConfig),
    );
  });

  it('should request the first page of unpinned eQTLs in the default sort', async () => {
    const { component, eqtlService } = await setup();
    const defaultSort = component.viewConfig.defaultSort ?? [];

    expect(eqtlService.getEqtls).toHaveBeenCalledWith({
      items: [],
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: 0,
      pageSize: DEFAULT_PAGE_SIZE,
      sortFields: defaultSort.map((sort) => sort.field),
      sortOrders: defaultSort.map((sort) => sort.order),
    });
  });

  it('should hand the unpinned eQTLs and their total to the comparison tool service', async () => {
    const totalElements = 381;
    const { comparisonToolService } = await setup(mockPage([mockEqtl], totalElements));

    expect(comparisonToolService.unpinnedData()).toEqual([mockEqtl]);
    expect(comparisonToolService.unpinnedRowCount()).toBe(totalElements);
  });

  it('should send the selected filters in the unpinned query', async () => {
    const { component, comparisonToolService, eqtlService } = await setup();
    const selectedFilters = {
      variant: ['rs2120461'],
      gene: ['RERE'],
      cellClass: ['Astrocyte'],
      cellSubclass: ['Astrocyte'],
    };
    jest.spyOn(comparisonToolService, 'selectedFilters').mockReturnValue(selectedFilters);

    component.getUnpinnedData(mockEmptyComparisonToolQuery);

    expect(jest.mocked(eqtlService.getEqtls).mock.lastCall?.[0]).toEqual(
      expect.objectContaining(selectedFilters),
    );
  });

  it('should identify each eQTL by its composite id', async () => {
    const { comparisonToolService } = await setup();
    expect(comparisonToolService.rowId(mockEqtl)).toBe(mockEqtl.composite_id);
  });

  it('should request a pinned eQTL in the current sort and show it in the pinned section', async () => {
    const { fixture, component, comparisonToolService, eqtlService } = await setup(
      mockPage([mockEqtl]),
    );
    const defaultSort = component.viewConfig.defaultSort ?? [];

    comparisonToolService.pinItem(mockEqtl);
    fixture.detectChanges();

    const queriesSent = jest.mocked(eqtlService.getEqtls).mock.calls.map(([query]) => query);
    expect(queriesSent).toContainEqual({
      items: [mockEqtl.composite_id],
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields: defaultSort.map((sort) => sort.field),
      sortOrders: defaultSort.map((sort) => sort.order),
    });
    expect(comparisonToolService.pinnedData()).toEqual([mockEqtl]);
  });

  it('should leave the pinned eQTLs out of the unpinned query', async () => {
    const { component, eqtlService } = await setup();
    const pinnedItems = [mockEqtl.composite_id];

    component.getUnpinnedData({ ...mockEmptyComparisonToolQuery, pinnedItems });

    expect(jest.mocked(eqtlService.getEqtls).mock.lastCall?.[0]).toEqual(
      expect.objectContaining({ items: pinnedItems, itemFilterType: ItemFilterTypeQuery.Exclude }),
    );
  });

  it('should ask Pin All for the remaining budget of unpinned eQTLs instead of a page', async () => {
    const { comparisonToolService, eqtlService } = await setup(mockPage([mockEqtl]));
    jest.spyOn(comparisonToolService, 'canPinAll').mockReturnValue(true);
    const remainingBudget = comparisonToolService.pinLimit() - comparisonToolService.pinCount();

    comparisonToolService.pinAll();

    const pinAllQuery = jest.mocked(eqtlService.getEqtls).mock.lastCall?.[0];
    expect(pinAllQuery).toEqual(
      expect.objectContaining({ itemFilterType: ItemFilterTypeQuery.Exclude, remainingBudget }),
    );
    expect(pinAllQuery).not.toHaveProperty('pageNumber');
    expect(pinAllQuery).not.toHaveProperty('pageSize');
  });

  describe('selected row', () => {
    const otherEqtl: Eqtl = { ...mockEqtl, composite_id: 'rs2120461~ENSG00000142599~micro' };
    const newResult: Eqtl = { ...mockEqtl, composite_id: 'rs2120461~ENSG00000142599~oligo' };

    it('should send the selected row with the unpinned query', async () => {
      const { component, comparisonToolService, eqtlService } = await setup(
        mockPage([mockEqtl, otherEqtl]),
      );
      comparisonToolService.selectRow(otherEqtl.composite_id);

      component.getUnpinnedData(mockEmptyComparisonToolQuery);

      expect(jest.mocked(eqtlService.getEqtls).mock.lastCall?.[0]).toEqual(
        expect.objectContaining({ selectedRowId: otherEqtl.composite_id }),
      );
    });

    it('should send no selected row when nothing is selected', async () => {
      const { component, eqtlService } = await setup();

      component.getUnpinnedData(mockEmptyComparisonToolQuery);

      expect(jest.mocked(eqtlService.getEqtls).mock.lastCall?.[0]?.selectedRowId).toBeUndefined();
    });

    it('should select the first new result when the selected row left the results', async () => {
      const { component, comparisonToolService, eqtlService } = await setup(
        mockPage([mockEqtl, otherEqtl]),
      );
      comparisonToolService.selectRow(otherEqtl.composite_id);
      const answer: EqtlsPage = { ...mockPage([newResult]), selectedRowInResults: false };
      (eqtlService.getEqtls as jest.Mock).mockReturnValue(of(answer));

      component.getUnpinnedData(mockEmptyComparisonToolQuery);

      expect(comparisonToolService.selectedRowId()).toBe(newResult.composite_id);
    });

    it('should not send the selected row with the Pin All query', async () => {
      const { comparisonToolService, eqtlService } = await setup(mockPage([mockEqtl, otherEqtl]));
      comparisonToolService.selectRow(otherEqtl.composite_id);
      jest.spyOn(comparisonToolService, 'canPinAll').mockReturnValue(true);

      comparisonToolService.pinAll();

      expect(jest.mocked(eqtlService.getEqtls).mock.lastCall?.[0]).not.toHaveProperty(
        'selectedRowId',
      );
    });
  });

  it('renders the sidebar chiclets and tabs', async () => {
    await setup();
    expect(screen.getByText('variant:')).toBeInTheDocument();
    expect(screen.getByText('gene:')).toBeInTheDocument();
    expect(screen.getByText('cell class:')).toBeInTheDocument();
    SIDEBAR_TABS.forEach(({ label }) => {
      expect(screen.getByRole('tab', { name: label })).toBeInTheDocument();
    });
  });
});
