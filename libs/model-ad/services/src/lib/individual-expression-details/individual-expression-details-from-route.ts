import { DestroyRef, inject, Signal, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import {
  ErrorOverlayService,
  HttpErrorReportLevelEnum,
  isNotFoundError,
  LoggerService,
  NotFoundService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import { routeSnapshotChanges } from '@sagebionetworks/explorers/util';
import { ModelIdentifierType } from '@sagebionetworks/model-ad/api-client';
import { QUERY_PARAMS } from '@sagebionetworks/model-ad/config';
import { catchError, EMPTY, Observable, of, switchMap, tap } from 'rxjs';

export const INDIVIDUAL_EXPRESSION_DATA_ERROR_MESSAGE = 'Error loading individual expression data';

/** The item type a not-found report names, for both the gene and protein details pages. */
export const INDIVIDUAL_EXPRESSION_ITEM_TYPE = 'individual expression data';

export interface IndividualExpressionDetailsQuery {
  tissue: string;
  modelIdentifierType: ModelIdentifierType;
  modelIdentifier: string;
}

export interface IndividualExpressionDetailsFromRouteOptions<T> {
  pathParam: string;
  source: string;
  fetchData: (id: string, query: IndividualExpressionDetailsQuery) => Observable<T[]>;
}

export interface IndividualExpressionDetailsState<T> {
  isLoading: Signal<boolean>;
  data: Signal<T[] | undefined>;
  tissue: Signal<string | null>;
  modelIdentifier: Signal<string | null>;
}

export function individualExpressionDetailsFromRoute<T>({
  pathParam,
  source,
  fetchData,
}: IndividualExpressionDetailsFromRouteOptions<T>): IndividualExpressionDetailsState<T> {
  const route = inject(ActivatedRoute);
  const logger = inject(LoggerService).forSource(source);
  const platformService = inject(PlatformService);
  const errorOverlayService = inject(ErrorOverlayService);
  const notFoundService = inject(NotFoundService);
  const destroyRef = inject(DestroyRef);

  const isLoading = signal(true);
  const data = signal<T[] | undefined>(undefined);
  const tissue = signal<string | null>(null);
  const modelIdentifier = signal<string | null>(null);

  const reset = () => {
    data.set(undefined);
    tissue.set(null);
    modelIdentifier.set(null);
    isLoading.set(true);
  };

  const redirectToNotFound = (
    reason: string,
    logData: Record<string, unknown>,
  ): Observable<never> => {
    notFoundService.redirectToNotFound(logger, INDIVIDUAL_EXPRESSION_ITEM_TYPE, {
      reason,
      ...logData,
    });
    return EMPTY;
  };

  const load = (snapshot: ActivatedRouteSnapshot): Observable<T[]> => {
    const id = snapshot.paramMap.get(pathParam);
    const modelName = snapshot.queryParamMap.get(QUERY_PARAMS.MODEL);
    const modelGroup = snapshot.queryParamMap.get(QUERY_PARAMS.MODEL_GROUP);
    const tissueParam = snapshot.queryParamMap.get(QUERY_PARAMS.TISSUE);
    tissue.set(tissueParam);

    const modelIdentifierType = modelGroup
      ? ModelIdentifierType.ModelGroup
      : ModelIdentifierType.Name;
    const modelIdentifierParam = modelGroup || modelName;
    modelIdentifier.set(modelIdentifierParam);

    if (!id || !tissueParam || !modelIdentifierParam) {
      return redirectToNotFound('missing required route info', {
        [pathParam]: id,
        tissue: tissueParam,
        modelIdentifierType,
        modelIdentifier: modelIdentifierParam,
      });
    }

    const query: IndividualExpressionDetailsQuery = {
      tissue: tissueParam,
      modelIdentifierType,
      modelIdentifier: modelIdentifierParam,
    };

    return fetchData(id, query).pipe(
      switchMap((individualData) =>
        individualData.length
          ? of(individualData)
          : redirectToNotFound('no individual expression data', {
              query: { [pathParam]: id, ...query },
            }),
      ),
      catchError((error) => {
        if (isNotFoundError(error)) {
          return redirectToNotFound('individual expression data not found', {
            query: { [pathParam]: id, ...query },
          });
        }
        errorOverlayService.reportAndShow(logger, INDIVIDUAL_EXPRESSION_DATA_ERROR_MESSAGE, {
          error,
          level: HttpErrorReportLevelEnum.Error,
        });
        isLoading.set(false);
        return EMPTY;
      }),
    );
  };

  routeSnapshotChanges(route)
    .pipe(
      tap(reset),
      switchMap((snapshot) =>
        // only fetch data during client hydration
        platformService.isBrowser ? load(snapshot) : EMPTY,
      ),
      takeUntilDestroyed(destroyRef),
    )
    .subscribe((individualData) => {
      data.set(individualData);
      isLoading.set(false);
    });

  return {
    isLoading: isLoading.asReadonly(),
    data: data.asReadonly(),
    tissue: tissue.asReadonly(),
    modelIdentifier: modelIdentifier.asReadonly(),
  };
}
