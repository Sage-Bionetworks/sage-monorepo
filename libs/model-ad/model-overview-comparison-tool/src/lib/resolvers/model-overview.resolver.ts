import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
} from '@sagebionetworks/model-ad/api-client-angular';
import { ComparisonToolPages } from '@sagebionetworks/model-ad/util';

@Injectable({ providedIn: 'root' })
export class ModelOverviewResolver implements Resolve<ComparisonToolConfig | null> {
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);

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
          console.error('Failed to load ModelOverview config:', error);
          return of(null);
        }),
      );
  }
}
