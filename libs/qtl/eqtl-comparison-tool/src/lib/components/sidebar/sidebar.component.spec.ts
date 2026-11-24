import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
  formatHttpErrorReportMessage,
  HttpRequestError,
  PlatformService,
  SourceLogger,
} from '@sagebionetworks/explorers/services';
import { EqtlPhenotype, EqtlPhenotypeService } from '@sagebionetworks/qtl/api-client';
import { eqtlPhenotypeMock } from '@sagebionetworks/qtl/testing';
import { render, screen, within } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { Observable, of, throwError } from 'rxjs';
import { IMPACT_BOXPLOT_TITLE_PREFIX } from '../impact/boxplot/impact-boxplot.constants';
import { IMPACT_DETAIL_FIELDS } from '../impact/details/impact-details.component';
import { IMPACT_FOREST_PLOT_TITLE } from '../impact/forest-plot/impact-forest-plot.constants';
import { LEGEND_CELL_CLASSES_TITLE } from '../legend/legend.constants';
import {
  EQTL_PHENOTYPE_ERROR_MESSAGE,
  SIDEBAR_CHICLET_LIST_ARIA_LABEL,
  SIDEBAR_PLACEHOLDER_PHENOTYPE_QUERY,
  SIDEBAR_TABS,
  SidebarComponent,
} from './sidebar.component';

const [impactTabLabel, legendTabLabel] = SIDEBAR_TABS.map((tab) => tab.label);

const browserPlatform = { isBrowser: true, isServer: false };
const serverPlatform = { isBrowser: false, isServer: true };

async function setup({
  phenotypeResponse = of(eqtlPhenotypeMock),
  platformService = browserPlatform,
}: {
  phenotypeResponse?: Observable<EqtlPhenotype>;
  platformService?: typeof browserPlatform;
} = {}) {
  const mockEqtlPhenotypeService = {
    getEqtlPhenotype: jest.fn().mockReturnValue(phenotypeResponse),
  };
  const component = await render(SidebarComponent, {
    providers: [
      provideNoopAnimations(),
      { provide: PlatformService, useValue: platformService },
      { provide: EqtlPhenotypeService, useValue: mockEqtlPhenotypeService },
    ],
  });
  return { component, mockEqtlPhenotypeService };
}

function queryImpactPlots() {
  const impactPanel = screen.getByRole('tabpanel');
  return {
    boxplot: within(impactPanel).queryByRole('figure', {
      name: `${IMPACT_BOXPLOT_TITLE_PREFIX} (Astrocyte)`,
    }),
    forestPlot: within(impactPanel).queryByRole('figure', { name: IMPACT_FOREST_PLOT_TITLE }),
  };
}

function getChicletTexts(): string[] {
  const chicletList = screen.getByRole('list', { name: SIDEBAR_CHICLET_LIST_ARIA_LABEL });
  return within(chicletList)
    .getAllByRole('listitem')
    .map((chiclet) => chiclet.textContent?.replace(/\s+/g, ' ').trim() ?? '');
}

describe('SidebarComponent', () => {
  it('lists the four chiclets with their label/value pairs, as a named list', async () => {
    await setup();
    expect(getChicletTexts()).toEqual([
      'variant: rs29475839',
      'gene: PAK1',
      'cell class: Astrocyte',
      'cell subclass: Astrocyte',
    ]);
  });

  it('shows the impact details in the Impact tab, with the same cell class as the chiclet', async () => {
    await setup();
    const impactPanel = screen.getByRole('tabpanel');
    const cellClassTerm = within(impactPanel).getByText('Cell Class');
    expect(within(impactPanel).getAllByRole('term')).toHaveLength(IMPACT_DETAIL_FIELDS.length);
    expect(cellClassTerm.nextElementSibling).toHaveTextContent('Astrocyte');
  });

  it('requests the phenotype data of the eQTL', async () => {
    const { mockEqtlPhenotypeService } = await setup();
    expect(mockEqtlPhenotypeService.getEqtlPhenotype).toHaveBeenCalledWith(
      SIDEBAR_PLACEHOLDER_PHENOTYPE_QUERY,
    );
  });

  it('shows the expression-by-genotype boxplot in the Impact tab, for the same cell subclass as the chiclet', async () => {
    await setup();
    expect(queryImpactPlots().boxplot).toBeVisible();
  });

  it('shows the effect sizes by cell class plot in the Impact tab', async () => {
    await setup();
    expect(queryImpactPlots().forestPlot).toBeVisible();
  });

  it('does not request the phenotype data or show the plots on the server', async () => {
    const { mockEqtlPhenotypeService } = await setup({ platformService: serverPlatform });
    expect(mockEqtlPhenotypeService.getEqtlPhenotype).not.toHaveBeenCalled();
    expect(queryImpactPlots()).toEqual({ boxplot: null, forestPlot: null });
  });

  it('reports a failed phenotype request and leaves out the plots', async () => {
    const error = new HttpRequestError(
      new HttpErrorResponse({
        status: HttpStatusCode.NotFound,
        url: 'https://xqtlexplorer.adknowledgeportal.org/api/v1/eqtl-phenotype',
      }),
      'GET',
    );
    const loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();

    await setup({ phenotypeResponse: throwError(() => error) });

    expect(loggerError).toHaveBeenCalledWith(
      formatHttpErrorReportMessage(EQTL_PHENOTYPE_ERROR_MESSAGE, error),
      expect.objectContaining({ error }),
    );
    expect(queryImpactPlots()).toEqual({ boxplot: null, forestPlot: null });
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
