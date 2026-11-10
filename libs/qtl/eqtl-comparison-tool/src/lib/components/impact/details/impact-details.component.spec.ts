import { render, screen } from '@testing-library/angular';
import { IMPACT_DETAIL_FIELDS, ImpactDetailsComponent } from './impact-details.component';
import { IMPACT_DETAILS_EXAMPLE } from './impact-details.constants';

async function setup(details = IMPACT_DETAILS_EXAMPLE) {
  return render(ImpactDetailsComponent, { componentInputs: { details } });
}

describe('ImpactDetailsComponent', () => {
  it('lists each detail label as a term, followed by its value as the definition', async () => {
    await setup();
    const terms = screen.getAllByRole('term');
    const pairs = terms.map((term) => [
      term.textContent?.trim(),
      term.nextElementSibling?.textContent?.trim(),
    ]);
    expect(pairs).toEqual(
      IMPACT_DETAIL_FIELDS.map(({ key, label }) => [label, IMPACT_DETAILS_EXAMPLE[key]]),
    );
    terms.forEach((term) => expect(term.nextElementSibling).toHaveRole('definition'));
  });
});
