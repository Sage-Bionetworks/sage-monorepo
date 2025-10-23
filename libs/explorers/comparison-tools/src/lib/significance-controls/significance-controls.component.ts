import { Component, inject, ViewEncapsulation } from '@angular/core';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolFilterService } from '@sagebionetworks/explorers/services';
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { FormsModule } from '@angular/forms';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { PopoverModule } from 'primeng/popover';
import { ComparisonToolColumnSelectorComponent } from '../comparison-tool-controls/comparison-tool-column-selector/comparison-tool-column-selector.component';

@Component({
  selector: 'explorers-significance-controls',
  imports: [
    FormsModule,
    TooltipModule,
    ToggleSwitchModule,
    SvgIconComponent,
    PopoverModule,
    ComparisonToolColumnSelectorComponent,
  ],
  templateUrl: './significance-controls.component.html',
  styleUrls: ['./significance-controls.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SignificanceControlsComponent {
  comparisonToolFilterService = inject(ComparisonToolFilterService);

  get significanceThreshold() {
    return this.comparisonToolFilterService.significanceThreshold();
  }

  updateSignificanceThreshold(value: number) {
    this.comparisonToolFilterService.setSignificanceThreshold(value);
  }

  get significanceThresholdActive() {
    return this.comparisonToolFilterService.significanceThresholdActive();
  }

  updateSignificanceThresholdActive(active: boolean) {
    this.comparisonToolFilterService.setSignificanceThresholdActive(active);
  }
}
