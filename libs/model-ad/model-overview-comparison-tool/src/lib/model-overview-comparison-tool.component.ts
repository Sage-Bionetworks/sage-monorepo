import { Component, inject, OnInit, signal } from '@angular/core';
import {
  BaseComparisonToolComponent,
  ComparisonToolHeaderComponent,
} from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolService } from '@sagebionetworks/model-ad/services';
import { ModelOverviewHelpLinksComponent } from './components/model-overview-help-links/model-overview-help-links.component';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/util';
@Component({
  selector: 'model-ad-model-overview-comparison-tool',
  imports: [
    BaseComparisonToolComponent,
    ComparisonToolHeaderComponent,
    ModelOverviewHelpLinksComponent,
  ],
  templateUrl: './model-overview-comparison-tool.component.html',
  styleUrls: ['./model-overview-comparison-tool.component.scss'],
  providers: [ComparisonToolService],
})
export class ModelOverviewComparisonToolComponent implements OnInit {
  loadingIconColors = inject(LOADING_ICON_COLORS);

  isLoading = signal(true);
  resultsCount = signal(50000);

  ngOnInit() {
    // TODO - Replace with actual data fetching logic
    setTimeout(() => {
      this.isLoading.set(false);
    }, 300);
  }
}
