export * from './comparison-tool-config.service';
import { ComparisonToolConfigService } from './comparison-tool-config.service';
export * from './data-version.service';
import { DataVersionService } from './data-version.service';
export * from './disease-correlation.service';
import { DiseaseCorrelationService } from './disease-correlation.service';
export * from './marmoset-model-overview.service';
import { MarmosetModelOverviewService } from './marmoset-model-overview.service';
export * from './model.service';
import { ModelService } from './model.service';
export * from './mouse-model-overview.service';
import { MouseModelOverviewService } from './mouse-model-overview.service';
export * from './transcriptomics.service';
import { TranscriptomicsService } from './transcriptomics.service';
export * from './transcriptomics-individual.service';
import { TranscriptomicsIndividualService } from './transcriptomics-individual.service';
export const APIS = [
  ComparisonToolConfigService,
  DataVersionService,
  DiseaseCorrelationService,
  MarmosetModelOverviewService,
  ModelService,
  MouseModelOverviewService,
  TranscriptomicsService,
  TranscriptomicsIndividualService,
];
