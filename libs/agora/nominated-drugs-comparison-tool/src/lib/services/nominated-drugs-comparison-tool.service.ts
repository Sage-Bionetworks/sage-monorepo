import { Injectable } from '@angular/core';
import { NominatedDrug } from '@sagebionetworks/agora/api-client';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';

@Injectable()
export class NominatedDrugsComparisonToolService extends ComparisonToolService<NominatedDrug> {}
