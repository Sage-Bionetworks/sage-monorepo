import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { SvgIconService } from '@sagebionetworks/agora/services';
import { render, RenderResult } from '@testing-library/angular';
import { of } from 'rxjs';
import { SvgIconComponent } from './svg-icon.component';

// Mock SvgIconService
class MockSvgIconService {
  getSvg = jest.fn();
}

describe('SvgIconComponent', () => {
  let httpMock: HttpTestingController;
  let renderResult: RenderResult<SvgIconComponent>;
  let component: SvgIconComponent;
  let svgService: SvgIconService;
  let sanitizer: DomSanitizer;

  beforeEach(async () => {
    renderResult = await render(SvgIconComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SvgIconService, useClass: MockSvgIconService },
      ],
    });

    component = renderResult.fixture.componentInstance as SvgIconComponent;
    httpMock = renderResult.fixture.debugElement.injector.get(HttpTestingController);
    svgService = renderResult.fixture.debugElement.injector.get(SvgIconService);
    sanitizer = renderResult.fixture.debugElement.injector.get(DomSanitizer);

    jest.clearAllMocks();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', async () => {
    expect(component).toBeTruthy();
  });

  it('should load SVG through service and set as svgContent', async () => {
    const mockSvg = '<svg><circle cx="50" cy="50" r="40" /></svg>';
    const validPath = 'agora-assets/icons/cog.svg';
    const sanitizedSvg = sanitizer.bypassSecurityTrustHtml(mockSvg);

    // Spy on service method
    jest.spyOn(svgService, 'getSvg').mockReturnValue(of(sanitizedSvg));

    // Set input and trigger ngOnInit
    component.imagePath = validPath;
    component.ngOnInit();

    // Verify service was called
    expect(svgService.getSvg).toHaveBeenCalledWith(validPath);

    // Verify component state
    expect(component.svgContent).toBe(sanitizedSvg);
  });
});
