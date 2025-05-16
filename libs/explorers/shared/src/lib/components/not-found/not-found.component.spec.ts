import { render, screen } from '@testing-library/angular';
import { NotFoundComponent } from './not-found.component';
import { ConfigService } from '@sagebionetworks/model-ad/config';

// Mock ConfigService
class MockConfigService {
  config = { supportEmail: 'support@example.com' };
}

async function setup() {
  const { fixture } = await render(NotFoundComponent, {
    providers: [{ provide: ConfigService, useClass: MockConfigService }],
  });

  const component = fixture.componentInstance;
  return component;
}

describe('NotFoundComponent', () => {
  it('should render the not found message', async () => {
    await setup();

    // Match text across line breaks and whitespace
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
      /This page isn't available/i,
    );
  });

  it('should display the support email from config', async () => {
    await setup();

    expect(screen.getByText('support@example.com')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'support@example.com' })).toHaveAttribute(
      'href',
      'mailto:support@example.com',
    );
  });
});
