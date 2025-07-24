import { Component, OnInit, signal } from '@angular/core';
import {
  BaseComparisonToolComponent,
  ComparisonToolHeaderComponent,
} from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { DiseaseCorrelationHelpLinksComponent } from './components/disease-correlation-help-links/disease-correlation-help-links.component';

@Component({
  selector: 'model-ad-disease-correlation-comparison-tool',
  imports: [
    BaseComparisonToolComponent,
    ComparisonToolHeaderComponent,
    DiseaseCorrelationHelpLinksComponent,
  ],
  templateUrl: './disease-correlation-comparison-tool.component.html',
  styleUrls: ['./disease-correlation-comparison-tool.component.scss'],
  providers: [ComparisonToolService],
})
export class DiseaseCorrelationComparisonToolComponent implements OnInit {
  isLoading = signal(true);
  resultsCount = signal(40000);

  ngOnInit() {
    // TODO - Replace with actual data fetching logic
    setTimeout(() => {
      this.isLoading.set(false);
    }, 300);
  }
}
