import { Component, OnInit, signal } from '@angular/core';
import {
  BaseComparisonToolComponent,
  ComparisonToolHeaderComponent,
} from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { GeneExpressionHelpLinksComponent } from './components/gene-expression-help-links/gene-expression-help-links.component';

@Component({
  selector: 'model-ad-gene-expression-comparison-tool',
  imports: [
    BaseComparisonToolComponent,
    ComparisonToolHeaderComponent,
    GeneExpressionHelpLinksComponent,
  ],
  templateUrl: './gene-expression-comparison-tool.component.html',
  styleUrls: ['./gene-expression-comparison-tool.component.scss'],
  providers: [ComparisonToolService],
})
export class GeneExpressionComparisonToolComponent implements OnInit {
  isLoading = signal(true);
  resultsCount = signal(50000);

  ngOnInit() {
    // TODO - Replace with actual data fetching logic
    setTimeout(() => {
      this.isLoading.set(false);
    }, 300);
  }
}
