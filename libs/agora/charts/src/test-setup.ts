import '@testing-library/jest-dom';
import {
  mockElementClientSize,
  mockResizeObserver,
  mockSvgGetBBox,
} from '@sagebionetworks/explorers/testing/jsdom';
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

mockElementClientSize();
mockResizeObserver();
mockSvgGetBBox();
