import { provideHttpClient } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { SynapseApiService } from '@sagebionetworks/explorers/services';
import {
  invalidWikiParam,
  provideLoadingIconColors,
  validMarkdown,
  validWikiParams,
} from '@sagebionetworks/explorers/testing';
import { server } from '@sagebionetworks/explorers/testing/msw';
import { render, waitFor } from '@testing-library/angular';
import { WikiComponent } from './wiki.component';

// Mock DomSanitizer
const mockDomSanitizer = {
  bypassSecurityTrustHtml: jest.fn((html: string) => html),
};

describe('WikiComponent', () => {
  async function setup(wikiParams?: SynapseWikiParams) {
    const result = await render(WikiComponent, {
      providers: [
        provideHttpClient(),
        SynapseApiService,
        { provide: DomSanitizer, useValue: mockDomSanitizer },
        provideLoadingIconColors(),
      ],
      componentInputs: {
        wikiParams,
      },
    });

    const component = result.fixture.componentInstance;
    return { result, component };
  }

  beforeAll(() => {
    // Start MSW server before all tests
    server.listen();
  });

  afterEach(() => {
    // Reset handlers after each test
    server.resetHandlers();
    jest.clearAllMocks();
  });

  afterAll(() => {
    // Clean up after all tests
    server.close();
  });

  describe('Component Initialization', () => {
    it('should create the component', async () => {
      const { component } = await setup(validWikiParams[0]);
      expect(component).toBeTruthy();
    });
  });

  describe('Successful Data Loading', () => {
    it('should load and process wiki data correctly', async () => {
      const { component } = await setup(validWikiParams[0]);

      await waitFor(() => {
        expect(component.isLoading()).toBe(false);
        expect(mockDomSanitizer.bypassSecurityTrustHtml).toHaveBeenCalled();
        expect(component.safeHtml).toContain(validMarkdown);
      });
    });

    it('should render the processed HTML content', async () => {
      const { component, result } = await setup(validWikiParams[0]);

      await waitFor(() => {
        const wikiInner = result.container.querySelector('.wiki-inner');
        expect(wikiInner).toBeTruthy();
        expect(component.safeHtml).toContain(validMarkdown);
      });
    });
  });

  describe('Error Handling', () => {
    it('should handle 404 errors gracefully', async () => {
      const { component } = await setup(invalidWikiParam);

      await waitFor(() => {
        expect(component.isLoading()).toBe(false);
        expect(component.safeHtml).toBe('<div class="wiki-no-data">No data found...</div>');
      });
    });
  });
});
