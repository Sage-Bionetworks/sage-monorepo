import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ComparisonToolUrlParams } from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';

export const COMPARISON_TOOL_URL_SYNC_DEBOUNCE_MS = 50;

@Injectable()
export class ComparisonToolUrlService {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  readonly params$: Observable<ComparisonToolUrlParams> = this.route.queryParams.pipe(
    debounceTime(COMPARISON_TOOL_URL_SYNC_DEBOUNCE_MS),
    map((params) => this.deserialize(params)),
    distinctUntilChanged((prev, curr) => isEqual(prev, curr)),
    takeUntilDestroyed(this.destroyRef),
  );

  syncToUrl(state: ComparisonToolUrlParams): void {
    const queryParams = this.serialize(state);

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }

  clearUrl(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {},
      replaceUrl: true,
    });
  }

  private serialize(state: ComparisonToolUrlParams): Params {
    const params: Params = {};

    if (state.pinnedItems && state.pinnedItems.length > 0) {
      params['pinned'] = [...state.pinnedItems];
    } else if (state.pinnedItems !== undefined) {
      params['pinned'] = null;
    }

    return params;
  }

  private deserialize(params: Params): ComparisonToolUrlParams {
    const result: ComparisonToolUrlParams = {};

    if (params['pinned']) {
      result.pinnedItems = this.toArray(params['pinned']);
    }

    return result;
  }

  private toArray(value: string | string[] | null | undefined): string[] {
    if (Array.isArray(value)) {
      return value.map(String);
    }
    return [String(value)];
  }
}
