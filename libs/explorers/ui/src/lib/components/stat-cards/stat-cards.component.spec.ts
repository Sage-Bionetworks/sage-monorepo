import { StatCardData } from '@sagebionetworks/explorers/models';
import { render, screen } from '@testing-library/angular';
import { StatCardsComponent } from './stat-cards.component';

async function setup(cards: StatCardData[]) {
  return render(StatCardsComponent, {
    componentInputs: {
      cards,
    },
  });
}

describe('StatCardsComponent', () => {
  it('should render one stat card for each item in cards', async () => {
    const cards: StatCardData[] = [
      { value: '1', label: 'Card 1' },
      { value: '2', label: 'Card 2' },
      { value: '3', label: 'Card 3' },
    ];
    await setup(cards);

    expect(screen.getByText('Card 1')).toBeInTheDocument();
    expect(screen.getByText('Card 2')).toBeInTheDocument();
    expect(screen.getByText('Card 3')).toBeInTheDocument();
  });

  it('should render the value of each card', async () => {
    const cards: StatCardData[] = [
      { value: '1,234', label: 'Total QTLs' },
      { value: '42', label: 'Studies' },
    ];
    await setup(cards);

    expect(screen.getByText('1,234')).toBeInTheDocument();
    expect(screen.getByText('42')).toBeInTheDocument();
  });

  it('should handle empty cards array gracefully', async () => {
    await setup([]);
    const container = document.querySelector('.stat-cards');
    expect(container).toBeEmptyDOMElement();
  });
});
