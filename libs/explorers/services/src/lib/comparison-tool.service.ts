import { Injectable, Signal, computed, signal } from '@angular/core';
import { ComparisonToolConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';

/**
 * Shared state contract for comparison tools.
 * - Call {@link initialize} when configs load to seed dropdowns, reset counts, and wiki params.
 * - Read derived state via {@link configs}, {@link dropdownSelection}, {@link currentConfig}, and {@link columns}.
 * - Update user selections through {@link setDropdownSelection}; consumers can read the latest value via {@link dropdownSelection}.
 * - Manage result totals via {@link totalResultsCount} and {@link pinnedResultsCount}.
 */
@Injectable()
export class ComparisonToolService {
  private readonly configsSignal = signal<ComparisonToolConfig[]>([]);
  private readonly dropdownSelectionSignal = signal<string[]>([]);
  private readonly isLegendVisible$ = signal(false);
  private readonly selectorsWikiParamsSignal = signal<Record<string, SynapseWikiParams>>({});

  readonly configs = this.configsSignal.asReadonly();
  readonly dropdownSelection = this.dropdownSelectionSignal.asReadonly();
  readonly selectorsWikiParams = this.selectorsWikiParamsSignal.asReadonly();

  readonly currentConfig: Signal<ComparisonToolConfig | null> = computed(() => {
    const configs = this.configsSignal();
    if (!configs.length) {
      return null;
    }

    const dropdownSelection = this.dropdownSelectionSignal();
    if (!dropdownSelection.length) {
      return configs[0];
    }

    const exactMatch = configs.find((config) => isEqual(config.dropdowns ?? [], dropdownSelection));

    if (exactMatch) {
      return exactMatch;
    }

    const prefixMatch = configs.find((config) =>
      this.isPrefix(dropdownSelection, config.dropdowns),
    );

    return prefixMatch ?? configs[0];
  });

  readonly columns: Signal<string[]> = computed(() => {
    const config = this.currentConfig();
    if (!config) {
      return [];
    }

    if (!config.columns || config.columns.length === 0) {
      throw new Error(
        'Missing comparison tool config data: "columns" property is undefined or empty.',
      );
    }

    return config.columns.map((column) => column.name);
  });

  totalResultsCount = signal<number>(0);
  pinnedResultsCount = signal<number>(0);

  isLegendVisible() {
    return this.isLegendVisible$();
  }

  setLegendVisibility(visible: boolean) {
    this.isLegendVisible$.set(visible);
  }

  toggleLegend() {
    this.isLegendVisible$.update((visible) => !visible);
  }

  initialize(
    configs: ComparisonToolConfig[],
    selection?: string[],
    selectorsWikiParams: Record<string, SynapseWikiParams> = {},
  ) {
    this.configsSignal.set(configs ?? []);
    this.totalResultsCount.set(0);
    this.pinnedResultsCount.set(0);
    this.setSelectorsWikiParams(selectorsWikiParams);

    if (!configs?.length) {
      this.updateDropdownSelectionIfChanged([]);
      return;
    }

    const normalizedSelection = this.normalizeSelection(selection ?? [], configs);
    this.updateDropdownSelectionIfChanged(normalizedSelection);
  }

  setDropdownSelection(selection: string[]) {
    const configs = this.configsSignal();
    if (!configs.length) {
      const normalizedSelection = selection ?? [];
      this.updateDropdownSelectionIfChanged(normalizedSelection);
      return;
    }

    const normalizedSelection = this.normalizeSelection(selection, configs);
    this.updateDropdownSelectionIfChanged(normalizedSelection);
  }

  setSelectorsWikiParams(params: Record<string, SynapseWikiParams>) {
    this.selectorsWikiParamsSignal.set(params ?? {});
  }

  private updateDropdownSelectionIfChanged(selection: string[]) {
    if (isEqual(this.dropdownSelectionSignal(), selection)) {
      return;
    }

    this.dropdownSelectionSignal.set(selection);
  }

  private normalizeSelection(selection: string[], configs: ComparisonToolConfig[]): string[] {
    if (!configs.length) {
      return [];
    }

    const defaultSelection = [...(configs[0].dropdowns ?? [])];

    if (!selection || selection.length === 0) {
      return defaultSelection;
    }

    const exactMatch = configs.find((config) => isEqual(config.dropdowns ?? [], selection));
    if (exactMatch) {
      return [...exactMatch.dropdowns];
    }

    const prefixMatch = configs.find((config) => this.isPrefix(selection, config.dropdowns));
    if (prefixMatch) {
      return [...prefixMatch.dropdowns];
    }

    return defaultSelection;
  }

  private isPrefix(prefix: string[], target: string[] | undefined): boolean {
    const normalizedTarget = target ?? [];
    if (prefix.length > normalizedTarget.length) {
      return false;
    }

    return prefix.every((value, index) => normalizedTarget[index] === value);
  }
}
