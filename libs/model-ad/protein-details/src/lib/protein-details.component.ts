import { HttpContext } from '@angular/common/http';
import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  LoggerService,
  PlatformService,
  SUPPRESS_ERROR_OVERLAY,
} from '@sagebionetworks/explorers/services';
import { routeSnapshotChanges } from '@sagebionetworks/explorers/util';
import {
  ModelIdentifierType,
  ProteomicsIndividual,
  ProteomicsIndividualFilterQuery,
  ProteomicsIndividualService,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { IndividualExpressionDetailsComponent } from '@sagebionetworks/model-ad/ui';
import { catchError, EMPTY, Observable, switchMap, tap } from 'rxjs';

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
    routeSnapshotChanges(this.route)
      .pipe(
        tap(() => this.reset()),
        switchMap((snapshot) =>
          // only fetch data during client hydration
          this.platformService.isBrowser
            ? this.loadProteomicsIndividualData(snapshot.paramMap, snapshot.queryParamMap)
            : EMPTY,
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((proteomicsIndividualData) => {
        this.proteomicsIndividualData.set(proteomicsIndividualData);
        this.isLoading.set(false);
      });
  }

  private loadProteomicsIndividualData(
    params: ParamMap,
    queryParams: ParamMap,
  ): Observable<ProteomicsIndividual[]> {
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

    if (!uniqueId || !tissue || !modelIdentifier) {
      this.isLoading.set(false);
      this.logger.log('ProteinDetailsComponent: missing required route info, redirecting', {
        uniqueId,
        tissue,
        modelIdentifierType,
        modelIdentifier,
      });
      this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
      return EMPTY;
    }

    const query: ProteomicsIndividualFilterQuery = {
      uniqueId,
      tissue,
      modelIdentifierType,
      modelIdentifier,
    };

    return this.proteomicsIndividualService
      .getProteomicsIndividual(query, 'body', false, {
        context: new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true),
      })
      .pipe(
        catchError(() => {
          this.isLoading.set(false);
          this.logger.log(
            'ProteinDetailsComponent: proteomics individual request failed, redirecting',
            { ...query },
          );
          this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
          return EMPTY;
        }),
      );
  }
}
