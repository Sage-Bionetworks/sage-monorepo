import { ApplicationRef, Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { render, screen } from '@testing-library/angular';
import {
  WIDEST_LINE_WIDTH_PROPERTY,
  WidestLineWidthDirective,
} from './widest-line-width.directive';

const LINE_WIDTHS = [98.4, 100.2];
const WIDEST_LINE_WIDTH = '101px';

@Component({
  imports: [WidestLineWidthDirective],
  template: `<a explorersWidestLineWidth data-testid="link"><span>Disease Correlation</span></a>`,
})
class TestHostComponent {}

let notifyResize: ResizeObserverCallback | undefined;
const disconnect = jest.fn();

class ResizeObserverStub {
  constructor(callback: ResizeObserverCallback) {
    notifyResize = callback;
  }
  observe = jest.fn();
  unobserve = jest.fn();
  disconnect = disconnect;
}

async function setup() {
  const { fixture } = await render(TestHostComponent);
  // afterNextRender callbacks, which install the resize observer, only run on tick
  TestBed.inject(ApplicationRef).tick();
  return { fixture, link: screen.getByTestId('link') };
}

describe('WidestLineWidthDirective', () => {
  beforeEach(() => {
    notifyResize = undefined;
    disconnect.mockClear();
    global.ResizeObserver = ResizeObserverStub as unknown as typeof ResizeObserver;
    Range.prototype.getClientRects = jest.fn(
      () => LINE_WIDTHS.map((width) => ({ width })) as unknown as DOMRectList,
    );
  });

  it('should set the widest line width as a custom property when the host resizes', async () => {
    const { link } = await setup();

    notifyResize?.([], {} as ResizeObserver);

    expect(link.style.getPropertyValue(WIDEST_LINE_WIDTH_PROPERTY)).toBe(WIDEST_LINE_WIDTH);
  });

  it('should leave the custom property unset when the host renders no text lines', async () => {
    Range.prototype.getClientRects = jest.fn(() => [] as unknown as DOMRectList);
    const { link } = await setup();

    notifyResize?.([], {} as ResizeObserver);

    expect(link.style.getPropertyValue(WIDEST_LINE_WIDTH_PROPERTY)).toBe('');
  });

  it('should disconnect the resize observer when destroyed', async () => {
    const { fixture } = await setup();

    fixture.destroy();

    expect(disconnect).toHaveBeenCalled();
  });
});
