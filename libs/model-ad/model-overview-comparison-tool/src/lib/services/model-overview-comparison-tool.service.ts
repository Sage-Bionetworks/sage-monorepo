import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { ModelOverview } from '@sagebionetworks/model-ad/api-client';

@Injectable()
export class ModelOverviewComparisonToolService extends ComparisonToolService<ModelOverview> {}
