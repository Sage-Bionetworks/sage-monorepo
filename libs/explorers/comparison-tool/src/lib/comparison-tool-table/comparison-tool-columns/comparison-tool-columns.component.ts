import {
  Component,
  computed,
  DestroyRef,
  ElementRef,
  inject,
  NgZone,
  ViewEncapsulation,
} from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { SortEvent } from 'primeng/api';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { MIN_COLUMN_WIDTH } from '../comparison-tool-table.constants';

@Component({
  selector: 'explorers-comparison-tool-columns',
  imports: [TableModule, TooltipModule],
  templateUrl: './comparison-tool-columns.component.html',
  styleUrls: ['./comparison-tool-columns.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolColumnsComponent {
  readonly comparisonToolService = inject(ComparisonToolService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly ngZone = inject(NgZone);
  private readonly elementRef = inject(ElementRef);

  selectedColumns = this.comparisonToolService.selectedColumns;
  currentConfig = this.comparisonToolService.currentConfig;
  resultsCount = this.comparisonToolService.totalResultsCount;
  /**
   * Clone multiSortMeta for PrimeNG binding to prevent it from mutating the service state.
   * PrimeNG's Table component directly mutates the multiSortMeta array when users interact
   * with column sorting (known PrimeNG behavior). Without cloning, these mutations would
   * corrupt the service's immutable state.
   */
  multiSortMeta = computed(() =>
    this.comparisonToolService.multiSortMeta().map((s) => ({ field: s.field, order: s.order })),
  );

  private resizeState: {
    startX: number;
    startWidth: number;
    columnIndex: number;
  } | null = null;
  private onResizeMoveBound: ((e: MouseEvent) => void) | null = null;
  private onResizeEndBound: (() => void) | null = null;
  private lastMousedownTime = 0;
  private lastMousedownDataKey = '';

  constructor() {
    this.destroyRef.onDestroy(() => this.cleanupResizeListeners());
  }

  sortCallback(event: SortEvent) {
    // Strip to only { field, order } to prevent PrimeNG's internal object properties
    // from causing false negatives in the service's isEqual dedup check.
    const sortMeta = event.multiSortMeta
      ? event.multiSortMeta.map((s) => ({ field: s.field, order: s.order }))
      : [];
    this.comparisonToolService.setSort(sortMeta);
    return [];
  }

  onResizeStart(event: MouseEvent, dataKey: string) {
    event.preventDefault();
    event.stopPropagation();

    // Detect double-click manually because the first mousedown adds pointer-events:none
    // via body.ct-resizing, which prevents the native dblclick event from firing.
    const now = Date.now();
    if (dataKey === this.lastMousedownDataKey && now - this.lastMousedownTime < 400) {
      this.lastMousedownTime = 0;
      this.lastMousedownDataKey = '';
      this.autoFitColumn(dataKey);
      return;
    }
    this.lastMousedownTime = now;
    this.lastMousedownDataKey = dataKey;

    const columnIndex = this.selectedColumns().findIndex((c) => c.data_key === dataKey);
    if (columnIndex === -1) return;

    const container = this.elementRef.nativeElement.closest(
      '.comparison-tool-table',
    ) as HTMLElement;
    if (!container) return;

    const currentWidth = container.style.getPropertyValue(`--ct-col-${columnIndex}-width`);
    const startWidth = parseInt(currentWidth, 10) || 150;

    this.resizeState = { startX: event.clientX, startWidth, columnIndex };

    const onMove = this.onResizeMove.bind(this);
    const onEnd = this.onResizeEnd.bind(this);
    this.onResizeMoveBound = onMove;
    this.onResizeEndBound = onEnd;

    // Add drag listeners outside Angular's zone to avoid CD on every mousemove
    this.ngZone.runOutsideAngular(() => {
      document.addEventListener('mousemove', onMove);
      document.addEventListener('mouseup', onEnd);
    });
    document.body.classList.add('ct-resizing');
  }

  private autoFitColumn(dataKey: string) {
    const columnIndex = this.selectedColumns().findIndex((c) => c.data_key === dataKey);
    if (columnIndex === -1) return;

    const container = this.elementRef.nativeElement.closest(
      '.comparison-tool-table',
    ) as HTMLElement;
    if (!container) return;

    const cssVar = `--ct-col-${columnIndex}-width`;

    // Temporarily shrink the column to 0 so all content overflows,
    // then scrollWidth reports the natural content width.
    container.style.setProperty(cssVar, '0px');
    void container.offsetWidth; // force synchronous reflow

    const cells = container.querySelectorAll(
      `th:nth-child(${columnIndex + 1}), td:nth-child(${columnIndex + 1})`,
    );
    let maxWidth = MIN_COLUMN_WIDTH;
    cells.forEach((cell) => {
      maxWidth = Math.max(maxWidth, cell.scrollWidth);
    });

    container.style.setProperty(cssVar, maxWidth + 'px');
  }

  private onResizeMove(event: MouseEvent) {
    if (!this.resizeState) return;
    const delta = event.clientX - this.resizeState.startX;
    const newWidth = Math.max(this.resizeState.startWidth + delta, MIN_COLUMN_WIDTH);
    const widthPx = newWidth + 'px';

    const container = this.elementRef.nativeElement.closest(
      '.comparison-tool-table',
    ) as HTMLElement;
    if (!container) return;
    container.style.setProperty(`--ct-col-${this.resizeState.columnIndex}-width`, widthPx);
  }

  private onResizeEnd() {
    this.resizeState = null;
    this.cleanupResizeListeners();
    document.body.classList.remove('ct-resizing');
  }

  private cleanupResizeListeners() {
    if (this.onResizeMoveBound) {
      document.removeEventListener('mousemove', this.onResizeMoveBound);
      this.onResizeMoveBound = null;
    }
    if (this.onResizeEndBound) {
      document.removeEventListener('mouseup', this.onResizeEndBound);
      this.onResizeEndBound = null;
    }
  }
}
