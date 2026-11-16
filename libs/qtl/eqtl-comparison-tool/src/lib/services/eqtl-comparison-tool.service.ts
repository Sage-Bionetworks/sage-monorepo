import { Injectable } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { Eqtl } from '@sagebionetworks/qtl/api-client';

@Injectable()
export class EqtlComparisonToolService extends ComparisonToolService<Eqtl> {}
