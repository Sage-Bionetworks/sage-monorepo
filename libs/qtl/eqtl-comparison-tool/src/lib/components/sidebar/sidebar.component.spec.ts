import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
  ComparisonToolService,
  formatHttpErrorReportMessage,
  HttpRequestError,
  PlatformService,
  provideComparisonToolService,
  SourceLogger,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { Eqtl, EqtlPhenotype, EqtlPhenotypeService } from '@sagebionetworks/qtl/api-client';
import { eqtlMock, eqtlPhenotypeMock } from '@sagebionetworks/qtl/testing';
import { formatGenomicLocation, resolveCellSubclass } from '@sagebionetworks/qtl/util';
import { render, screen, within } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { MessageService } from 'primeng/api';
import { NEVER, Observable, of, Subject, throwError } from 'rxjs';
import { EqtlComparisonToolService } from '../../services/eqtl-comparison-tool.service';
import { IMPACT_BOXPLOT_TITLE_PREFIX } from '../impact/boxplot/impact-boxplot.constants';
import { IMPACT_DETAIL_FIELDS } from '../impact/details/impact-details.component';
import { IMPACT_FOREST_PLOT_TITLE } from '../impact/forest-plot/impact-forest-plot.constants';
import { impactPlotLoadingLabel } from '../impact/impact-plot-loading';
import { LEGEND_CELL_CLASSES_TITLE } from '../legend/legend.constants';
import {
  EQTL_PHENOTYPE_ERROR_MESSAGE,
  SELECTED_EQTL_NOT_FOUND_MESSAGE,
  SIDEBAR_CHICLET_LIST_ARIA_LABEL,
  SIDEBAR_NO_DATA_MESSAGE,
  SIDEBAR_TABS,
  SidebarComponent,
} from './sidebar.component';

const [impactTabLabel, legendTabLabel] = SIDEBAR_TABS.map((tab) => tab.label);

const browserPlatform = { isBrowser: true, isServer: false };
const serverPlatform = { isBrowser: false, isServer: true };

const microgliaEqtl: Eqtl = {
  ...eqtlMock,
  composite_id: 'rs2120461~ENSG00000142599~micropvm',
  beta: -0.213456,
  adj_p_val: '3.21E-12',
  cell_type_id: 'micropvm',
  cell_class: 'Immune Cell',
  cell_class_short: 'Immune',
  cell_subclass: 'Microglia and Perivascular Macrophage',
  cell_subclass_short: 'MicroPVM',
};

const astrocyteSubclass = resolveCellSubclass('astro');
const microgliaSubclass = resolveCellSubclass('micropvm');

const astrocytePlotLoadingLabels = [
  impactPlotLoadingLabel(`${IMPACT_BOXPLOT_TITLE_PREFIX} (${astrocyteSubclass.name})`),
  impactPlotLoadingLabel(IMPACT_FOREST_PLOT_TITLE),
];

const PHENOTYPE_REQUEST_URL = 'https://xqtlexplorer.adknowledgeportal.org/api/v1/eqtl-phenotype';

function buildPhenotypeRequestError(status: HttpStatusCode): HttpRequestError {
  return new HttpRequestError(new HttpErrorResponse({ status, url: PHENOTYPE_REQUEST_URL }), 'GET');
}

