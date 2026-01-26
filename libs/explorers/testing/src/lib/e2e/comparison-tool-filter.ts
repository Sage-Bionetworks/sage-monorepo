import { Page } from '@playwright/test';
import {
  clickFilterCheckbox,
  expectFilters,
  expectFiltersParams,
  openFilterMenuAndClickCheckbox,
  toggleFilterPanel,
} from './comparison-tool';

export async function testFilterSelectionUpdatesUrl(
  page: Page,
  filterQueryParamName: string,
  filterMenuName: string,
  filterName: string,
): Promise<void> {
  await expectFiltersParams(page, {});

  const filterPanelMain = await openFilterMenuAndClickCheckbox(page, filterMenuName, filterName);
  await expectFiltersParams(page, { [filterQueryParamName]: [filterName] });

  await clickFilterCheckbox(filterPanelMain, filterMenuName, filterName);
  await expectFiltersParams(page, {});
}

export async function testFilterSelectionRestoredFromUrl(
  page: Page,
  expectedFilterParams: Record<string, string[]>,
  expectedSelectedFilters: Record<string, string[]>,
): Promise<void> {
  await expectFiltersParams(page, expectedFilterParams);
  await expectFilters(page, expectedSelectedFilters);

  const filterPanelMain = await toggleFilterPanel(page);
  for (const [filterMenuName, filterNames] of Object.entries(expectedSelectedFilters)) {
    for (const filterName of filterNames) {
      await clickFilterCheckbox(filterPanelMain, filterMenuName, filterName);
    }
  }

  await expectFiltersParams(page, {});
}

export async function testFiltersRemovedFromUrlOnClearAll(
  page: Page,
  expectedInitialFilterParams: Record<string, string[]>,
): Promise<void> {
  await expectFiltersParams(page, expectedInitialFilterParams);

  const clearAllButton = page.getByRole('button', { name: 'Clear All' });
  await clearAllButton.click();

  await expectFiltersParams(page, {});
}
