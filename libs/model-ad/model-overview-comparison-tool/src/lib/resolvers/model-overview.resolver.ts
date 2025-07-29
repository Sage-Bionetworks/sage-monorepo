import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { Observable, EMPTY } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
} from '@sagebionetworks/model-ad/api-client-angular';
import { ComparisonToolPages } from '@sagebionetworks/model-ad/util';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';

@Injectable({ providedIn: 'root' })
export class ModelOverviewResolver implements Resolve<ComparisonToolConfig | null> {
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly router = inject(Router);

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<ComparisonToolConfig | null> {
    return this.comparisonToolConfigService
      .getComparisonToolConfig(ComparisonToolPages.ModelOverview)
      .pipe(
        // model overview only has one config (no dropdowns), so return the first one or null
        map((config) => config[0] ?? null),
        catchError((error) => {
          const attemptedUrl = _state.url;
          const errorMessage = 'Failed to load ModelOverview configuration';

          console.error(`${errorMessage}: `, error);
          this.router.navigate([ROUTE_PATHS.ERROR_PAGE], {
            queryParams: {
              message: errorMessage,
              retryUrl: attemptedUrl,
            },
          });
          return EMPTY;
        }),
      );
  }
}
