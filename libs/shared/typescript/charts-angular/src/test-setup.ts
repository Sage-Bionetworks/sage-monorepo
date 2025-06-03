import '@testing-library/jest-dom';
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

const originalClientHeightDescriptor = Object.getOwnPropertyDescriptor(
  window.HTMLElement.prototype,
  'clientHeight',
);
const originalClientWidthDescriptor = Object.getOwnPropertyDescriptor(
  window.HTMLElement.prototype,
  'clientWidth',
);

const observeMock = jest.fn();
const unobserveMock = jest.fn();
const disconnectMock = jest.fn();
const originalResizeObserver = global.ResizeObserver;

beforeEach(() => {
  /**
   * Mock clientHeight and clientWidth -- Apache Echarts expects a non-zero value to be returned, but
   * jsdom will always return 0. See https://github.com/jsdom/jsdom/issues/2342 and
   * https://github.com/jsdom/jsdom/issues/2310. */
  Object.defineProperty(window.HTMLElement.prototype, 'clientHeight', {
    configurable: true,
    value: jest.fn().mockReturnValue(500),
  });
  Object.defineProperty(window.HTMLElement.prototype, 'clientWidth', {
    configurable: true,
    value: jest.fn().mockReturnValue(100),
  });

  global.ResizeObserver = jest.fn().mockImplementation(() => ({
    observe: observeMock,
    unobserve: unobserveMock,
    disconnect: disconnectMock,
  }));
});

afterEach(() => {
  if (originalClientHeightDescriptor) {
    Object.defineProperty(
      window.HTMLElement.prototype,
      'clientHeight',
      originalClientHeightDescriptor,
    );
  }

  if (originalClientWidthDescriptor) {
    Object.defineProperty(
      window.HTMLElement.prototype,
      'clientWidth',
      originalClientWidthDescriptor,
    );
  }

  global.ResizeObserver = originalResizeObserver;
  observeMock.mockClear();
  unobserveMock.mockClear();
  disconnectMock.mockClear();
});
