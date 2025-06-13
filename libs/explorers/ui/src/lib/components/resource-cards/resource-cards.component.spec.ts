import { render, screen } from '@testing-library/angular';
import { ResourceCardsComponent } from './resource-cards.component';

async function setup(componentProperties: Partial<ResourceCardsComponent> = {}) {
  return render(ResourceCardsComponent, { componentProperties });
}

describe('ResourceCardsComponent', () => {
  it('should render one resource card for each item in cards', async () => {
    const cards = [{ title: 'Card 1' }, { title: 'Card 2' }, { title: 'Card 3' }];
    await setup({ cards });

    expect(screen.getByText(cards[0].title)).toBeInTheDocument();
    expect(screen.getByText(cards[1].title)).toBeInTheDocument();
    expect(screen.getByText(cards[2].title)).toBeInTheDocument();
  });

  it('should handle empty cards array gracefully', async () => {
    await setup({ cards: [] });
    const container = document.querySelector('.resource-cards');
    expect(container).toBeEmptyDOMElement();
  });
});
