import { DomSanitizer } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { render, waitFor, RenderResult } from '@testing-library/angular';
import { setupServer } from 'msw/node';

import { SynapseApiService } from '@sagebionetworks/explorers/services';
import { synapseHandlers } from '@sagebionetworks/explorers/testing/msw';
import { SynapseApiServiceStub } from '@sagebionetworks/explorers/testing';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { WikiComponent } from './wiki.component';

// Mock DomSanitizer
const mockDomSanitizer = {
  bypassSecurityTrustHtml: jest.fn((html: string) => html),
};

const validWikiParams: SynapseWikiParams = {
  ownerId: 'syn25913473',
  wikiId: '612058',
};

const invalidWikiParams: SynapseWikiParams = {
  ownerId: 'syn99999999',
  wikiId: '999999',
};

// Setup MSW server
const server = setupServer(...synapseHandlers);

describe('WikiComponent', () => {
  let component: WikiComponent;
  let renderResult: RenderResult<WikiComponent>;

  async function setup(wikiParams?: SynapseWikiParams) {
    renderResult = await render(WikiComponent, {
      providers: [
        provideHttpClient(),
        SynapseApiService,
        { provide: DomSanitizer, useValue: mockDomSanitizer },
      ],
      componentInputs: {
        wikiParams,
      },
    });

    component = renderResult.fixture.componentInstance;
    return renderResult;
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
      await setup(validWikiParams);
      expect(component).toBeTruthy();
    });
  });

  describe('Successful Data Loading', () => {
    it('should load and process wiki data correctly', async () => {
      await setup(validWikiParams);

      await waitFor(() => {
        expect(component.data).toBeDefined();
        expect(component.data?.markdown).toBe(
          '<h1>Test Wiki Content</h1><p>This is test content.</p>',
        );
        expect(component.isLoading).toBe(false);
      });
    });

    it('should render the processed HTML content', async () => {
      await setup(validWikiParams);

      await waitFor(() => {
        const wikiInner = renderResult.container.querySelector('.wiki-inner');
        expect(wikiInner).toBeTruthy();
        expect(component.safeHtml).toContain('<h1>Test Wiki Content</h1>');
      });
    });
  });

  describe('Error Handling', () => {
    it('should handle 404 errors gracefully', async () => {
      await setup(invalidWikiParams);

      await waitFor(() => {
        expect(component.isLoading).toBe(false);
        expect(component.safeHtml).toBe('<div class="wiki-no-data">No data found...</div>');
      });
    });
  });
});
