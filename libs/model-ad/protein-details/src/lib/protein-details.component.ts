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
  ProteomicsIndividual,
  ProteomicsIndividualFilterQuery,
  ProteomicsIndividualService,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { IndividualExpressionDetailsComponent } from '@sagebionetworks/model-ad/ui';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'model-ad-protein-details',
  imports: [IndividualExpressionDetailsComponent],
  templateUrl: './protein-details.component.html',
  styleUrls: ['./protein-details.component.scss'],
})
export class ProteinDetailsComponent implements OnInit {
  route = inject(ActivatedRoute);
  router = inject(Router);
  proteomicsIndividualService = inject(ProteomicsIndividualService);
  destroyRef = inject(DestroyRef);
  platformService = inject(PlatformService);
  private readonly logger = inject(LoggerService);

  readonly modality = 'Protein';
  readonly downloadFilenamePrefix = 'protein_expression_individual';

  isLoading = signal(true);

  proteomicsIndividualData = signal<ProteomicsIndividual[] | undefined>(undefined);
  tissue = signal<string | null>(null);
  modelIdentifier = signal<string | null>(null);

  reset() {
    this.proteomicsIndividualData.set(undefined);
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
          this.loadProteomicsIndividualData(params, queryParams);
        }
      });
  }

  private loadProteomicsIndividualData(params: ParamMap, queryParams: ParamMap) {
    const uniqueId = params.get('uniqueId');
    const modelName = queryParams.get('model');
    const modelGroup = queryParams.get('modelGroup');
    const tissue = queryParams.get('tissue');
    this.tissue.set(tissue);

    const modelIdentifierType = modelGroup
      ? ModelIdentifierType.ModelGroup
      : ModelIdentifierType.Name;
    const modelIdentifier = modelGroup || modelName;
    this.modelIdentifier.set(modelIdentifier);

    if (uniqueId && tissue && modelIdentifier) {
      const query: ProteomicsIndividualFilterQuery = {
        uniqueId,
        tissue,
        modelIdentifierType,
        modelIdentifier,
      };

      this.proteomicsIndividualService
        .getProteomicsIndividual(query, 'body', false, {
          context: new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true),
        })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (proteomicsIndividualData: ProteomicsIndividual[]) => {
            this.proteomicsIndividualData.set(proteomicsIndividualData);
            this.isLoading.set(false);
          },
          error: () => {
            this.isLoading.set(false);
            this.logger.log(
              `ProteinDetailsComponent: loadProteomicsIndividualData: query: ${JSON.stringify(query)}, redirecting`,
            );
            this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
          },
        });
    } else {
      this.isLoading.set(false);
      this.logger.log(
        `ProteinDetailsComponent: loadProteomicsIndividualData: uniqueId: ${uniqueId} modelIdentifierType: ${modelIdentifierType} modelIdentifier: ${modelIdentifier}, redirecting`,
      );
      this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
    }
  }
}
