import '@testing-library/jest-dom';
import 'jest-preset-angular/setup-jest';

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
