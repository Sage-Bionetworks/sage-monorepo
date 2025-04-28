import { render, screen } from '@testing-library/angular';
import { HomeComponent } from './home.component';

async function setup() {
  await render(HomeComponent);
}

describe('HomeComponent', () => {
  it('should display home button', async () => {
    await setup();
    const button = screen.getByRole('button', { name: /model-ad/i });
    expect(button).toBeInTheDocument();
  });
});
