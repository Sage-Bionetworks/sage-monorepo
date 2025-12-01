export * from './comparison-tool-config.service';
import { ComparisonToolConfigService } from './comparison-tool-config.service';
export * from './data-version.service';
import { DataVersionService } from './data-version.service';
export * from './disease-correlation.service';
import { DiseaseCorrelationService } from './disease-correlation.service';
export * from './gene-expression.service';
import { GeneExpressionService } from './gene-expression.service';
export * from './model.service';
import { ModelService } from './model.service';
export * from './model-overview.service';
import { ModelOverviewService } from './model-overview.service';
export const APIS = [
  ComparisonToolConfigService,
  DataVersionService,
  DiseaseCorrelationService,
  GeneExpressionService,
  ModelService,
  ModelOverviewService,
];
