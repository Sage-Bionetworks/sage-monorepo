import { CommonModule } from '@angular/common';
import {
  Component,
  effect,
  inject,
  signal,
  untracked,
  viewChildren,
  ViewEncapsulation,
} from '@angular/core';
import { HeatmapDetailsPanelData } from '@sagebionetworks/explorers/models';
import { ComparisonToolService, HelperService } from '@sagebionetworks/explorers/services';
import { Popover, PopoverModule } from 'primeng/popover';

const defaultPanelData: HeatmapDetailsPanelData = {
  heading: '',
  subHeadings: [],
  valueLabel: '',
  footer: '',
};

@Component({
  selector: 'explorers-heatmap-details-panel',
  imports: [CommonModule, PopoverModule],
  templateUrl: './heatmap-details-panel.component.html',
  styleUrls: ['./heatmap-details-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class HeatmapDetailsPanelComponent {
  private readonly EMDASH = '\u2014'; //Shift+Option+Hyphen

  private readonly comparisonToolService = inject(ComparisonToolService);
  private readonly helperService = inject(HelperService);
  panels = viewChildren(Popover);

  /**
   * Double buffering: Two popovers alternate so that we always show a fresh,
   * hidden popover at the new position. PrimeNG's popover does not reliably
   * reposition when calling show() on an already-visible popover, so we hide
   * the current one and show the other at the new target location.
   */
  activeIndex = signal(0);
  panelData = signal<HeatmapDetailsPanelData>({ ...defaultPanelData });

  constructor() {
    effect(() => {
      const panelData = this.comparisonToolService.heatmapDetailsPanelData();
      untracked(() => {
        if (panelData) {
          this.show(panelData.event, panelData.data);
        } else {
          this.hide();
        }
      });
    });
  }

  private show(event: Event, data: HeatmapDetailsPanelData) {
    const currentIndex = this.activeIndex();
    const nextIndex = currentIndex === 0 ? 1 : 0;

    this.panelData.set(data);
    this.activeIndex.set(nextIndex);

    this.panels()[currentIndex]?.hide();
    this.panels()[nextIndex]?.show(event, event.target);
  }

  private hide() {
    this.panels().forEach((panel) => panel?.hide());
  }

  /**
   * Handles the PrimeNG popover's `onHide` event, triggered when the popover
   * closes (e.g., user clicks outside the panel).
   *
   * ## Why queueMicrotask is needed
   *
   * There's a race condition when clicking a heatmap button while a panel is open.
   * A single click triggers TWO handlers in this order:
   *
   *   1. PrimeNG's document click listener fires first → calls `onHide()`
   *   2. The button's click handler fires second → calls `showHeatmapDetailsPanel()`
   *
   * The button's handler checks if the same cell is already open (toggle logic):
   * - If the signal has data for this cell → it hides the panel (toggle off)
   * - If the signal is null → it shows a new panel (toggle on)
   *
   * ## The problem without queueMicrotask
   *
   * If `onPanelHide()` immediately clears the signal:
   *   1. onHide → signal = null (cleared too early!)
   *   2. Button click → sees signal is null → opens new panel (wrong behavior)
   *
   * Result: Clicking the same button opens a new panel instead of closing it.
   *
   * ## The solution with queueMicrotask
   *
   * `queueMicrotask` schedules the cleanup to run AFTER all synchronous handlers:
   *   1. onHide → schedules cleanup (deferred, not yet run)
   *   2. Button click → sees signal still has data → hides panel → signal = null
   *   3. Microtask runs → signal is already null → cleanup skipped (correct!)
   *
   * For non-toggle scenarios (clicking empty space), the microtask runs and
   * properly clears the signal since no button handler intervened.
   */
  onPanelHide() {
    queueMicrotask(() => {
      const anyVisible = this.panels().some((p) => p?.overlayVisible);
      if (!anyVisible && this.comparisonToolService.heatmapDetailsPanelData()) {
        this.comparisonToolService.hideHeatmapDetailsPanel();
      }
    });
  }

  getSignificantFigures(n: number | null | undefined, significantDigits: number): string | number {
    if (n === null || n === undefined) return this.EMDASH;
    return this.helperService.getSignificantFigures(n, significantDigits);
  }
}
