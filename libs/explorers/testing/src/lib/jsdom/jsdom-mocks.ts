/// <reference types="jest" />

/*
 * Stubs for browser APIs that jsdom lacks or reports as zero. Each function registers
 * beforeEach/afterEach hooks, so call it at the top level of a Jest setup file.
 */

export const MOCK_CLIENT_HEIGHT_PX = 500;
export const MOCK_CLIENT_WIDTH_PX = 100;

export function mockResizeObserver(): void {
  const originalResizeObserver = global.ResizeObserver;

  beforeEach(() => {
    global.ResizeObserver = jest.fn().mockImplementation(() => ({
      observe: jest.fn(),
      unobserve: jest.fn(),
      disconnect: jest.fn(),
    }));
  });

  afterEach(() => {
    global.ResizeObserver = originalResizeObserver;
  });
}

/**
 * Apache ECharts expects a non-zero element size, but jsdom always returns 0.
 * See https://github.com/jsdom/jsdom/issues/2342 and https://github.com/jsdom/jsdom/issues/2310.
 */
export function mockElementClientSize(): void {
  mockPrototypeProperty(window.HTMLElement.prototype, 'clientHeight', () => ({
    configurable: true,
    get: () => MOCK_CLIENT_HEIGHT_PX,
  }));
  mockPrototypeProperty(window.HTMLElement.prototype, 'clientWidth', () => ({
    configurable: true,
    get: () => MOCK_CLIENT_WIDTH_PX,
  }));
}

export function mockSvgGetBBox(): void {
  mockPrototypeProperty(global.SVGElement.prototype, 'getBBox', () => ({
    configurable: true,
    writable: true,
    value: jest.fn().mockReturnValue({ x: 0, y: 0 }),
  }));
}

function mockPrototypeProperty(
  prototype: object,
  property: string,
  createDescriptor: () => PropertyDescriptor,
): void {
  const originalDescriptor = Object.getOwnPropertyDescriptor(prototype, property);

  beforeEach(() => {
    Object.defineProperty(prototype, property, createDescriptor());
  });

  afterEach(() => {
    if (originalDescriptor) {
      Object.defineProperty(prototype, property, originalDescriptor);
    } else {
      Reflect.deleteProperty(prototype, property);
    }
  });
}
