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
  it('should render the home page heading', async () => {
    await setup();
    expect(
      screen.getByRole('heading', {
        name: /explore quantitative trait loci across models and studies/i,
      }),
    ).toBeInTheDocument();
  });

  it('should render the link bar', async () => {
    await setup();
    expect(screen.getByText(/view qtls by cohort, ancestry, or cell type/i)).toBeInTheDocument();
  });
});
