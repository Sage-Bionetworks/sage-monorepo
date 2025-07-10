import { Component, OnInit, signal } from '@angular/core';
import {
  BaseComparisonToolComponent,
  ComparisonToolHeaderComponent,
} from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { ModelOverviewHelpLinksComponent } from './components/model-overview-help-links/model-overview-help-links.component';

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
  isLoading = signal(true);
  resultsCount = signal(50000);

  ngOnInit() {
    // TODO - Replace with actual data fetching logic
    setTimeout(() => {
      this.isLoading.set(false);
    }, 300);
  }
}
