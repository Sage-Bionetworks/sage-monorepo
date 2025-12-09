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
    const currentSortFields = currentState.sortFields ?? [];
    const nextSortFields = state.sortFields ?? [];
    const currentSortOrders = currentState.sortOrders ?? [];
    const nextSortOrders = state.sortOrders ?? [];

    if (
      isEqual(currentPinned, nextPinned) &&
      isEqual(currentCategories, nextCategories) &&
      isEqual(currentSortFields, nextSortFields) &&
      isEqual(currentSortOrders, nextSortOrders)
    ) {
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

    this.serializeArrayParam(params, 'categories', state.categories);
    this.serializeArrayParam(params, 'pinned', state.pinnedItems);
    this.serializeArrayParam(params, 'sortFields', state.sortFields);
    this.serializeNumberArrayParam(params, 'sortOrders', state.sortOrders);

    return params;
  }

  private serializeArrayParam(
    params: Params,
    key: string,
    value: string[] | null | undefined,
  ): void {
    if (value && value.length > 0) {
      // Encode each value to preserve commas within individual values
      // Then join with commas as the delimiter
      params[key] = value.map((v) => encodeURIComponent(v)).join(',');
    } else if (value !== undefined) {
      params[key] = null;
    }
  }

  private serializeNumberArrayParam(
    params: Params,
    key: string,
    value: number[] | null | undefined,
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

    if (params['sortFields']) {
      const sortFields = this.parseCommaSeparatedParam(params['sortFields']);
      if (sortFields.length > 0) {
        result.sortFields = sortFields;
      }
    }

    if (params['sortOrders']) {
      const sortOrders = this.parseSortOrdersParam(params['sortOrders']);
      if (sortOrders.length > 0) {
        result.sortOrders = sortOrders;
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

  private parseSortOrdersParam(value: string | string[] | null | undefined): number[] {
    if (value == null) {
      return [];
    }

    const values = Array.isArray(value) ? value : [value];

    return values
      .flatMap((entry) => `${entry}`.split(','))
      .map((entry) => {
        const num = parseInt(entry, 10);
        // Validate that the number is valid and is either 1 or -1 (valid sort orders)
        if (isNaN(num) || (num !== 1 && num !== -1)) {
          console.warn(
            `Invalid sort order value '${entry}' in URL. Expected 1 or -1. Ignoring invalid value.`,
          );
          return NaN; // Return NaN to be filtered out
        }
        return num;
      })
      .filter((entry) => !isNaN(entry));
  }
}
