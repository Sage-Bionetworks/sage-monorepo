import { provideHttpClient } from '@angular/common/http';
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
    expect(result.fixture.componentInstance.svgContent()).toBe(sanitizedSvg);
  });

  describe('background tile', () => {
    let renderResult: RenderResult<SvgIconComponent>;

    beforeEach(async () => {
      renderResult = await render(SvgIconComponent, {
        componentInputs: { imagePath: dummyPath },
        providers: [provideHttpClient(), { provide: SvgIconService, useClass: MockSvgIconService }],
      });
    });

    function getWrapper(): HTMLElement {
      return renderResult.container.querySelector('.svg-icon-wrapper') as HTMLElement;
    }

    it('should not apply background when backgroundColor is unset', () => {
      const wrapper = getWrapper();
      expect(wrapper.classList.contains('has-bg')).toBe(false);
      expect(wrapper.style.backgroundColor).toBe('');
      expect(wrapper.style.padding).toBe('0px');
    });

    it('should apply has-bg class and inline background-color when backgroundColor is set', () => {
      renderResult.fixture.componentRef.setInput('backgroundColor', 'rgb(255, 0, 0)');
      renderResult.fixture.detectChanges();
      const wrapper = getWrapper();
      expect(wrapper.classList.contains('has-bg')).toBe(true);
      expect(wrapper.style.backgroundColor).toBe('rgb(255, 0, 0)');
    });

    it('should not apply circle or square classes when backgroundColor is unset', () => {
      const wrapper = getWrapper();
      expect(wrapper.classList.contains('circle')).toBe(false);
      expect(wrapper.classList.contains('square')).toBe(false);
    });

    it('should apply the circle class by default when backgroundColor is set', () => {
      renderResult.fixture.componentRef.setInput('backgroundColor', 'rgb(0, 0, 0)');
      renderResult.fixture.detectChanges();
      const wrapper = getWrapper();
      expect(wrapper.classList.contains('circle')).toBe(true);
      expect(wrapper.classList.contains('square')).toBe(false);
    });

    it('should apply the square class when backgroundShape is "square" and backgroundColor is set', () => {
      renderResult.fixture.componentRef.setInput('backgroundColor', 'rgb(0, 0, 0)');
      renderResult.fixture.componentRef.setInput('backgroundShape', 'square');
      renderResult.fixture.detectChanges();
      const wrapper = getWrapper();
      expect(wrapper.classList.contains('square')).toBe(true);
      expect(wrapper.classList.contains('circle')).toBe(false);
    });

    it('should apply backgroundPadding when backgroundColor is set', () => {
      renderResult.fixture.componentRef.setInput('backgroundColor', 'rgb(0, 128, 0)');
      renderResult.fixture.componentRef.setInput('backgroundPadding', 12);
      renderResult.fixture.detectChanges();
      const wrapper = getWrapper();
      expect(wrapper.style.padding).toBe('12px');
    });

    it('should keep padding at 0 when backgroundColor is unset, even with non-zero backgroundPadding', () => {
      renderResult.fixture.componentRef.setInput('backgroundPadding', 16);
      renderResult.fixture.detectChanges();
      const wrapper = getWrapper();
      expect(wrapper.style.padding).toBe('0px');
    });
  });
});
