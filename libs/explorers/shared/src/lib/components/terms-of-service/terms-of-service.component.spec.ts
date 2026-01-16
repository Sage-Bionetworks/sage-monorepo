import { Component } from '@angular/core';
import { SynapseApiService } from '@sagebionetworks/explorers/services';
import { render, screen, waitFor } from '@testing-library/angular';
import { MarkdownModule } from 'ngx-markdown';
import { of, throwError } from 'rxjs';
import { TermsOfServiceComponent } from './terms-of-service.component';

// Mock SynapseApiService
class MockSynapseApiService {
  getTermsOfService = jest.fn();
}

// Mock child components used in the template
@Component({ selector: 'explorers-hero', template: '' })
class MockHeroComponent {}

@Component({ selector: 'explorers-loading-icon', template: '' })
class MockLoadingIconComponent {}

describe('TermsOfServiceComponent', () => {
  async function setup(mockMarkdown = 'Mock TOS Content', error = false) {
    const mockService = new MockSynapseApiService();
    if (error) {
      mockService.getTermsOfService.mockReturnValue(throwError(() => new Error('Failed to load')));
    } else {
      mockService.getTermsOfService.mockReturnValue(of(mockMarkdown));
    }

    const renderResult = await render(TermsOfServiceComponent, {
      componentProviders: [{ provide: SynapseApiService, useValue: mockService }],
      imports: [MockHeroComponent, MockLoadingIconComponent, MarkdownModule.forRoot()],
    });

    return { mockService, renderResult };
  }

  it('should display the terms of service content after loading', async () => {
    const mockContent = 'Welcome to the Terms of Service!';
    await setup(mockContent);
    await waitFor(() => {
      expect(screen.getByText(mockContent)).toBeInTheDocument();
    });
  });

  it('returns undefined for heroBackgroundImagePath when no value is provided', async () => {
    const { renderResult } = await setup();
    expect(renderResult.fixture.componentInstance.heroBackgroundImagePath()).toBeUndefined();
  });

  it('uses the provided hero background when input is set', async () => {
    const { renderResult } = await setup();

    renderResult.fixture.componentRef.setInput('heroBackgroundImagePath', 'custom-assets/hero.svg');

    expect(renderResult.fixture.componentInstance.heroBackgroundImagePath()).toBe(
      'custom-assets/hero.svg',
    );
  });

  it('uses default for effectiveHeroBackgroundImagePath when heroBackgroundImagePath is undefined', async () => {
    const { renderResult } = await setup();
    const component = renderResult.fixture.componentInstance;

    expect(component.heroBackgroundImagePath()).toBeUndefined();
    expect(component.effectiveHeroBackgroundImagePath()).toBe(
      'explorers-assets/images/background.svg',
    );
  });

  it('uses provided value for effectiveHeroBackgroundImagePath when heroBackgroundImagePath is set', async () => {
    const { renderResult } = await setup();
    const component = renderResult.fixture.componentInstance;

    renderResult.fixture.componentRef.setInput('heroBackgroundImagePath', 'custom-assets/hero.svg');

    expect(component.effectiveHeroBackgroundImagePath()).toBe('custom-assets/hero.svg');
  });
});
