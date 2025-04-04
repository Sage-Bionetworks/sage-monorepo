import '@testing-library/jest-dom';
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

/**
 * Mock clientHeight and clientWidth -- Apache Echarts expects a non-zero value to be returned, but
 * jsdom will always return 0. See https://github.com/jsdom/jsdom/issues/2342 and
 * https://github.com/jsdom/jsdom/issues/2310. */
Object.defineProperty(window.HTMLElement.prototype, 'clientHeight', {
  configurable: true,
  value: jest.fn(),
});
Object.defineProperty(window.HTMLElement.prototype, 'clientWidth', {
  configurable: true,
  value: jest.fn(),
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

Object.defineProperty(global.SVGElement.prototype, 'getBBox', {
  writable: true,
  value: jest.fn().mockReturnValue({
    x: 0,
    y: 0,
  }),
});
