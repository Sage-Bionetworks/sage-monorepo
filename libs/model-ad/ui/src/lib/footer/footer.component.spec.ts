import { render, screen } from '@testing-library/angular';
import { FooterComponent } from './footer.component';

async function setup() {
  await render(FooterComponent);
}

describe('FooterComponent', () => {
  it('should include logo', async () => {
    await setup();
    expect(screen.getByAltText(/Model-AD logo/i)).toBeInTheDocument();
  });
});
