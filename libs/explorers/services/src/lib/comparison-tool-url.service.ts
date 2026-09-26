import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ComparisonToolUrlParams } from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, shareReplay } from 'rxjs/operators';
import {
  deserializeComparisonToolUrlParams,
  serializeComparisonToolUrlParams,
} from './comparison-tool-url-params';

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
    shareReplay({ bufferSize: 1, refCount: true }),
    takeUntilDestroyed(this.destroyRef),
  );

  syncToUrl(state: ComparisonToolUrlParams): void {
    const currentState = this.deserialize(this.route.snapshot.queryParams);

    // Compare all tracked state properties
    if (this.isStateEqual(currentState, state)) {
      return;
    }

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

  private isStateEqual(current: ComparisonToolUrlParams, next: ComparisonToolUrlParams): boolean {
    return (
      isEqual(current.pinnedItems ?? [], next.pinnedItems ?? []) &&
      isEqual(current.categories ?? [], next.categories ?? []) &&
      isEqual(current.sortFields ?? [], next.sortFields ?? []) &&
      isEqual(current.sortOrders ?? [], next.sortOrders ?? []) &&
      isEqual(current.filterSelections ?? {}, next.filterSelections ?? {})
    );
  }

  private serialize(state: ComparisonToolUrlParams): Params {
    return serializeComparisonToolUrlParams(state, this.route.snapshot.queryParams);
  }

  private deserialize(params: Params): ComparisonToolUrlParams {
    return deserializeComparisonToolUrlParams(params);
  }
}
