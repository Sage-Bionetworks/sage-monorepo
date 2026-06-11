import { render, screen } from '@testing-library/angular';
import { FooterComponent } from './footer.component';

async function setup() {
  await render(FooterComponent);
}

describe('FooterComponent', () => {
  it('should include logo', async () => {
    await setup();
    expect(screen.getByAltText(/footer logo/i)).toBeInTheDocument();
  });

  it('should include Sage Bionetworks branding', async () => {
    await setup();
    expect(screen.getByText(/powered by/i)).toBeInTheDocument();
    expect(screen.getByAltText(/sage bionetworks logo/i)).toBeInTheDocument();
  });
});
