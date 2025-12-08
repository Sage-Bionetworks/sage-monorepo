import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import type { GeneExpression } from '@sagebionetworks/model-ad/api-client';

@Injectable()
export class GeneExpressionComparisonToolService extends ComparisonToolService<GeneExpression> {}
