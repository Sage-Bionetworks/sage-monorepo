import { Component, inject } from '@angular/core';
import { TranscriptomicsIndividualService } from '@sagebionetworks/model-ad/api-client';
import { individualExpressionDetailsFromRoute } from '@sagebionetworks/model-ad/services';
import { IndividualExpressionDetailsComponent } from '@sagebionetworks/model-ad/ui';

@Component({
  selector: 'model-ad-gene-details',
  imports: [IndividualExpressionDetailsComponent],
  templateUrl: './gene-details.component.html',
  styleUrls: ['./gene-details.component.scss'],
})
export class GeneDetailsComponent {
  private readonly transcriptomicsIndividualService = inject(TranscriptomicsIndividualService);

  readonly modality = 'RNA';
  readonly downloadFilenamePrefix = 'transcriptomics_individual';

  readonly details = individualExpressionDetailsFromRoute({
    pathParam: 'ensemblGeneId',
    logLabel: 'GeneDetailsComponent',
    fetchData: (ensemblGeneId, query, options) =>
      this.transcriptomicsIndividualService.getTranscriptomicsIndividual(
        { ensemblGeneId, ...query },
        'body',
        false,
        options,
      ),
  });
}
