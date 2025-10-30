import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import type { GeneExpression } from '../gene-expression-comparison-tool.component';

@Injectable()
export class GeneExpressionComparisonToolService extends ComparisonToolService<GeneExpression> {}
