import '@testing-library/jest-dom';
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

// jsdom does not implement FontFaceSet, which the table's column-width measurement awaits.
if (!document.fonts) {
  Object.defineProperty(document, 'fonts', {
    value: { ready: Promise.resolve() },
  });
}
