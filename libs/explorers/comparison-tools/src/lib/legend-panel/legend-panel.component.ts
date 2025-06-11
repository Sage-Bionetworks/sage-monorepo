import { Component, ViewEncapsulation, inject, input } from '@angular/core';
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

  colorChartLowerLabel = input('');
  colorChartUpperLabel = input('');
  colorChartText = input('');
  sizeChartLowerLabel = input('');
  sizeChartUpperLabel = input('');
  sizeChartText = input('');

  onHowToClick() {
    //TODO implement
  }
}
