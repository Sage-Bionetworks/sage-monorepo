import { Routes } from '@angular/router';
import { ModelOverviewComparisonToolComponent } from './model-overview-comparison-tool.component';
import { ModelOverviewResolver } from './resolvers/model-overview.resolver';

export const modelOverviewResolver = ModelOverviewResolver;

export const routes: Routes = [
  {
    path: '',
    component: ModelOverviewComparisonToolComponent,
    resolve: { comparisonToolConfig: modelOverviewResolver },
  },
];
