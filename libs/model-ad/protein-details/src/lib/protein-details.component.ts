import { Component, inject } from '@angular/core';
import { ProteomicsIndividualService } from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PARAMS } from '@sagebionetworks/model-ad/config';
import { individualExpressionDetailsFromRoute } from '@sagebionetworks/model-ad/services';
import { IndividualExpressionDetailsComponent } from '@sagebionetworks/model-ad/ui';

@Component({
  selector: 'model-ad-protein-details',
  imports: [IndividualExpressionDetailsComponent],
  templateUrl: './protein-details.component.html',
  styleUrls: ['./protein-details.component.scss'],
})
export class ProteinDetailsComponent {
  private readonly proteomicsIndividualService = inject(ProteomicsIndividualService);

  readonly modality = 'Protein';
  readonly downloadFilenamePrefix = 'protein_expression_individual';

  readonly details = individualExpressionDetailsFromRoute({
    pathParam: ROUTE_PARAMS.UNIQUE_ID,
    source: 'ProteinDetailsComponent',
    fetchData: (uniqueId, query) =>
      this.proteomicsIndividualService.getProteomicsIndividual({ uniqueId, ...query }),
  });
}
