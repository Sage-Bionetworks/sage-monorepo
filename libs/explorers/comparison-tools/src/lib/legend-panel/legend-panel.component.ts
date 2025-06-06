import { Component, ViewEncapsulation, Input, inject } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { LegendComponent } from './legend/legend.component';
import { ComparisonToolService } from '@sagebionetworks/model-ad/services';

@Component({
  selector: 'explorers-legend-panel',
  imports: [DialogModule, LegendComponent],
  templateUrl: './legend-panel.component.html',
  styleUrls: ['./legend-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LegendPanelComponent {
  comparisonToolService = inject(ComparisonToolService);

  @Input({ required: true }) colorChartLowerLabel = '';
  @Input({ required: true }) colorChartUpperLabel = '';
  @Input({ required: true }) colorChartText = '';
  @Input({ required: true }) sizeChartLowerLabel = '';
  @Input({ required: true }) sizeChartUpperLabel = '';
  @Input({ required: true }) sizeChartText = '';

  onHowToClick() {
    //TODO implement
  }
}
