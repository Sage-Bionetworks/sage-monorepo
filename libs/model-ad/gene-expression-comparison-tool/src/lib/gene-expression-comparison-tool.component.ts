import { Component, inject, OnInit, signal } from '@angular/core';
import {
  BaseComparisonToolComponent,
  ComparisonToolHeaderComponent,
} from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolService } from '@sagebionetworks/model-ad/services';
import { GeneExpressionHelpLinksComponent } from './components/gene-expression-help-links/gene-expression-help-links.component';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/util';
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
