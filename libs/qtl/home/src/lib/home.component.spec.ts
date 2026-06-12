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

  it('should apply the home background image', async () => {
    const { fixture } = await setup();
    const homePage = fixture.nativeElement.querySelector('#home-page') as HTMLElement;
    expect(homePage.style.backgroundImage).toContain('qtl-assets/images/home-background.svg');
  });
});
