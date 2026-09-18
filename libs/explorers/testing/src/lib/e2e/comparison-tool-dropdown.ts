import { expect, Page } from '@playwright/test';
import { escapeRegexChars } from '@sagebionetworks/shared/util/helpers';

export const CATEGORY_DROPDOWN_INDEX = { first: 0, last: -1 } as const;
export const CATEGORY_OPTION_INDEX = { first: 0, second: 1 } as const;

/**
 * Selects an option in one of the category dropdowns. Returns once the dropdown has closed, which
 * is before the table has refetched -- assert on the resulting data to wait for the new rows.
 * @param page - Playwright Page object
 * @param dropdownIndex - Position of the dropdown in the selector row, 0 being the leftmost and -1
 * the rightmost. Levels with a single option render as plain text, so only rendered dropdowns count.
 * @param option - The option label, matched case-insensitively as a substring, or the option's
 * position in the listbox for tests that don't know the labels up front
 * @returns The label of the selected option
 */
export const selectCategoryOption = async (
  page: Page,
  dropdownIndex: number,
  option: string | number,
): Promise<string> => {
  const dropdown = page
    .locator('.comparison-tool-category-selectors')
    .getByRole('combobox')
    .nth(dropdownIndex);
  const listbox = page.getByRole('listbox');

  // Click dropdown to open
  await dropdown.click();
  await expect(listbox).toBeVisible();

  const optionLocator =
    typeof option === 'number'
      ? page.getByRole('option').nth(option)
      : page.getByRole('option', { name: new RegExp(escapeRegexChars(option), 'i') });
  await expect(optionLocator).toBeVisible();
  const optionLabel = (await optionLocator.textContent()) ?? '';
  await optionLocator.click();

  // Wait for listbox to close (dropdown selection complete)
  await expect(listbox).toBeHidden();

  return optionLabel.trim();
};
