import { HttpContext } from '@angular/common/http';
import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  LoggerService,
  PlatformService,
  SUPPRESS_ERROR_OVERLAY,
} from '@sagebionetworks/explorers/services';
import {
  ModelIdentifierType,
  TranscriptomicsIndividual,
  TranscriptomicsIndividualFilterQuery,
  TranscriptomicsIndividualService,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { IndividualExpressionDetailsComponent } from '@sagebionetworks/model-ad/ui';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'model-ad-gene-details',
  imports: [IndividualExpressionDetailsComponent],
  templateUrl: './gene-details.component.html',
  styleUrls: ['./gene-details.component.scss'],
})
export class GeneDetailsComponent implements OnInit {
  route = inject(ActivatedRoute);
  router = inject(Router);
  transcriptomicsIndividualService = inject(TranscriptomicsIndividualService);
  destroyRef = inject(DestroyRef);
  platformService = inject(PlatformService);
  private readonly logger = inject(LoggerService);

  readonly modality = 'RNA';
  readonly downloadFilenamePrefix = 'transcriptomics_individual';

  isLoading = signal(true);

  transcriptomicsIndividualData = signal<TranscriptomicsIndividual[] | undefined>(undefined);
  tissue = signal<string | null>(null);
  modelIdentifier = signal<string | null>(null);

  reset() {
    this.transcriptomicsIndividualData.set(undefined);
    this.tissue.set(null);
    this.modelIdentifier.set(null);
    this.isLoading.set(true);
  }

  ngOnInit() {
    combineLatest([this.route.paramMap, this.route.queryParamMap])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(([params, queryParams]) => {
        this.reset();

        // only fetch data during client hydration
        if (this.platformService.isBrowser) {
          this.loadTranscriptomicsIndividualData(params, queryParams);
        }
      });
  }

  private loadTranscriptomicsIndividualData(params: ParamMap, queryParams: ParamMap) {
    const ensemblGeneId = params.get('ensemblGeneId');
    const modelName = queryParams.get('model');
    const modelGroup = queryParams.get('modelGroup');
    const tissue = queryParams.get('tissue');
    this.tissue.set(tissue);

    const modelIdentifierType = modelGroup
      ? ModelIdentifierType.ModelGroup
      : ModelIdentifierType.Name;
    const modelIdentifier = modelGroup || modelName;
    this.modelIdentifier.set(modelIdentifier);

    if (ensemblGeneId && tissue && modelIdentifier) {
      const query: TranscriptomicsIndividualFilterQuery = {
        ensemblGeneId,
        tissue,
        modelIdentifierType,
        modelIdentifier,
      };

      this.transcriptomicsIndividualService
        .getTranscriptomicsIndividual(query, 'body', false, {
          context: new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true),
        })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (transcriptomicsIndividualData: TranscriptomicsIndividual[]) => {
            this.transcriptomicsIndividualData.set(transcriptomicsIndividualData);
            this.isLoading.set(false);
          },
          error: () => {
            this.isLoading.set(false);
            this.logger.log(
              `GeneDetailsComponent: loadTranscriptomicsIndividualData: query: ${JSON.stringify(query)}, redirecting`,
            );
            this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
          },
        });
    } else {
      this.isLoading.set(false);
      this.logger.log(
        `GeneDetailsComponent: loadTranscriptomicsIndividualData: ensemblGeneId: ${ensemblGeneId} modelIdentifierType: ${modelIdentifierType} modelIdentifier: ${modelIdentifier}, redirecting`,
      );
      this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
    }
  }
}
