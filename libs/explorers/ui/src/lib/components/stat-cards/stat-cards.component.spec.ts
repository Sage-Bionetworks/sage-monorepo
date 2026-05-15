import { provideHttpClient } from '@angular/common/http';
import { StatCardData } from '@sagebionetworks/explorers/models';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { StatCardsComponent } from './stat-cards.component';

const icon = '/path/to/icon.svg';

async function setup(cards: StatCardData[]) {
  return render(StatCardsComponent, {
    componentInputs: {
      cards,
    },
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
}

describe('StatCardsComponent', () => {
  it('should render one stat card for each item in cards', async () => {
    const cards: StatCardData[] = [
      { iconPath: icon, iconAltText: 'icon', header: 'Card 1' },
      { iconPath: icon, iconAltText: 'icon', header: 'Card 2' },
      { iconPath: icon, iconAltText: 'icon', header: 'Card 3' },
    ];
    await setup(cards);

    expect(screen.getByText('Card 1')).toBeInTheDocument();
    expect(screen.getByText('Card 2')).toBeInTheDocument();
    expect(screen.getByText('Card 3')).toBeInTheDocument();
  });

  it('should render sub-headers when provided', async () => {
    const cards: StatCardData[] = [
      {
        iconPath: icon,
        iconAltText: 'icon',
        header: 'Total QTLs',
        subHeader: 'across 53 tissues',
      },
      { iconPath: icon, iconAltText: 'icon', header: 'Studies' },
    ];
    await setup(cards);

    expect(screen.getByText('across 53 tissues')).toBeInTheDocument();
  });

  it('should expose --stat-card-count on the host element matching the card count', async () => {
    await setup([
      { iconPath: icon, iconAltText: 'icon', header: 'a' },
      { iconPath: icon, iconAltText: 'icon', header: 'b' },
      { iconPath: icon, iconAltText: 'icon', header: 'c' },
      { iconPath: icon, iconAltText: 'icon', header: 'd' },
      { iconPath: icon, iconAltText: 'icon', header: 'e' },
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
