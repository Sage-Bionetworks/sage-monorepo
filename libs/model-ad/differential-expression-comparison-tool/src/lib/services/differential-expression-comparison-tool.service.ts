import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import type { Proteomics, Transcriptomics } from '@sagebionetworks/model-ad/api-client';

export type DifferentialExpressionRow = Transcriptomics | Proteomics;

@Injectable()
export class DifferentialExpressionComparisonToolService extends ComparisonToolService<DifferentialExpressionRow> {}
