/* eslint-disable @angular-eslint/no-output-on-prefix */
import { CommonModule } from '@angular/common';
import {
  Component,
  computed,
  inject,
  signal,
  viewChildren,
  ViewEncapsulation,
} from '@angular/core';
import { HeatmapDetailsPanelData } from '@sagebionetworks/explorers/models';
import { HelperService } from '@sagebionetworks/explorers/services';
import { Popover, PopoverModule } from 'primeng/popover';

@Component({
  selector: 'explorers-heatmap-details-panel',
  imports: [CommonModule, PopoverModule],
  templateUrl: './heatmap-details-panel.component.html',
  styleUrls: ['./heatmap-details-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class HeatmapDetailsPanelComponent {
  helperService = inject(HelperService);
  panels = viewChildren(Popover);

  /**
   * Use a double buffering approach: Two popover panels alternate to ensure smooth transitions.
   * When showing new data, we update the inactive panel's data, switch the active index,
   * hide the old panel, and show the new one. This prevents flickering that would occur
   * if we updated and repositioned a single panel while it's visible.
   */
  panelData = signal<[HeatmapDetailsPanelData, HeatmapDetailsPanelData]>([{}, {}]);
  activePanelIndex = signal(0);

  data = computed(() => this.panelData()[this.activePanelIndex()]);

  private lastEventTarget: EventTarget | null = null;

  show(event: Event, data: HeatmapDetailsPanelData = {}) {
    const currentIndex = this.activePanelIndex();
    const newIndex = currentIndex === 0 ? 1 : 0;

    this.panelData.update(([d0, d1]) => (newIndex === 0 ? [data, d1] : [d0, data]));
    this.activePanelIndex.set(newIndex);

    this.panels()[currentIndex]?.hide();

    this.lastEventTarget = event?.target ?? null;
    const target = event?.target ?? document.createElement('span');
    this.panels()[newIndex]?.show(event ?? new Event('click'), target);
  }

  hide() {
    this.panels().forEach((panel) => panel?.hide());
    this.lastEventTarget = null;
  }

  toggle(event: Event, data?: HeatmapDetailsPanelData) {
    const isVisible = this.panels().some((p) => p?.overlayVisible);
    if (event.target === this.lastEventTarget && isVisible) {
      this.hide();
    } else {
      this.show(event, data);
    }
  }

  getSignificantFigures(n: number | null | undefined, significantDigits: number) {
    const emdash = '\u2014'; // Shift+Option+Hyphen
    if (n === null || n === undefined) return emdash;
    return this.helperService.getSignificantFigures(n, significantDigits);
  }
}
