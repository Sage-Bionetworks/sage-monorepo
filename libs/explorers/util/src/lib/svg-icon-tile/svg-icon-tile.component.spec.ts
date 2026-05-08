import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { render, RenderResult } from '@testing-library/angular';
import { of } from 'rxjs';
import { SvgIconTileComponent } from './svg-icon-tile.component';

class MockSvgIconService {
  getSvg = jest.fn().mockReturnValue(of(null));
}

const dummyPath = '/libs/explorers/assets/icons/cog.svg';

describe('SvgIconTileComponent', () => {
  let renderResult: RenderResult<SvgIconTileComponent>;

  beforeEach(async () => {
    renderResult = await render(SvgIconTileComponent, {
      componentInputs: { imagePath: dummyPath, backgroundColor: 'rgb(255, 0, 0)' },
      providers: [provideHttpClient(), { provide: SvgIconService, useClass: MockSvgIconService }],
    });
  });

  function getWrapper(): HTMLElement {
    return renderResult.container.querySelector('.svg-icon-tile-wrapper') as HTMLElement;
  }

  it('should create the component', () => {
    expect(renderResult.fixture.componentInstance).toBeTruthy();
  });

  it('should apply the inline background-color', () => {
    const wrapper = getWrapper();
    expect(wrapper.style.backgroundColor).toBe('rgb(255, 0, 0)');
  });

  it('should default to the circle shape', () => {
    const wrapper = getWrapper();
    expect(wrapper.classList.contains('circle')).toBe(true);
    expect(wrapper.classList.contains('square')).toBe(false);
  });

  it('should apply the square class when shape is "square"', () => {
    renderResult.fixture.componentRef.setInput('shape', 'square');
    renderResult.fixture.detectChanges();
    const wrapper = getWrapper();
    expect(wrapper.classList.contains('square')).toBe(true);
    expect(wrapper.classList.contains('circle')).toBe(false);
  });

  it('should default padding to 8px', () => {
    const wrapper = getWrapper();
    expect(wrapper.style.padding).toBe('8px');
  });

  it('should apply the configured padding', () => {
    renderResult.fixture.componentRef.setInput('padding', 16);
    renderResult.fixture.detectChanges();
    const wrapper = getWrapper();
    expect(wrapper.style.padding).toBe('16px');
  });

  it('should render an inner explorers-svg-icon', () => {
    expect(renderResult.container.querySelector('explorers-svg-icon')).not.toBeNull();
  });
});
