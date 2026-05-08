import { provideHttpClient } from '@angular/common/http';
import { SafeHtml } from '@angular/platform-browser';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { render } from '@testing-library/angular';
import { of } from 'rxjs';
import { SvgIconComponent } from './svg-icon.component';

class MockSvgIconService {
  getSvg = jest.fn().mockReturnValue(of(null));
}

const dummyPath = '/libs/explorers/assets/icons/cog.svg';

describe('SvgIconComponent', () => {
  it('should create the component', async () => {
    const result = await render(SvgIconComponent, {
      componentInputs: { imagePath: dummyPath },
      providers: [provideHttpClient(), { provide: SvgIconService, useClass: MockSvgIconService }],
    });
    expect(result.fixture.componentInstance).toBeTruthy();
  });

  it('should load SVG through service and set as svgContent', async () => {
    const sanitizedSvg = {} as SafeHtml;
    const mockService = { getSvg: jest.fn().mockReturnValue(of(sanitizedSvg)) };

    const result = await render(SvgIconComponent, {
      componentInputs: { imagePath: dummyPath },
      providers: [provideHttpClient(), { provide: SvgIconService, useValue: mockService }],
    });

    expect(mockService.getSvg).toHaveBeenCalledWith(dummyPath);
    expect(result.fixture.componentInstance.svgContent).toBe(sanitizedSvg);
  });

  describe('accessibility', () => {
    async function renderWithAltText(altText: string) {
      return render(SvgIconComponent, {
        componentInputs: { imagePath: dummyPath, altText },
        providers: [provideHttpClient(), { provide: SvgIconService, useClass: MockSvgIconService }],
      });
    }

    function getIconElement(container: Element): HTMLElement {
      return container.querySelector('.svg-icon, .svg-icon-no-hover') as HTMLElement;
    }

    it('should expose altText as an aria-label with role="img"', async () => {
      const result = await renderWithAltText('info');
      const icon = getIconElement(result.container);
      expect(icon.getAttribute('role')).toBe('img');
      expect(icon.getAttribute('aria-label')).toBe('info');
    });

    it('should omit role and aria-label when altText is empty (decorative)', async () => {
      const result = await renderWithAltText('');
      const icon = getIconElement(result.container);
      expect(icon.getAttribute('role')).toBeNull();
      expect(icon.getAttribute('aria-label')).toBeNull();
    });
  });
});
