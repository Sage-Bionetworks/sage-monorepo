import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { MouseModelOverview } from '@sagebionetworks/model-ad/api-client';

@Injectable()
export class MouseModelOverviewComparisonToolService extends ComparisonToolService<MouseModelOverview> {}
