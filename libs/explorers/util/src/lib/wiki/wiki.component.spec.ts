import { DomSanitizer } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { render, waitFor, RenderResult } from '@testing-library/angular';
import { of, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { SynapseApiService } from '@sagebionetworks/explorers/services';
import { SynapseWikiMarkdown, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { synapseWikiMock } from '@sagebionetworks/explorers/testing';
import { WikiComponent } from './wiki.component';

// Mock the SynapseApiService
const mockSynapseApiService = {
  getWikiMarkdown: jest.fn(),
  renderHtml: jest.fn(),
};

// Mock DomSanitizer
const mockDomSanitizer = {
  bypassSecurityTrustHtml: jest.fn(),
};

const validWikiParams: SynapseWikiParams = {
  ownerId: 'syn25913473',
  wikiId: '612058',
};

const mockWikiData: SynapseWikiMarkdown = {
  ...synapseWikiMock,
  markdown: '<h1>Test Wiki Content</h1><p>This is test content.</p>',
};

const timerDelay = 100;

describe('WikiComponent', () => {
  let component: WikiComponent;
  let renderResult: RenderResult<WikiComponent>;

  async function setup(wikiParams?: SynapseWikiParams) {
    renderResult = await render(WikiComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SynapseApiService, useValue: mockSynapseApiService },
        { provide: DomSanitizer, useValue: mockDomSanitizer },
      ],
      componentInputs: {
        wikiParams,
      },
    });

    component = renderResult.fixture.componentInstance;
    return renderResult;
  }

  beforeEach(() => {
    jest.clearAllMocks();

    // Setup default mock implementations
    mockSynapseApiService.getWikiMarkdown.mockReturnValue(of(mockWikiData));
    mockSynapseApiService.renderHtml.mockReturnValue('<h1>Rendered HTML</h1>');
    mockDomSanitizer.bypassSecurityTrustHtml.mockReturnValue('<h1>Safe HTML</h1>');
  });

  describe('Component Initialization', () => {
    it('should create the component', async () => {
      await setup(validWikiParams);
      expect(component).toBeTruthy();
    });

    it('should initialize with loading state', async () => {
      // Make the service return a delayed observable to keep loading state
      mockSynapseApiService.getWikiMarkdown.mockReturnValue(
        timer(timerDelay).pipe(switchMap(() => of(mockWikiData))),
      );

      await setup(validWikiParams);
      expect(component.isLoading).toBe(true);
    });

    it('should have default safeHtml content', async () => {
      await setup();
      expect(component.safeHtml).toBe('<div class="wiki-no-data">No data found...</div>');
    });
  });

  describe('Successful Data Loading', () => {
    it('should call SynapseApiService with correct parameters', async () => {
      await setup(validWikiParams);

      await waitFor(() => {
        expect(mockSynapseApiService.getWikiMarkdown).toHaveBeenCalledWith(
          validWikiParams.ownerId,
          validWikiParams.wikiId,
        );
      });
    });

    it('should process wiki data correctly', async () => {
      await setup(validWikiParams);

      await waitFor(() => {
        expect(component.data).toEqual(mockWikiData);
        expect(mockSynapseApiService.renderHtml).toHaveBeenCalledWith(mockWikiData.markdown);
        expect(mockDomSanitizer.bypassSecurityTrustHtml).toHaveBeenCalledWith(
          '<h1>Rendered HTML</h1>',
        );
      });
    });

    it('should render the processed HTML content', async () => {
      await setup(validWikiParams);

      await waitFor(() => {
        const wikiInner = renderResult.container.querySelector('.wiki-inner');
        expect(wikiInner).toBeTruthy();
      });
    });

    it('should stop loading after successful data fetch', async () => {
      await setup(validWikiParams);

      await waitFor(() => {
        expect(component.isLoading).toBe(false);
      });
    });
  });
});
