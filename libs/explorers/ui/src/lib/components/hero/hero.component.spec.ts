import { render, screen } from '@testing-library/angular';
import { HeroComponent } from './hero.component';

async function setup(props = {}) {
  return await render(HeroComponent, {
    componentInputs: props,
  });
}

describe('HeroComponent', () => {
  it('should display the provided title', async () => {
    const title = 'Test Hero Title';
    await setup({ title });
    expect(screen.getByText(title)).toBeInTheDocument();
  });

  it('should apply background image when provided', async () => {
    const backgroundImagePath = '/path/to/image.jpg';
    const { container } = await setup({ backgroundImagePath });
    const heroElement = container.querySelector('.hero-container');
    expect(heroElement).toHaveStyle(`background-image: url(${backgroundImagePath})`);
  });
});