async function setup({
  rows = [eqtlMock],
  phenotypeResponse = of(eqtlPhenotypeMock),
  platformService = browserPlatform,
}: {
  rows?: Eqtl[];
  phenotypeResponse?: Observable<EqtlPhenotype>;
  platformService?: typeof browserPlatform;
} = {}) {
  const mockEqtlPhenotypeService = {
    getEqtlPhenotype: jest.fn().mockReturnValue(phenotypeResponse),
  };
  const component = await render(SidebarComponent, {
    providers: [
      provideNoopAnimations(),
      MessageService,
      provideLoadingIconColors(),
      ...provideComparisonToolService({
        viewConfig: { rowIdDataKey: 'composite_id', rowSelectionEnabled: true },
      }),
      { provide: EqtlComparisonToolService, useExisting: ComparisonToolService },
      { provide: PlatformService, useValue: platformService },
      { provide: EqtlPhenotypeService, useValue: mockEqtlPhenotypeService },
    ],
  });
  const comparisonToolService =
    component.fixture.debugElement.injector.get(EqtlComparisonToolService);
  const loadRows = async (loadedRows: Eqtl[]) => {
    comparisonToolService.fetchUnpinned(of({ data: loadedRows, totalCount: loadedRows.length }));
    component.fixture.detectChanges();
    await component.fixture.whenStable();
  };
  const selectRow = async (rowId: string) => {
    comparisonToolService.selectRow(rowId);
    component.fixture.detectChanges();
    await component.fixture.whenStable();
  };
  await loadRows(rows);
  return { component, comparisonToolService, mockEqtlPhenotypeService, loadRows, selectRow };
}

function queryImpactPlots() {
  const impactPanel = screen.getByRole('tabpanel');
  return {
    boxplot: within(impactPanel).queryByRole('figure', {
      name: `${IMPACT_BOXPLOT_TITLE_PREFIX} (${astrocyteSubclass.name})`,
    }),
    forestPlot: within(impactPanel).queryByRole('figure', { name: IMPACT_FOREST_PLOT_TITLE }),
  };
}

function getPlotLoadingStatusNames(): (string | null)[] {
  return within(screen.getByRole('tabpanel'))
    .queryAllByRole('status')
    .map((status) => status.getAttribute('aria-label'));
}

function getChicletTexts(): string[] {
  const chicletList = screen.getByRole('list', { name: SIDEBAR_CHICLET_LIST_ARIA_LABEL });
  return within(chicletList)
    .getAllByRole('listitem')
    .map((chiclet) => chiclet.textContent?.replace(/\s+/g, ' ').trim() ?? '');
}

function getImpactDetail(label: string): string | undefined {
  const impactPanel = screen.getByRole('tabpanel');
  return within(impactPanel).getByText(label).nextElementSibling?.textContent?.trim();
}

