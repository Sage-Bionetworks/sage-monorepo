import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ComparisonToolUrlParams } from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, shareReplay } from 'rxjs/operators';

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
    const currentPinned = currentState.pinnedItems ?? [];
    const nextPinned = state.pinnedItems ?? [];
    const currentCategories = currentState.categories ?? [];
    const nextCategories = state.categories ?? [];

    if (isEqual(currentPinned, nextPinned) && isEqual(currentCategories, nextCategories)) {
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

  private serialize(state: ComparisonToolUrlParams): Params {
    const params: Params = {};

    this.serializeArrayParam(params, 'pinned', state.pinnedItems);
    this.serializeArrayParam(params, 'categories', state.categories);

    return params;
  }

  private serializeArrayParam(
    params: Params,
    key: string,
    value: string[] | null | undefined,
  ): void {
    if (value && value.length > 0) {
      params[key] = value.join(',');
    } else if (value !== undefined) {
      params[key] = null;
    }
  }

  private deserialize(params: Params): ComparisonToolUrlParams {
    const result: ComparisonToolUrlParams = {};

    if (params['pinned']) {
      const pinnedItems = this.parseCommaSeparatedParam(params['pinned']);
      if (pinnedItems.length > 0) {
        result.pinnedItems = pinnedItems;
      }
    }

    if (params['categories']) {
      const categories = this.parseCommaSeparatedParam(params['categories']);
      if (categories.length > 0) {
        result.categories = categories;
      }
    }

    return result;
  }

  private parseCommaSeparatedParam(value: string | string[] | null | undefined): string[] {
    if (value == null) {
      return [];
    }

    const values = Array.isArray(value) ? value : [value];

    return values
      .flatMap((entry) => `${entry}`.split(','))
      .map((entry) => {
        try {
          return decodeURIComponent(entry);
        } catch {
          return entry;
        }
      })
      .filter((entry) => entry.length > 0);
  }
}
