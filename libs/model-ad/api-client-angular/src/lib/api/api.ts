export * from './comparison-tool-config.service';
import { ComparisonToolConfigService } from './comparison-tool-config.service';
export * from './dataversion.service';
import { DataversionService } from './dataversion.service';
export * from './model-overview.service';
import { ModelOverviewService } from './model-overview.service';
export * from './models.service';
import { ModelsService } from './models.service';
export const APIS = [
  ComparisonToolConfigService,
  DataversionService,
  ModelOverviewService,
  ModelsService,
];
