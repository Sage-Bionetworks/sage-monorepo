import { expect, Locator, Page } from '@playwright/test';

export const getPinnedQueryParams = (url: string): string[] => {
  const searchParams = new URL(url).searchParams;
  const pinnedValues = searchParams.getAll('pinned');

  if (!pinnedValues.length) {
    return [];
  }

  return pinnedValues
    .flatMap((value) => value.split(','))
    .map((value) => value.trim())
    .filter((value) => value.length > 0);
};

export const getPinnedTable = (page: Page): Locator => page.locator('explorers-base-table').first();

export const getUnpinnedTable = (page: Page): Locator =>
  page.locator('explorers-base-table').last();

export const expectUnpinnedTableOnly = async (page: Page): Promise<void> => {
  await expect(page.locator('explorers-base-table').filter({ visible: true })).toHaveCount(1);
  await expect(getUnpinnedTable(page).locator('tbody tr').first()).toBeVisible();
};

export const getRowByName = (table: Locator, page: Page, name: string): Locator =>
  table.locator('tbody tr').filter({
    has: page.getByRole('cell', { name, exact: true }),
  });

export const togglePinByName = async (
  table: Locator,
  page: Page,
  name: string,
  toggle: 'pin' | 'unpin',
) => {
  const row = getRowByName(table, page, name);
  await expect(row).toHaveCount(1);
  const pinButton = row.getByRole('button', { name: toggle === 'pin' ? 'Pin' : 'Unpin' });
  await pinButton.focus();
  await pinButton.press('Enter');
  return row;
};

export const pinByName = async (table: Locator, page: Page, name: string) => {
  return await togglePinByName(table, page, name, 'pin');
};

export const unPinByName = async (table: Locator, page: Page, name: string) => {
  return await togglePinByName(table, page, name, 'unpin');
};

export const expectPinnedParams = async (page: Page, expected: string[]): Promise<void> => {
  await expect.poll(() => getPinnedQueryParams(page.url())).toEqual(expected);
};

export const expectPinnedRows = async (page: Page, rowNames: string[]): Promise<void> => {
  await expect(page.locator('explorers-base-table')).toHaveCount(2);
  const pinnedTable = getPinnedTable(page);
  for (const rowName of rowNames) {
    await expect(getRowByName(pinnedTable, page, rowName)).toHaveCount(1);
  }
};
