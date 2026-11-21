import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { render, screen, within } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { IMPACT_BOXPLOT_TITLE_PREFIX } from '../impact/boxplot/impact-boxplot.constants';
import { IMPACT_DETAIL_FIELDS } from '../impact/details/impact-details.component';
import { IMPACT_FOREST_PLOT_TITLE } from '../impact/forest-plot/impact-forest-plot.constants';
import { LEGEND_CELL_CLASSES_TITLE } from '../legend/legend.constants';
import {
  SIDEBAR_CHICLET_LIST_ARIA_LABEL,
  SIDEBAR_TABS,
  SidebarComponent,
} from './sidebar.component';

const [impactTabLabel, legendTabLabel] = SIDEBAR_TABS.map((tab) => tab.label);

async function setup() {
  return render(SidebarComponent, {
    providers: [provideNoopAnimations()],
  });
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

  it('shows the expression-by-genotype boxplot in the Impact tab, for the same cell subclass as the chiclet', async () => {
    await setup();
    const impactPanel = screen.getByRole('tabpanel');
    expect(
      within(impactPanel).getByRole('figure', {
        name: `${IMPACT_BOXPLOT_TITLE_PREFIX} (Astrocyte)`,
      }),
    ).toBeVisible();
  });

  it('shows the effect sizes by cell class plot in the Impact tab', async () => {
    await setup();
    const impactPanel = screen.getByRole('tabpanel');
    expect(
      within(impactPanel).getByRole('figure', { name: IMPACT_FOREST_PLOT_TITLE }),
    ).toBeVisible();
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
