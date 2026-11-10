import { render, screen, within } from '@testing-library/angular';
import { ImpactDetailsComponent } from './impact-details.component';
import { IMPACT_DETAILS_EXAMPLE } from './impact-details.constants';

async function setup(details = IMPACT_DETAILS_EXAMPLE) {
  return render(ImpactDetailsComponent, { componentInputs: { details } });
}

describe('ImpactDetailsComponent', () => {
  it('renders each detail label next to its value, in design order', async () => {
    await setup();
    const rowTexts = screen.getAllByRole('row').map((row) =>
      within(row)
        .getAllByRole('cell')
        .map((cell) => cell.textContent?.trim()),
    );
    expect(rowTexts).toEqual([
      ['Beta', IMPACT_DETAILS_EXAMPLE.beta],
      ['P-Value', IMPACT_DETAILS_EXAMPLE.pValue],
      ['Variant Loc.', IMPACT_DETAILS_EXAMPLE.variantLocation],
      ['Gene Loc.', IMPACT_DETAILS_EXAMPLE.geneLocation],
      ['Cell Class', IMPACT_DETAILS_EXAMPLE.cellClass],
    ]);
  });

  it('renders no header row', async () => {
    await setup();
    expect(screen.queryByRole('columnheader')).not.toBeInTheDocument();
  });
});