describe('SidebarComponent', () => {
  afterEach(() => jest.restoreAllMocks());

  it("lists the selected row's variant, gene, cell class and cell subclass as a named list", async () => {
    await setup();
    expect(getChicletTexts()).toEqual([
      `variant: ${eqtlMock.variant}`,
      `gene: ${eqtlMock.gene_symbol}`,
      `cell class: ${astrocyteSubclass.cellClass.name}`,
      `cell subclass: ${astrocyteSubclass.name}`,
    ]);
  });

  it("shows the selected row's Beta, P-Value, locations and cell class in the Impact tab", async () => {
    await setup();
    expect(screen.getAllByRole('term')).toHaveLength(IMPACT_DETAIL_FIELDS.length);
    expect(getImpactDetail('Beta')).toBe(String(eqtlMock.beta));
    expect(getImpactDetail('P-Value')).toBe(eqtlMock.adj_p_val);
    expect(getImpactDetail('Variant Loc.')).toBe(formatGenomicLocation(eqtlMock.variant_location));
    expect(getImpactDetail('Gene Loc.')).toBe(formatGenomicLocation(eqtlMock.gene_location));
    expect(getImpactDetail('Cell Class')).toBe(astrocyteSubclass.cellClass.name);
  });

  it('updates the sidebar when another row is selected', async () => {
    const { selectRow } = await setup({ rows: [eqtlMock, microgliaEqtl] });
    await selectRow(microgliaEqtl.composite_id);
    expect(getChicletTexts()).toContain(`cell subclass: ${microgliaSubclass.name}`);
    expect(getImpactDetail('Beta')).toBe(String(microgliaEqtl.beta));
  });

  it('keeps showing the selected row after it leaves the rows on screen', async () => {
    const { loadRows } = await setup();
    await loadRows([microgliaEqtl]);
    expect(getChicletTexts()).toContain(`variant: ${eqtlMock.variant}`);
    expect(getImpactDetail('Beta')).toBe(String(eqtlMock.beta));
  });

  it('shows the no-data message without reporting an error when no row is selected', async () => {
    const loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
    await setup({ rows: [] });
    expect(screen.queryByRole('list', { name: SIDEBAR_CHICLET_LIST_ARIA_LABEL })).toBeNull();
    expect(screen.getByRole('tabpanel')).toHaveTextContent(SIDEBAR_NO_DATA_MESSAGE);
    expect(loggerError).not.toHaveBeenCalled();
  });

  it('shows the no-data message and reports an error when the selected row was never loaded', async () => {
    const loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
    const unloadedRowId = microgliaEqtl.composite_id;
    const { selectRow } = await setup({ rows: [] });
    await selectRow(unloadedRowId);
    expect(screen.getByRole('tabpanel')).toHaveTextContent(SIDEBAR_NO_DATA_MESSAGE);
    expect(loggerError).toHaveBeenCalledWith(SELECTED_EQTL_NOT_FOUND_MESSAGE, {
      data: { selectedRowId: unloadedRowId },
    });
  });

  it("requests the selected row's phenotype data", async () => {
    const { mockEqtlPhenotypeService } = await setup();
    expect(mockEqtlPhenotypeService.getEqtlPhenotype).toHaveBeenCalledWith({
      variant: eqtlMock.variant,
      ensemblGeneId: eqtlMock.ensembl_gene_id,
      cellTypeId: eqtlMock.cell_type_id,
    });
  });

  it('shows a named loading status in each plot while the plot data loads', async () => {
    await setup({ phenotypeResponse: NEVER });
    expect(getPlotLoadingStatusNames()).toEqual(astrocytePlotLoadingLabels);
  });

  it('shows the expression-by-genotype boxplot in the Impact tab, for the same cell subclass as the chiclet', async () => {
    await setup();
    expect(queryImpactPlots().boxplot).toBeVisible();
    expect(getPlotLoadingStatusNames()).toEqual([]);
  });

  it('shows the effect sizes by cell class plot in the Impact tab', async () => {
    await setup();
    expect(queryImpactPlots().forestPlot).toBeVisible();
  });

  it("requests the new row's plot data on a row switch, showing the loading status until it loads", async () => {
    const { mockEqtlPhenotypeService, selectRow } = await setup({
      rows: [eqtlMock, microgliaEqtl],
    });
    mockEqtlPhenotypeService.getEqtlPhenotype.mockReturnValueOnce(NEVER);

    await selectRow(microgliaEqtl.composite_id);

    expect(mockEqtlPhenotypeService.getEqtlPhenotype).toHaveBeenLastCalledWith({
      variant: microgliaEqtl.variant,
      ensemblGeneId: microgliaEqtl.ensembl_gene_id,
      cellTypeId: microgliaEqtl.cell_type_id,
    });
    expect(getPlotLoadingStatusNames()).toEqual([
      impactPlotLoadingLabel(`${IMPACT_BOXPLOT_TITLE_PREFIX} (${microgliaSubclass.name})`),
      impactPlotLoadingLabel(IMPACT_FOREST_PLOT_TITLE),
    ]);
  });

  it('cancels the earlier plot data request when another row is selected before it loads', async () => {
    const earlierPhenotypeResponse = new Subject<EqtlPhenotype>();
    const { selectRow, mockEqtlPhenotypeService } = await setup({
      rows: [eqtlMock, microgliaEqtl],
      phenotypeResponse: earlierPhenotypeResponse,
    });
    mockEqtlPhenotypeService.getEqtlPhenotype.mockReturnValueOnce(NEVER);
    expect(earlierPhenotypeResponse.observed).toBe(true);

    await selectRow(microgliaEqtl.composite_id);

    expect(earlierPhenotypeResponse.observed).toBe(false);
  });

  it('does not request the phenotype data on the server', async () => {
    const { mockEqtlPhenotypeService } = await setup({ platformService: serverPlatform });
    expect(mockEqtlPhenotypeService.getEqtlPhenotype).not.toHaveBeenCalled();
  });

  it('reports a row with no plot data as a warning and shows the plots without data', async () => {
    const error = buildPhenotypeRequestError(HttpStatusCode.NotFound);
    const loggerWarn = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();

    await setup({ phenotypeResponse: throwError(() => error) });

    expect(loggerWarn).toHaveBeenCalledWith(
      formatHttpErrorReportMessage(EQTL_PHENOTYPE_ERROR_MESSAGE, error),
      expect.anything(),
      error,
    );
    expect(queryImpactPlots().boxplot).toBeVisible();
    expect(queryImpactPlots().forestPlot).toBeVisible();
    expect(getPlotLoadingStatusNames()).toEqual([]);
  });

  it('reports any other failed plot data request as an error and shows the plots without data', async () => {
    const error = buildPhenotypeRequestError(HttpStatusCode.ServiceUnavailable);
    const loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();

    await setup({ phenotypeResponse: throwError(() => error) });

    expect(loggerError).toHaveBeenCalledWith(
      formatHttpErrorReportMessage(EQTL_PHENOTYPE_ERROR_MESSAGE, error),
      expect.objectContaining({ error }),
    );
    expect(queryImpactPlots().boxplot).toBeVisible();
    expect(getPlotLoadingStatusNames()).toEqual([]);
  });

  it('renders the Impact and Legend tab headers', async () => {
    await setup();
    expect(screen.getAllByRole('tab').map((tab) => tab.textContent?.trim())).toEqual(
      SIDEBAR_TABS.map((tab) => tab.label),
    );
  });

  it('shows the legend when the Legend tab is clicked', async () => {
    const user = userEvent.setup();
    await setup();
    await user.click(screen.getByRole('tab', { name: legendTabLabel }));
    const legendPanel = screen.getByRole('tabpanel', { name: legendTabLabel });
    expect(
      within(legendPanel).getByRole('heading', { name: LEGEND_CELL_CLASSES_TITLE }),
    ).toBeVisible();
  });

  it('puts only the active tab in the tab order (roving tabindex)', async () => {
    await setup();
    expect(screen.getByRole('tab', { name: impactTabLabel })).toHaveAttribute('tabindex', '0');
    expect(screen.getByRole('tab', { name: legendTabLabel })).toHaveAttribute('tabindex', '-1');
  });

  it('moves focus and activation to the next tab on ArrowRight, wrapping at the end', async () => {
    const user = userEvent.setup();
    await setup();
    const impact = screen.getByRole('tab', { name: impactTabLabel });
    const legend = screen.getByRole('tab', { name: legendTabLabel });

    impact.focus();
    await user.keyboard('{ArrowRight}');
    expect(legend).toHaveFocus();
    expect(legend).toHaveAttribute('aria-selected', 'true');

    await user.keyboard('{ArrowRight}');
    expect(impact).toHaveFocus();
  });

  it('moves focus to the previous tab on ArrowLeft, wrapping at the start', async () => {
    const user = userEvent.setup();
    await setup();
    const impact = screen.getByRole('tab', { name: impactTabLabel });

    impact.focus();
    await user.keyboard('{ArrowLeft}');
    expect(screen.getByRole('tab', { name: legendTabLabel })).toHaveFocus();
  });

  it('jumps to the last tab on End and the first tab on Home', async () => {
    const user = userEvent.setup();
    await setup();

    screen.getByRole('tab', { name: impactTabLabel }).focus();

    await user.keyboard('{End}');
    expect(screen.getByRole('tab', { name: legendTabLabel })).toHaveFocus();

    await user.keyboard('{Home}');
    expect(screen.getByRole('tab', { name: impactTabLabel })).toHaveFocus();
  });
});
