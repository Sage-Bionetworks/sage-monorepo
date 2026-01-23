import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS } from '@sagebionetworks/explorers/constants';
import { ComparisonToolUrlParams, SortOrder } from '@sagebionetworks/explorers/models';
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
    const params: Params = {};

    this.serializeArrayParam(params, 'categories', state.categories);
    this.serializeArrayParam(params, 'pinned', state.pinnedItems);
    this.serializeArrayParam(params, 'sortFields', state.sortFields);
    this.serializeNumberArrayParam(params, 'sortOrders', state.sortOrders);
    this.serializeFilterSelections(params, state.filterSelections);

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

  private serializeFilterSelections(
    params: Params,
    filterSelections: Record<string, string[]> | null | undefined,
  ): void {
    // Get current filter keys from URL that need to be cleared
    const currentParams = this.route.snapshot.queryParams;
    const currentFilterKeys = Object.keys(currentParams).filter(
      (key) => !RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS.has(key),
    );

    // Clear all current filter keys first
    for (const key of currentFilterKeys) {
      params[key] = null;
    }

    // Then set any new filter values
    if (filterSelections) {
      for (const [queryParamKey, values] of Object.entries(filterSelections)) {
        if (values && values.length > 0) {
          params[queryParamKey] = values.map((v) => encodeURIComponent(v)).join(',');
        }
      }
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
      const sortOrders = this.parseNumberArrayParam(params['sortOrders']);
      if (sortOrders.length > 0) {
        result.sortOrders = sortOrders as SortOrder[];
      }
    }

    const filterSelections = this.deserializeFilterSelections(params);
    if (Object.keys(filterSelections).length > 0) {
      result.filterSelections = filterSelections;
    }

    return result;
  }

  private deserializeFilterSelections(params: Params): Record<string, string[]> {
    const filterSelections: Record<string, string[]> = {};

    for (const [key, value] of Object.entries(params)) {
      if (RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS.has(key)) {
        continue;
      }

      const values = this.parseCommaSeparatedParam(value);
      if (values.length > 0) {
        filterSelections[key] = values;
      }
    }

    return filterSelections;
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

  private parseNumberArrayParam(value: string | string[] | null | undefined): number[] {
    if (value == null) {
      return [];
    }

    const stringValue = Array.isArray(value) ? value.join(',') : value;
    return stringValue
      .split(',')
      .map((s) => parseInt(s.trim(), 10))
      .filter((n) => !isNaN(n));
  }
}
