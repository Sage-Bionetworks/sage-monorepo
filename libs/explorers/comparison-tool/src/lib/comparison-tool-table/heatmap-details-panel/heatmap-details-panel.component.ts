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
   * Double buffering: Two popovers are used so we can hide one
   * while showing the other at a new position.  Otherwise, PrimeNG won't
   * reposition the new popover.
   */
  activePanelIndex = signal(0);
  panelData = signal<[HeatmapDetailsPanelData, HeatmapDetailsPanelData]>([
    { ...defaultPanelData },
    { ...defaultPanelData },
  ]);

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
    const currentIndex = this.activePanelIndex();
    const newIndex = currentIndex === 0 ? 1 : 0;

    // Update data for the next panel
    this.panelData.update((arr) => {
      const copy: [HeatmapDetailsPanelData, HeatmapDetailsPanelData] = [...arr];
      copy[newIndex] = data;
      return copy;
    });
    this.activePanelIndex.set(newIndex);

    // Hide current, show next at new position
    this.panels()[currentIndex]?.hide();
    this.panels()[newIndex]?.show(event, event.target);
  }

  private hide() {
    this.panels().forEach((panel) => panel?.hide());
  }

  /**
   * Called when a popover is hidden by PrimeNG (e.g., clicking outside).
   * Only clears service state if no panel is still visible.
   */
  onPanelHide() {
    const anyVisible = this.panels().some((p) => p?.overlayVisible);
    if (!anyVisible && this.comparisonToolService.heatmapDetailsPanelData()) {
      this.comparisonToolService.hideHeatmapDetailsPanel();
    }
  }

  getSignificantFigures(n: number | null | undefined, significantDigits: number): string | number {
    if (n === null || n === undefined) return this.EMDASH;
    return this.helperService.getSignificantFigures(n, significantDigits);
  }
}
