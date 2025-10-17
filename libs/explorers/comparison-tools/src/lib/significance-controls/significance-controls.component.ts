import { Component, inject, ViewEncapsulation } from '@angular/core';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolFilterService } from '@sagebionetworks/explorers/services';
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { FormsModule } from '@angular/forms';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { PopoverModule } from 'primeng/popover';

@Component({
  selector: 'explorers-significance-controls',
  imports: [FormsModule, TooltipModule, ToggleSwitchModule, SvgIconComponent, PopoverModule],
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
