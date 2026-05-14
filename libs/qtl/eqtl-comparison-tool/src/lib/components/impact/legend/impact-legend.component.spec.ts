import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { CELL_CLASSES } from '@sagebionetworks/qtl/config';
import { render, screen, within } from '@testing-library/angular';
import { ImpactLegendComponent } from './impact-legend.component';

async function setup({ visible = true, isBrowser = true } = {}) {
  const result = await render(ImpactLegendComponent, {
    imports: [NoopAnimationsModule],
    providers: [{ provide: PlatformService, useValue: { isBrowser, isServer: !isBrowser } }],
    componentInputs: { visible },
  });
  return { ...result, component: result.fixture.componentInstance };
}

describe('ImpactLegendComponent', () => {
  it('renders the dialog when visible and running in the browser', async () => {
    await setup();
    expect(screen.getByRole('dialog')).toBeInTheDocument();
    expect(screen.getByText('Legend')).toBeInTheDocument();
  });

  it('does not render anything when not running in the browser', async () => {
    await setup({ isBrowser: false });
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
  });

  it('does not render the dialog when visible is false', async () => {
    await setup({ visible: false });
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
  });

  it('renders Abbreviation and Description column headers', async () => {
    await setup();
    expect(screen.getByRole('columnheader', { name: 'Abbreviation' })).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: 'Description' })).toBeInTheDocument();
  });

  it('renders a row for each cell class with its short and long name', async () => {
    await setup();
    const rows = screen.getAllByRole('row').slice(1); // drop header row
    const entries = Object.entries(CELL_CLASSES);
    expect(rows).toHaveLength(entries.length);

    entries.forEach(([longName, { short }], i) => {
      const row = within(rows[i]);
      expect(row.getByText(short)).toBeInTheDocument();
      expect(row.getByText(longName)).toBeInTheDocument();
    });
  });

  it('renders the "Open in new tab" footer button', async () => {
    await setup();
    expect(screen.getByRole('button', { name: /open in new tab/i })).toBeInTheDocument();
  });
});
