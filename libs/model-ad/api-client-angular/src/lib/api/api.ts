export * from './comparison-tool-config.service';
import { ComparisonToolConfigService } from './comparison-tool-config.service';
export * from './data-version.service';
import { DataVersionService } from './data-version.service';
export * from './disease-correlation.service';
import { DiseaseCorrelationService } from './disease-correlation.service';
export * from './gene-expression.service';
import { GeneExpressionService } from './gene-expression.service';
export * from './model-legacy.service';
import { ModelLegacyService } from './model-legacy.service';
export * from './model-next.service';
import { ModelNextService } from './model-next.service';
export * from './model-overview.service';
import { ModelOverviewService } from './model-overview.service';
export const APIS = [
  ComparisonToolConfigService,
  DataVersionService,
  DiseaseCorrelationService,
  GeneExpressionService,
  ModelLegacyService,
  ModelNextService,
  ModelOverviewService,
];
