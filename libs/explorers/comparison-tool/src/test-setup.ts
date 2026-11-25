import '@testing-library/jest-dom';
import { mockDocumentFonts } from '@sagebionetworks/explorers/testing/jsdom';
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

mockDocumentFonts();
