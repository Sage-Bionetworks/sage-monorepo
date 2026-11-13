import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import {
  DEFAULT_PAGE_SIZE,
  PlatformService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  provideExplorersConfig,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import {
  ComparisonToolConfigService,
  ComparisonToolPage,
  Eqtl,
  EqtlService,
  EqtlsPage,
  ItemFilterTypeQuery,
} from '@sagebionetworks/qtl/api-client';
import { render, screen } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
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
        useValue: { isBrowser: true },
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
      MessageService,
      ...provideComparisonToolService(),
      ...provideComparisonToolFilterService(),
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

  it('renders the sidebar chiclets and tabs', async () => {
    await setup();
    expect(screen.getByText('variant:')).toBeInTheDocument();
    expect(screen.getByText('gene:')).toBeInTheDocument();
    expect(screen.getByText('cell class:')).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Impact' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Traits' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Resources' })).toBeInTheDocument();
  });
});
