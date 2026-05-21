import '@testing-library/jest-dom';
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

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
