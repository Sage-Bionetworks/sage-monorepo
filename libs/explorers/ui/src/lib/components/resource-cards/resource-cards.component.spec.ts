import { ResourceCardData } from '@sagebionetworks/explorers/models';
import { render, screen } from '@testing-library/angular';
import { ResourceCardsComponent } from './resource-cards.component';

async function setup(cards: ResourceCardData[]) {
  return render(ResourceCardsComponent, {
    componentInputs: {
      cards,
    },
  });
}

describe('ResourceCardsComponent', () => {
  it('should render one resource card for each item in cards', async () => {
    const cards: ResourceCardData[] = [
      {
        title: 'Card 1',
        description: 'Description 1',
        link: 'https://example.com/1',
        imagePath: '/img1.svg',
      },
      {
        title: 'Card 2',
        description: 'Description 2',
        link: 'https://example.com/2',
        imagePath: '/img2.svg',
      },
      {
        title: 'Card 3',
        description: 'Description 3',
        link: 'https://example.com/3',
        imagePath: '/img3.svg',
      },
    ];
    await setup(cards);

    expect(screen.getByText('Card 1')).toBeInTheDocument();
    expect(screen.getByText('Card 2')).toBeInTheDocument();
    expect(screen.getByText('Card 3')).toBeInTheDocument();
  });

  it('should handle empty cards array gracefully', async () => {
    await setup([]);
    const container = document.querySelector('.resource-cards');
    expect(container).toBeEmptyDOMElement();
  });
});
