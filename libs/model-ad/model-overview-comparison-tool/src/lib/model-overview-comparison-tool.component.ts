import { Component, OnInit, signal } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolService } from '@sagebionetworks/model-ad/services';
import { ModelOverviewHelpLinksComponent } from './components/model-overview-help-links/model-overview-help-links.component';
import { LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/util';
@Component({
  selector: 'model-ad-model-overview-comparison-tool',
  imports: [BaseComparisonToolComponent, ModelOverviewHelpLinksComponent],
  templateUrl: './model-overview-comparison-tool.component.html',
  styleUrls: ['./model-overview-comparison-tool.component.scss'],
  providers: [ComparisonToolService],
})
export class ModelOverviewComparisonToolComponent implements OnInit {
  isLoading = signal(true);
  loadingIconColors = signal(LOADING_ICON_COLORS);
  resultsCount = signal(50000);

  ngOnInit() {
    // TODO - Replace with actual data fetching logic
    setTimeout(() => {
      this.isLoading.set(false);
    }, 300);
  }
}
