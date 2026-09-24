import '@testing-library/jest-dom';
import { mockResizeObserver } from '@sagebionetworks/explorers/testing/jsdom';
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

mockResizeObserver();
