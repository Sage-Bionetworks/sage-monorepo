import { SafeHtml } from '@angular/platform-browser';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { render, RenderResult } from '@testing-library/angular';
import { of } from 'rxjs';
import { SvgIconComponent } from './svg-icon.component';

class MockSvgIconService {
  getSvg = jest.fn().mockReturnValue(of(null));
}

const dummyPath = '/libs/explorers/assets/icons/cog.svg';

describe('SvgIconComponent', () => {
  it('should create the component without a background by default', async () => {
    const result = await render(SvgIconComponent, {
      componentInputs: { imagePath: dummyPath },
      providers: [{ provide: SvgIconService, useClass: MockSvgIconService }],
    });
    expect(result.fixture.componentInstance).toBeTruthy();
    expect(result.container.querySelector('.svg-icon-background')).toBeNull();
  });

  it('should load SVG through service and set as svgContent', async () => {
    const sanitizedSvg = {} as SafeHtml;
    const mockService = { getSvg: jest.fn().mockReturnValue(of(sanitizedSvg)) };

    const result = await render(SvgIconComponent, {
      componentInputs: { imagePath: dummyPath },
      providers: [{ provide: SvgIconService, useValue: mockService }],
    });

    expect(mockService.getSvg).toHaveBeenCalledWith(dummyPath);
    expect(result.fixture.componentInstance.svgContent).toBe(sanitizedSvg);
  });

  describe('accessibility', () => {
    async function renderWithAltText(altText: string) {
      return render(SvgIconComponent, {
        componentInputs: { imagePath: dummyPath, altText },
        providers: [{ provide: SvgIconService, useClass: MockSvgIconService }],
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

  describe('strokeWidth', () => {
    function getIconDiv(container: Element): HTMLElement {
      return container.querySelector('.svg-icon, .svg-icon-no-hover') as HTMLElement;
    }

    it('should not apply the stroke-override class or CSS variable when strokeWidth is unset', async () => {
      const result = await render(SvgIconComponent, {
        componentInputs: { imagePath: dummyPath },
        providers: [{ provide: SvgIconService, useClass: MockSvgIconService }],
      });
      const icon = getIconDiv(result.container);
      expect(icon.classList.contains('svg-icon-stroke-override')).toBe(false);
      expect(icon.style.getPropertyValue('--svg-icon-stroke-width')).toBe('');
    });

    it('should apply the stroke-override class and CSS variable when strokeWidth is set', async () => {
      const result = await render(SvgIconComponent, {
        componentInputs: { imagePath: dummyPath, strokeWidth: 2 },
        providers: [{ provide: SvgIconService, useClass: MockSvgIconService }],
      });
      const icon = getIconDiv(result.container);
      expect(icon.classList.contains('svg-icon-stroke-override')).toBe(true);
      expect(icon.style.getPropertyValue('--svg-icon-stroke-width')).toBe('2');
    });
  });

  describe('background', () => {
    let renderResult: RenderResult<SvgIconComponent>;

    beforeEach(async () => {
      renderResult = await render(SvgIconComponent, {
        componentInputs: { imagePath: dummyPath, enableBackground: true },
        providers: [{ provide: SvgIconService, useClass: MockSvgIconService }],
      });
    });

    function getBackground(): HTMLElement {
      return renderResult.container.querySelector('.svg-icon-background') as HTMLElement;
    }

    it('should render the background when enableBackground is true', () => {
      expect(getBackground()).not.toBeNull();
    });

    it('should apply a default background color', () => {
      expect(getBackground().style.backgroundColor).not.toBe('');
    });

    it('should apply a configured background color', () => {
      renderResult.fixture.componentRef.setInput('backgroundColor', 'rgb(255, 0, 0)');
      renderResult.fixture.detectChanges();
      expect(getBackground().style.backgroundColor).toBe('rgb(255, 0, 0)');
    });

    it('should default to the circle shape', () => {
      const bg = getBackground();
      expect(bg.classList.contains('circle')).toBe(true);
      expect(bg.classList.contains('square')).toBe(false);
    });

    it('should apply the square class when backgroundShape is "square"', () => {
      renderResult.fixture.componentRef.setInput('backgroundShape', 'square');
      renderResult.fixture.detectChanges();
      const bg = getBackground();
      expect(bg.classList.contains('square')).toBe(true);
      expect(bg.classList.contains('circle')).toBe(false);
    });

    it('should default backgroundPadding to 8px', () => {
      expect(getBackground().style.padding).toBe('8px');
    });

    it('should apply the configured backgroundPadding', () => {
      renderResult.fixture.componentRef.setInput('backgroundPadding', 16);
      renderResult.fixture.detectChanges();
      expect(getBackground().style.padding).toBe('16px');
    });
  });
});
