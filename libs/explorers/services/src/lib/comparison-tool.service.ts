import { Injectable, signal } from '@angular/core';

@Injectable()
export class ComparisonToolService {
  columns = signal<string[]>([]);

  totalResultsCount = signal<number>(0);
  pinnedResultsCount = signal<number>(0);

  /* Legend */
  private readonly isLegendVisible$ = signal(false);

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
