import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { DiseaseCorrelation } from '@sagebionetworks/model-ad/api-client';

@Injectable()
export class DiseaseCorrelationComparisonToolService extends ComparisonToolService<DiseaseCorrelation> {}
