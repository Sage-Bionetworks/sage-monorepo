import { isPlatformBrowser, isPlatformServer } from '@angular/common';

export function runInServer(fn: () => void, platformId: object) {
  if (isPlatformServer(platformId)) {
    fn();
  }
}

export function runInBrowser(fn: () => void, platformId: object) {
  if (isPlatformBrowser(platformId)) {
    fn();
  }
}
