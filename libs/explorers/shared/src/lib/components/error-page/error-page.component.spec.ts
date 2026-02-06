import { render, screen } from '@testing-library/angular';
import { ErrorPageComponent } from './error-page.component';
import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

type QueryParams = Record<string, any>;

async function setup(options?: { supportEmail?: string | null; queryParams?: QueryParams }) {
  const supportEmail = options?.supportEmail ?? 'support@example.com';
  const queryParams = options?.queryParams ?? {};

  const { fixture } = await render(ErrorPageComponent, {
    componentInputs: { supportEmail },
    providers: [
      {
        provide: ActivatedRoute,
        useValue: { queryParams: of(queryParams) },
      },
    ],
  });
  const component = fixture.componentInstance;
  fixture.detectChanges();
  return { fixture, component };
}

describe('ErrorPageComponent', () => {
  it('should render the heading', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(/Something went wrong/i);
  });

  it('should display the error message from query params', async () => {
    await setup({
      queryParams: { message: 'Test Error Message', retryUrl: '/retry-url' },
    });
    expect(screen.getByText('Test Error Message')).toBeInTheDocument();
  });

  it('should have retry button', async () => {
    await setup({
      queryParams: { retryUrl: '/retry-url' },
    });
    // The retry button should be present (by role or text)
    const retryButton = screen.getByRole('button', { name: /Retry/i });
    expect(retryButton).toBeInTheDocument();
  });

  it('should not have retry button if retry url not specified', async () => {
    await setup();
    // The retry button should not be present when no retryUrl is provided
    const retryButton = screen.queryByRole('button', { name: /Retry/i });
    expect(retryButton).not.toBeInTheDocument();
  });

  it('should have reload button', async () => {
    await setup();
    // The reload button should be present (by role or text)
    const reloadButton = screen.getByRole('button', { name: /Reload/i });
    expect(reloadButton).toBeInTheDocument();
  });

  it('should display the support email', async () => {
    await setup();
    expect(screen.getByText('support@example.com')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'support@example.com' })).toHaveAttribute(
      'href',
      'mailto:support@example.com',
    );
  });
});
