import { StatCardData } from '@sagebionetworks/explorers/models';
import { render, screen } from '@testing-library/angular';
import { StatCardsComponent } from './stat-cards.component';

const icon = '/path/to/icon.svg';

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
      { iconPath: icon, header: 'Card 1' },
      { iconPath: icon, header: 'Card 2' },
      { iconPath: icon, header: 'Card 3' },
    ];
    await setup(cards);

    expect(screen.getByText('Card 1')).toBeInTheDocument();
    expect(screen.getByText('Card 2')).toBeInTheDocument();
    expect(screen.getByText('Card 3')).toBeInTheDocument();
  });

  it('should render sub-headers when provided', async () => {
    const cards: StatCardData[] = [
      { iconPath: icon, header: 'Total QTLs', subHeader: 'across 53 tissues' },
      { iconPath: icon, header: 'Studies' },
    ];
    await setup(cards);

    expect(screen.getByText('across 53 tissues')).toBeInTheDocument();
  });

  it('should expose --stat-card-count on the host element matching the card count', async () => {
    await setup([
      { iconPath: icon, header: 'a' },
      { iconPath: icon, header: 'b' },
      { iconPath: icon, header: 'c' },
      { iconPath: icon, header: 'd' },
      { iconPath: icon, header: 'e' },
    ]);
    const grid = document.querySelector('.stat-cards') as HTMLElement;
    const host = grid.parentElement as HTMLElement;
    expect(host.style.getPropertyValue('--stat-card-count')).toBe('5');
  });

  it('should handle empty cards array gracefully', async () => {
    await setup([]);
    const container = document.querySelector('.stat-cards');
    expect(container).toBeEmptyDOMElement();
  });
});
