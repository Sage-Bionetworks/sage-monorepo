import { DescriptionListItem } from '@sagebionetworks/explorers/models';
import { render, screen } from '@testing-library/angular';
import { DescriptionListComponent } from './description-list.component';

const VARIANT_DETAILS: DescriptionListItem[] = [
  { term: 'P-Value', description: '1.25e-08' },
  { term: 'Beta', description: 0.98 },
];

async function setup(items = VARIANT_DETAILS) {
  return render(DescriptionListComponent, { componentInputs: { items } });
}

describe('DescriptionListComponent', () => {
  it('pairs each term with its description, in order', async () => {
    await setup();
    const terms = screen.getAllByRole('term').map((term) => term.textContent?.trim());
    const descriptions = screen
      .getAllByRole('definition')
      .map((description) => description.textContent?.trim());
    expect(terms).toEqual(VARIANT_DETAILS.map(({ term }) => term));
    expect(descriptions).toEqual(VARIANT_DETAILS.map(({ description }) => String(description)));
  });

  it('places each description directly after its term', async () => {
    await setup();
    const pValueTerm = screen.getByText('P-Value');
    expect(pValueTerm).toHaveRole('term');
    expect(pValueTerm.nextElementSibling).toHaveRole('definition');
    expect(pValueTerm.nextElementSibling).toHaveTextContent('1.25e-08');
  });

  it('renders every item when a term repeats', async () => {
    const repeatedTerm = 'Note';
    const notes: DescriptionListItem[] = [
      { term: repeatedTerm, description: 'Lead variant for this gene' },
      { term: repeatedTerm, description: 'Replicated in a second cohort' },
    ];
    await setup(notes);
    expect(screen.getAllByRole('term')).toHaveLength(notes.length);
    expect(
      screen.getAllByRole('definition').map((description) => description.textContent?.trim()),
    ).toEqual(notes.map(({ description }) => description));
  });

  it('renders an empty list when there are no items', async () => {
    const { container } = await setup([]);
    expect(container.querySelector('dl')).toBeEmptyDOMElement();
  });
});
