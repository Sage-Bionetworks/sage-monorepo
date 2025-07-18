import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, computed, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { DecodeGreekEntityPipe } from '@sagebionetworks/explorers/util';
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

  modelDataList = input.required<ModelData[]>();
  sexes = input.required<IndividualData.SexEnum[]>();
  title = input.required<string>();

  private readonly defaultLargeBreakpoint = '992px';

  private readonly largeBreakpoint = (() => {
    if (typeof document !== 'undefined') {
      const breakpointValue = getComputedStyle(document.documentElement).getPropertyValue(
        '--lg-breakpoint',
      );
      return breakpointValue?.trim() || this.defaultLargeBreakpoint;
    }
    return this.defaultLargeBreakpoint;
  })();

  private readonly observeLargeScreen = toSignal(
    this.breakpointObserver.observe(`(min-width: ${this.largeBreakpoint})`),
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
