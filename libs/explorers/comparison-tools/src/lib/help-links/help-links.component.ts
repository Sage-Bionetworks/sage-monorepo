import { Component, output } from '@angular/core';

@Component({
  selector: 'explorers-help-links',
  templateUrl: './help-links.component.html',
  styleUrls: ['./help-links.component.scss'],
})
export class HelpLinksComponent {
  legendToggle = output<void>();
  visualizationOverviewToggle = output<void>();

  toggleLegend() {
    this.legendToggle.emit();
  }

  toggleVisualizationOverview() {
    this.visualizationOverviewToggle.emit();
  }
}
