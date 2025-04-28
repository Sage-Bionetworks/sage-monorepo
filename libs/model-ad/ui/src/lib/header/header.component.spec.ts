import { render, screen } from '@testing-library/angular';
import { HeaderComponent } from './header.component';

async function setup() {
  await render(HeaderComponent);
}

describe('HeaderComponent', () => {
  it('should render text', async () => {
    await setup();
    expect(screen.getByText(/Model-AD header/i)).toBeInTheDocument();
  });
});
