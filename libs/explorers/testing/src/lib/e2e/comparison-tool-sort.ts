import { expect, Page } from '@playwright/test';
import {
  expectSortFieldsParams,
  expectSortOrdersParams,
  getSortFieldsQueryParams,
  getSortOrdersQueryParams,
  sortColumn,
} from './comparison-tool';

/**
 * Tests that clicking a column header updates the URL with sortFields and sortOrders.
 * Verifies initial state has no sort params, then clicking sets descending sort.
 */
export async function testClickColumnUpdatesSortUrl(
  page: Page,
  columnName: string,
  expectedField: string,
): Promise<void> {
  // Initial state: no sort params (default sort applied internally)
  expect(getSortFieldsQueryParams(page.url())).toEqual([]);
  expect(getSortOrdersQueryParams(page.url())).toEqual([]);

  // Click column header
  await sortColumn(page, columnName);

  await expectSortFieldsParams(page, [expectedField]);
  await expectSortOrdersParams(page, [-1]);
}

/**
 * Tests that clicking the same column toggles between descending and ascending order.
 */
export async function testClickColumnTogglesSortOrder(
  page: Page,
  columnName: string,
  expectedField: string,
): Promise<void> {
  // First click - descending
  await sortColumn(page, columnName);
  await expectSortFieldsParams(page, [expectedField]);
  await expectSortOrdersParams(page, [-1]);

  // Second click - ascending
  await sortColumn(page, columnName);
  await expectSortFieldsParams(page, [expectedField]);
  await expectSortOrdersParams(page, [1]);

  // Third click - back to descending
  await sortColumn(page, columnName);
  await expectSortFieldsParams(page, [expectedField]);
  await expectSortOrdersParams(page, [-1]);

  // Fourth click - ascending again
  await sortColumn(page, columnName);
  await expectSortFieldsParams(page, [expectedField]);
  await expectSortOrdersParams(page, [1]);
}

/**
 * Tests that sort state is restored from URL and can be toggled.
 */
export async function testSortRestoredFromUrl(
  page: Page,
  columnName: string,
  expectedField: string,
): Promise<void> {
  await expectSortFieldsParams(page, [expectedField]);
  await expectSortOrdersParams(page, [1]);

  // Click the sorted column to toggle it
  await sortColumn(page, columnName);
  await expectSortFieldsParams(page, [expectedField]);
  await expectSortOrdersParams(page, [-1]);

  // Click again to toggle back
  await sortColumn(page, columnName);
  await expectSortFieldsParams(page, [expectedField]);
  await expectSortOrdersParams(page, [1]);
}

/**
 * Configuration for a column: display name and expected field name in URL
 */
export interface ColumnConfig {
  name: string;
  field: string;
}

/**
 * Tests that clicking different columns in sequence replaces the single-column sort.
 * @param columns - Array of at least 3 columns to test with
 */
export async function testClickDifferentColumnsReplacesSingleSort(
  page: Page,
  columns: ColumnConfig[],
): Promise<void> {
  if (columns.length < 3) {
    throw new Error('At least 3 columns are required for this test');
  }

  const [first, second, third] = columns;

  // Click first column
  await sortColumn(page, first.name);
  await expectSortFieldsParams(page, [first.field]);
  await expectSortOrdersParams(page, [-1]);

  // Click second column - should replace the sort
  await sortColumn(page, second.name);
  await expectSortFieldsParams(page, [second.field]);
  await expectSortOrdersParams(page, [-1]);

  // Click third column - should replace again
  await sortColumn(page, third.name);
  await expectSortFieldsParams(page, [third.field]);
  await expectSortOrdersParams(page, [-1]);

  // Click first column again - back to first column
  await sortColumn(page, first.name);
  await expectSortFieldsParams(page, [first.field]);
  await expectSortOrdersParams(page, [-1]);
}

/**
 * Tests that Meta+click builds multi-column sort and regular click resets to single sort.
 * @param columns - Array of at least 3 columns to test with
 */
export async function testMetaClickBuildsMultiColumnSort(
  page: Page,
  columns: ColumnConfig[],
): Promise<void> {
  if (columns.length < 3) {
    throw new Error('At least 3 columns are required for this test');
  }

  const [first, second, third] = columns;

  // Click first column
  await sortColumn(page, first.name);
  await expectSortFieldsParams(page, [first.field]);
  await expectSortOrdersParams(page, [-1]);

  // Meta+click second column to add to sort
  await sortColumn(page, second.name, true);
  await expectSortFieldsParams(page, [first.field, second.field]);
  await expectSortOrdersParams(page, [-1, -1]);

  // Regular click on third column - should reset to single column sort
  await sortColumn(page, third.name);
  await expectSortFieldsParams(page, [third.field]);
  await expectSortOrdersParams(page, [-1]);

  // Build multi-column sort again to verify cycle works
  await sortColumn(page, first.name);
  await sortColumn(page, second.name, true);
  await expectSortFieldsParams(page, [first.field, second.field]);
  await expectSortOrdersParams(page, [-1, -1]);
}

/**
 * Tests that multi-column sort is restored from URL and can be extended.
 */
export async function testMultiColumnSortRestoredFromUrl(
  page: Page,
  existingColumns: ColumnConfig[],
  newColumn: ColumnConfig,
): Promise<void> {
  const existingFields = existingColumns.map((c) => c.field);
  const existingOrders = existingColumns.map((_, i) => (i === 0 ? -1 : 1));

  await expectSortFieldsParams(page, existingFields);
  await expectSortOrdersParams(page, existingOrders);

  // Meta+click to add new column
  await sortColumn(page, newColumn.name, true);
  await expectSortFieldsParams(page, [...existingFields, newColumn.field]);
  await expectSortOrdersParams(page, [...existingOrders, -1]);
}

/**
 * Tests that Meta+click on existing sort columns toggles their order.
 * @param columns - Array of at least 2 columns to test with
 */
export async function testMetaClickTogglesExistingSortOrder(
  page: Page,
  columns: ColumnConfig[],
): Promise<void> {
  if (columns.length < 2) {
    throw new Error('At least 2 columns are required for this test');
  }

  const fields = columns.map((c) => c.field);

  // Set up multi-column sort
  await sortColumn(page, columns[0].name);
  for (let i = 1; i < columns.length; i++) {
    await sortColumn(page, columns[i].name, true);
  }
  await expectSortFieldsParams(page, fields);
  await expectSortOrdersParams(
    page,
    columns.map(() => -1),
  );

  // Toggle each column's order
  const orders = columns.map(() => -1);
  for (let i = 0; i < columns.length; i++) {
    await sortColumn(page, columns[i].name, true);
    orders[i] = 1;
    await expectSortFieldsParams(page, fields);
    await expectSortOrdersParams(page, [...orders]);
  }

  // Toggle all back to descending
  for (let i = 0; i < columns.length; i++) {
    await sortColumn(page, columns[i].name, true);
  }
  await expectSortFieldsParams(page, fields);
  await expectSortOrdersParams(
    page,
    columns.map(() => -1),
  );
}
