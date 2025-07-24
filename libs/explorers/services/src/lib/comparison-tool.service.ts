import { Injectable, signal } from '@angular/core';

@Injectable()
export class ComparisonToolService {
  private readonly isLegendVisible$ = signal(false);

  /* Legend */
  isLegendVisible() {
    return this.isLegendVisible$();
  }

  setLegendVisibility(visible: boolean) {
    this.isLegendVisible$.set(visible);
  }

  toggleLegend() {
    this.isLegendVisible$.update((visible) => !visible);
  }
}
