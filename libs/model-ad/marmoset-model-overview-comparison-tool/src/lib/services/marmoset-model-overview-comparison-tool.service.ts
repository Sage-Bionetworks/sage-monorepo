import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { MarmosetModelOverview } from '@sagebionetworks/model-ad/api-client';

@Injectable()
export class MarmosetModelOverviewComparisonToolService extends ComparisonToolService<MarmosetModelOverview> {}
