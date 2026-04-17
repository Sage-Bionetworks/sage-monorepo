import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import type { Transcriptomics } from '@sagebionetworks/model-ad/api-client';

@Injectable()
export class DifferentialExpressionComparisonToolService extends ComparisonToolService<Transcriptomics> {}
