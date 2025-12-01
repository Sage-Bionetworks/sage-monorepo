export * from './api-next-public-openapi-disease-correlation.service';
import { ApiNextPublicOpenapiDiseaseCorrelationService } from './api-next-public-openapi-disease-correlation.service';
export * from './api-next-public-openapi-gene-expression.service';
import { ApiNextPublicOpenapiGeneExpressionService } from './api-next-public-openapi-gene-expression.service';
export * from './api-next-public-openapi-model-overview.service';
import { ApiNextPublicOpenapiModelOverviewService } from './api-next-public-openapi-model-overview.service';
export * from './api-public-openapi-comparison-tool-config.service';
import { ApiPublicOpenapiComparisonToolConfigService } from './api-public-openapi-comparison-tool-config.service';
export * from './api-public-openapi-data-version.service';
import { ApiPublicOpenapiDataVersionService } from './api-public-openapi-data-version.service';
export * from './api-public-openapi-model.service';
import { ApiPublicOpenapiModelService } from './api-public-openapi-model.service';
export const APIS = [
  ApiNextPublicOpenapiDiseaseCorrelationService,
  ApiNextPublicOpenapiGeneExpressionService,
  ApiNextPublicOpenapiModelOverviewService,
  ApiPublicOpenapiComparisonToolConfigService,
  ApiPublicOpenapiDataVersionService,
  ApiPublicOpenapiModelService,
];
