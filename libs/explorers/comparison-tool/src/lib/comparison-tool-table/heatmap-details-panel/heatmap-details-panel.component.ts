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

  /**
   * Tracks the last currentTarget for toggle detection.
   * Uses currentTarget (the element with the listener) instead of target
   * (the actual clicked element) to ensure consistent comparison regardless
   * of which child element within the button was clicked.
   */
  private lastCurrentTarget: EventTarget | null = null;

  constructor() {
    effect(() => {
      const panelData = this.comparisonToolService.heatmapDetailsPanelData();
      untracked(() => {
        if (panelData) {
          this.toggle(panelData.event, panelData.data);
        }
      });
    });
  }

  /**
   * Toggle behavior: if clicking the same target while a panel is visible, hide it.
   * Otherwise, show the panel at the new target.
   * PrimeNG handles "click outside" automatically.
   */
  toggle(event: Event, data: HeatmapDetailsPanelData) {
    const isVisible = this.panels().some((p) => p?.overlayVisible);

    if (event.currentTarget === this.lastCurrentTarget && isVisible) {
      this.hide();
    } else {
      this.show(event, data);
    }
  }

  private show(event: Event, data: HeatmapDetailsPanelData) {
    const currentIndex = this.activeIndex();
    const nextIndex = currentIndex === 0 ? 1 : 0;

    this.panelData.set(data);
    this.activeIndex.set(nextIndex);

    this.panels()[currentIndex]?.hide();
    this.panels()[nextIndex]?.show(event, event.currentTarget);
    this.lastCurrentTarget = event.currentTarget;
  }

  private hide() {
    this.panels().forEach((panel) => panel?.hide());
    this.lastCurrentTarget = null;
  }

  getSignificantFigures(n: number | null | undefined, significantDigits: number): string | number {
    if (n === null || n === undefined) return this.EMDASH;
    return this.helperService.getSignificantFigures(n, significantDigits);
  }
}
