import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { Component } from '@angular/core';
import {
  formatHttpErrorReportMessage,
  ErrorOverlayService,
  HttpRequestError,
  SourceLogger,
  SynapseApiService,
} from '@sagebionetworks/explorers/services';
import { render, screen, waitFor } from '@testing-library/angular';
import { MarkdownModule } from 'ngx-markdown';
import { of, throwError } from 'rxjs';
import {
  TERMS_OF_SERVICE_ERROR_MESSAGE,
  TermsOfServiceComponent,
} from './terms-of-service.component';

// Mock SynapseApiService
class MockSynapseApiService {
  getTermsOfService = jest.fn();
}

// Mock child components used in the template
@Component({ selector: 'explorers-hero', template: '' })
class MockHeroComponent {}

@Component({ selector: 'explorers-loading-icon', template: '' })
class MockLoadingIconComponent {}

const synapseRequestError = new HttpRequestError(
  new HttpErrorResponse({
    status: HttpStatusCode.ServiceUnavailable,
    url: 'https://repo-prod.prod.sagebase.org/auth/v1/termsOfUse2/info',
  }),
  'GET',
);

describe('TermsOfServiceComponent', () => {
  afterEach(() => {
    jest.restoreAllMocks();
  });

  async function setup(mockMarkdown = 'Mock TOS Content', error = false) {
    const mockService = new MockSynapseApiService();
    if (error) {
      mockService.getTermsOfService.mockReturnValue(throwError(() => synapseRequestError));
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

  it('reports a failed Synapse request as a warning and shows the error overlay', async () => {
    const warnSpy = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
    const showRequestErrorSpy = jest.spyOn(ErrorOverlayService.prototype, 'showRequestError');

    await setup(undefined, true);

    expect(showRequestErrorSpy).toHaveBeenCalledWith(synapseRequestError);

    expect(warnSpy).toHaveBeenCalledWith(
      formatHttpErrorReportMessage(TERMS_OF_SERVICE_ERROR_MESSAGE, synapseRequestError),
      expect.any(Object),
      synapseRequestError,
    );
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

  it('uses default for heroBackgroundImagePathOrDefault when heroBackgroundImagePath is undefined', async () => {
    const { renderResult } = await setup();
    const component = renderResult.fixture.componentInstance;

    expect(component.heroBackgroundImagePath()).toBeUndefined();
    expect(component.heroBackgroundImagePathOrDefault()).toBe(
      'explorers-assets/images/background.svg',
    );
  });

  it('uses provided value for heroBackgroundImagePathOrDefault when heroBackgroundImagePath is set', async () => {
    const { renderResult } = await setup();
    const component = renderResult.fixture.componentInstance;

    renderResult.fixture.componentRef.setInput('heroBackgroundImagePath', 'custom-assets/hero.svg');

    expect(component.heroBackgroundImagePathOrDefault()).toBe('custom-assets/hero.svg');
  });
});
