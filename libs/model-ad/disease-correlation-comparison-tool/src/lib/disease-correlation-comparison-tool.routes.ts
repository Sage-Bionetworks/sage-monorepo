import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { DiseaseCorrelationComparisonToolComponent } from './disease-correlation-comparison-tool.component';
import { DiseaseCorrelationComparisonToolService } from './services/disease-correlation-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: DiseaseCorrelationComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: DiseaseCorrelationComparisonToolService },
      DiseaseCorrelationComparisonToolService,
      ComparisonToolFilterService,
    ],
  },
];
