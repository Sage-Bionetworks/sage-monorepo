import { provideHttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { provideRouter, Router } from '@angular/router';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, RenderComponentOptions, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { LinkBarComponent } from './link-bar.component';

const mockLink = 'mock-link';

@Component({ template: '<div>Dummy Content</div>' })
class DummyComponent {}

const baseProviders = [
  provideHttpClient(),
  provideRouter([{ path: mockLink, component: DummyComponent }]),
  { provide: SvgIconService, useClass: SvgIconServiceStub },
];

function renderTemplate(template: string, options: RenderComponentOptions<unknown> = {}) {
  return render(template, {
    imports: [LinkBarComponent],
    ...options,
    providers: [...baseProviders, ...(options.providers ?? [])],
  });
}

describe('LinkBarComponent', () => {
  it('should render projected text content', async () => {
    await renderTemplate(
      `<explorers-link-bar [link]="'${mockLink}'">View QTL data</explorers-link-bar>`,
    );
    expect(screen.getByText('View QTL data')).toBeInTheDocument();
  });

  it('should render projected markup content', async () => {
    await renderTemplate(
      `<explorers-link-bar [link]="'${mockLink}'"><b>Explore</b> data</explorers-link-bar>`,
    );
    const bold = screen.getByText('Explore');
    expect(bold.tagName).toBe('B');
    expect(screen.getByText(/data/)).toBeInTheDocument();
  });

  it('should render the text as a link to the provided destination', async () => {
    await renderTemplate(
      `<explorers-link-bar [link]="'${mockLink}'">View QTL data</explorers-link-bar>`,
    );
    const link = screen.getByRole('link', { name: 'View QTL data' });
    expect(link.tagName).toBe('A');
    expect(link).toHaveAttribute('href', `/${mockLink}`);
  });

  it('should navigate to the provided link when the text is clicked', async () => {
    const user = userEvent.setup();
    const { fixture } = await renderTemplate(
      `<explorers-link-bar [link]="'${mockLink}'">Go</explorers-link-bar>`,
    );
    const link = screen.getByRole('link', { name: 'Go' });
    const router = fixture.debugElement.injector.get(Router);
    await user.click(link);
    expect(router.url).toBe(`/${mockLink}`);
  });

  it('should navigate to the provided link when the arrow is clicked', async () => {
    const user = userEvent.setup();
    const { fixture } = await renderTemplate(
      `<explorers-link-bar [link]="'${mockLink}'">Go</explorers-link-bar>`,
    );
    const button = screen.getByRole('button');
    const router = fixture.debugElement.injector.get(Router);
    await user.click(button);
    expect(router.url).toBe(`/${mockLink}`);
  });

  it('should apply altText to the arrow button when provided', async () => {
    await renderTemplate(
      `<explorers-link-bar [link]="'${mockLink}'" altText="Open QTL explorer">QTL</explorers-link-bar>`,
    );
    expect(screen.getByRole('button', { name: 'Open QTL explorer' })).toBeInTheDocument();
  });

  it('should render the arrow icon via the shared SvgIconComponent', async () => {
    const { fixture } = await renderTemplate(
      `<explorers-link-bar [link]="'${mockLink}'">Go</explorers-link-bar>`,
    );
    expect(fixture.nativeElement.querySelector('explorers-svg-icon')).not.toBeNull();
  });
});
