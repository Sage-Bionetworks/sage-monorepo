import { CommonModule } from '@angular/common';
import { Component, ViewEncapsulation, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'explorers-visualization-overview-panel',
  imports: [CommonModule, ButtonModule, CheckboxModule, DialogModule, FormsModule],
  templateUrl: './visualization-overview-panel.component.html',
  styleUrls: ['./visualization-overview-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class VisualizationOverviewPanelComponent {
  comparisonToolService = inject(ComparisonToolService);
  viewConfig = this.comparisonToolService.viewConfig;

  willHide = this.comparisonToolService.isVisualizationOverviewHiddenByUser;

  setWillHide(value: boolean) {
    this.comparisonToolService.setVisualizationOverviewHiddenByUser(value);
  }

  activePane = 0;

  panes = this.viewConfig().visualizationOverviewPanes;

  previous() {
    if (this.activePane > 0) {
      this.activePane--;
    }
  }

  next() {
    if (this.activePane < this.panes.length - 1) {
      this.activePane++;
    }
  }

  onHide() {
    this.activePane = 0;
  }

  toggle() {
    this.comparisonToolService.toggleVisualizationOverview();
  }

  close() {
    this.comparisonToolService.setVisualizationOverviewVisibility(false);
  }
}
