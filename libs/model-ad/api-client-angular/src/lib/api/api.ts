export * from './comparisonToolConfig.service';
import { ComparisonToolConfigService } from './comparisonToolConfig.service';
export * from './dataversion.service';
import { DataversionService } from './dataversion.service';
export * from './modelOverview.service';
import { ModelOverviewService } from './modelOverview.service';
export * from './models.service';
import { ModelsService } from './models.service';
export const APIS = [
  ComparisonToolConfigService,
  DataversionService,
  ModelOverviewService,
  ModelsService,
];
