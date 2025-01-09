import { Component, Output, EventEmitter, ViewEncapsulation } from '@angular/core';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'agora-gene-comparison-tool-legend-panel',
  imports: [DialogModule],
  templateUrl: './gene-comparison-tool-legend-panel.component.html',
  styleUrls: ['./gene-comparison-tool-legend-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneComparisonToolLegendPanelComponent {
  isActive = false;

  @Output() howToClick: EventEmitter<any> = new EventEmitter();

  toggle() {
    this.isActive = !this.isActive;
  }

  onHowToClick() {
    this.howToClick.emit(null);
  }
}
