import { Injectable } from '@angular/core';
import { NominatedTarget } from '@sagebionetworks/agora/api-client';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';

@Injectable()
export class NominatedTargetsComparisonToolService extends ComparisonToolService<NominatedTarget> {}
