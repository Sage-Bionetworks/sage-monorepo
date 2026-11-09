export * from './comparison-tool-config.service';
import { ComparisonToolConfigService } from './comparison-tool-config.service';
export * from './data-version.service';
import { DataVersionService } from './data-version.service';
export * from './eqtl.service';
import { EqtlService } from './eqtl.service';
export const APIS = [ComparisonToolConfigService, DataVersionService, EqtlService];
