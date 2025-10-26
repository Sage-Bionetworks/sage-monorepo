import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { DiseaseCorrelationComparisonToolComponent } from './disease-correlation-comparison-tool.component';

export const routes: Routes = [
  {
    path: '',
    component: DiseaseCorrelationComparisonToolComponent,
    providers: [ComparisonToolService, ComparisonToolFilterService],
  },
];
