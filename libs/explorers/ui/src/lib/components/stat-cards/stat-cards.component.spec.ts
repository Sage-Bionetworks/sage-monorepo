import { provideHttpClient } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { StatCardData } from '@sagebionetworks/explorers/models';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { StatCardsComponent } from './stat-cards.component';

const icon = '/path/to/icon.svg';
const link = '/';

async function setup(cards: StatCardData[], inputs: Record<string, unknown> = {}) {
  return render(StatCardsComponent, {
    componentInputs: {
      cards,
      ...inputs,
    },
    providers: [
      provideHttpClient(),
      provideRouter([]),
      provideLocationMocks(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });
}

describe('StatCardsComponent', () => {
  it('should render one stat card for each item in cards', async () => {
    const cards: StatCardData[] = [
      { iconPath: icon, iconAltText: 'icon', header: 'Card 1', link },
      { iconPath: icon, iconAltText: 'icon', header: 'Card 2', link },
      { iconPath: icon, iconAltText: 'icon', header: 'Card 3', link },
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
        link,
      },
      { iconPath: icon, iconAltText: 'icon', header: 'Studies', link },
    ];
    await setup(cards);

    expect(screen.getByText('across 53 tissues')).toBeInTheDocument();
  });

  it('should expose --stat-card-count on the host element matching the card count', async () => {
    await setup([
      { iconPath: icon, iconAltText: 'icon', header: 'a', link },
      { iconPath: icon, iconAltText: 'icon', header: 'b', link },
      { iconPath: icon, iconAltText: 'icon', header: 'c', link },
      { iconPath: icon, iconAltText: 'icon', header: 'd', link },
      { iconPath: icon, iconAltText: 'icon', header: 'e', link },
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

  it('should apply the animate-on-load class once the component has hydrated', async () => {
    const { fixture } = await setup([{ iconPath: icon, iconAltText: 'icon', header: 'a', link }], {
      animateOnLoad: true,
    });
    await fixture.whenStable();
    fixture.detectChanges();
    expect(document.querySelector('.stat-cards.stat-cards-animate-on-load')).toBeInTheDocument();
  });

  it('should not apply the animate-on-load class by default', async () => {
    const { fixture } = await setup([{ iconPath: icon, iconAltText: 'icon', header: 'a', link }]);
    await fixture.whenStable();
    fixture.detectChanges();
    const grid = document.querySelector('.stat-cards');
    expect(grid).not.toHaveClass('stat-cards-animate-on-load');
    expect(grid).not.toHaveClass('stat-cards-prepare-animation');
  });
});
