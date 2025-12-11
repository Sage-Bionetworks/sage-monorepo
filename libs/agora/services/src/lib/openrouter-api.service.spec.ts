import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { OpenRouterApiService } from './openrouter-api.service';

describe('OpenRouterApiService', () => {
  let service: OpenRouterApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    // Mock the API key in process.env for tests
    process.env['OPENROUTER_API_KEY'] = 'test-api-key';

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(OpenRouterApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call OpenRouter API with correct payload', () => {
    const mockImageBase64 =
      'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==';
    const mockContext = { chartType: 'bar', gene: 'APOE' };
    const mockResponse = {
      choices: [{ message: { content: 'This is a test explanation' } }],
    };

    service.explainVisualization(mockImageBase64, mockContext).subscribe((response) => {
      expect(response).toBe('This is a test explanation');
    });

    const req = httpMock.expectOne('https://openrouter.ai/api/v1/chat/completions');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.model).toBe('google/gemini-3-pro-preview');
    expect(req.request.body.messages[0].content[0].type).toBe('text');
    expect(req.request.body.messages[0].content[1].type).toBe('image_url');

    req.flush(mockResponse);
  });
});
