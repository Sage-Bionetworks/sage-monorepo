import { provideHttpClient } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { render, screen } from '@testing-library/angular';
import { NotFoundComponent } from './not-found.component';

async function setup() {
  await render(NotFoundComponent, {
    providers: [provideHttpClient()],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
  });
}

describe('NotFoundComponent', () => {
  it('should display heading', async () => {
    await setup();
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toHaveTextContent('Page Not Found');
  });
});
