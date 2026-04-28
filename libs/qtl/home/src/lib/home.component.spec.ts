import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { render, screen } from '@testing-library/angular';
import { HomeComponent } from './home.component';

async function setup() {
  const { fixture } = await render(HomeComponent, {
    providers: [provideHttpClient(), provideRouter([])],
  });

  return { fixture };
}

describe('HomeComponent', () => {
  it('should render welcome message', async () => {
    await setup();
    expect(
      screen.getByRole('heading', { name: /welcome to the qtl explorer!/i }),
    ).toBeInTheDocument();
  });
});
