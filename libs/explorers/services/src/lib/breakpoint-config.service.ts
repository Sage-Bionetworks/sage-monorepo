import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class BreakpointConfigService {
  private readonly defaultLargeBreakpoint = '992px';
  private _largeBreakpoint: string | null = null;

  get largeBreakpoint(): string {
    if (this._largeBreakpoint === null) {
      this._largeBreakpoint = this.computeLargeBreakpoint();
    }
    return this._largeBreakpoint;
  }

  private computeLargeBreakpoint(): string {
    if (typeof document !== 'undefined') {
      const breakpointValue = getComputedStyle(document.documentElement).getPropertyValue(
        '--lg-breakpoint',
      );
      return breakpointValue?.trim() || this.defaultLargeBreakpoint;
    }
    return this.defaultLargeBreakpoint;
  }
}
