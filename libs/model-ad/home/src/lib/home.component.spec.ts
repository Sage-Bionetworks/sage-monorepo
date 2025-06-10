import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { HomeComponent } from './home.component';

async function setup() {
  await render(HomeComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });
}

describe('HomeComponent', () => {
  it('should render heading', async () => {
    await setup();
    expect(
      screen.getByRole('heading', { name: /find the right model for your research/i }),
    ).toBeInTheDocument();
  });
});
