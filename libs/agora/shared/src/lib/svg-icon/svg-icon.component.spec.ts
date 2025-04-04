import { render, RenderResult, screen } from '@testing-library/angular';
import { SvgIconComponent } from './svg-icon.component';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

describe('SvgIconComponent', () => {
  let httpMock: HttpTestingController;
  let renderResult: RenderResult<SvgIconComponent>;
  let component: SvgIconComponent;

  beforeEach(async () => {
    renderResult = await render(SvgIconComponent, {
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    component = renderResult.fixture.componentInstance as SvgIconComponent;
    httpMock = renderResult.fixture.debugElement.injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', async () => {
    expect(component).toBeTruthy();
  });

  it('should not load SVG if imagePath is invalid', async () => {
    // Test valid path
    expect(component.isValidImagePath('/agora-assets/icons/cog.svg')).toBe(true);

    // Test invalid paths
    expect(component.isValidImagePath('https://external-domain.com/icon.svg')).toBe(false);
    expect(component.isValidImagePath('/some-other-path/icon.svg')).toBe(false);
    expect(component.isValidImagePath('')).toBe(false);
  });

  it('should load SVG and sanitize it', async () => {
    const mockSvg = '<svg><circle cx="50" cy="50" r="40" /></svg>';
    const validPath = '/agora-assets/icons/cog.svg';
    component.imagePath = validPath;

    // Call the function
    const loadPromise = component.loadSVG();

    // Expect an HTTP GET request
    const req = httpMock.expectOne(validPath);
    expect(req.request.method).toBe('GET');

    // Flush the mock SVG content
    req.flush(mockSvg);

    // Await the promise to let it resolve
    await loadPromise;

    // Assert that the sanitized content is set (not null)
    expect(component.svgContent).not.toBeNull();
  });
});
