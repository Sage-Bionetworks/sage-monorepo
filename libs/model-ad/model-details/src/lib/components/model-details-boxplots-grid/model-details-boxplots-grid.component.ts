import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, computed, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { DecodeGreekEntityPipe } from '@sagebionetworks/explorers/util';
import { BreakpointConfigService } from '@sagebionetworks/explorers/services';
import { IndividualData, ModelData } from '@sagebionetworks/model-ad/api-client-angular';
import { ModelDetailsBoxplotComponent } from '../model-details-boxplot/model-details-boxplot.component';

@Component({
  selector: 'model-ad-model-details-boxplots-grid',
  imports: [ModelDetailsBoxplotComponent, DecodeGreekEntityPipe],
  templateUrl: './model-details-boxplots-grid.component.html',
  styleUrls: ['./model-details-boxplots-grid.component.scss'],
})
export class ModelDetailsBoxplotsGridComponent {
  private readonly breakpointObserver = inject(BreakpointObserver);
  private readonly breakpointConfigService = inject(BreakpointConfigService);

  modelDataList = input.required<ModelData[]>();
  genotypeOrder = input<string[] | undefined>();
  sexes = input.required<IndividualData.SexEnum[]>();
  title = input.required<string>();

  private readonly observeLargeScreen = toSignal(
    this.breakpointObserver.observe(`(min-width: ${this.breakpointConfigService.largeBreakpoint})`),
    { initialValue: { matches: false, breakpoints: {} } },
  );

  legendIndex = computed(() => {
    const totalPlots = this.modelDataList().length;
    const isLargeScreen = this.observeLargeScreen().matches ?? false;

    if (!isLargeScreen) {
      // 1-column layout: legend on last plot
      return totalPlots - 1;
    } else {
      // 2-column layout: legend on leftmost of last row
      return totalPlots % 2 === 0 ? totalPlots - 2 : totalPlots - 1;
    }
  });
}
