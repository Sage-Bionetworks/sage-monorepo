import { Page, expect, test } from '@playwright/test';
import { GCT_CATEGORIES, GCT_PROTEIN_SUBCATEGORIES, GCT_RNA_SUBCATEGORIES } from './constants';
import { waitForSpinnerNotVisible } from './utils';

export const expectGctPageLoaded = async (
  page: Page,
  category: string,
  subcategory: string,
  closeHelpDialog = true,
) => {
  await test.step('wait for GCT page to load', async () => {
    if (closeHelpDialog) await closeGctHelpDialog(page);

    await waitForSpinnerNotVisible(page);

    await expect(page.getByRole('heading', { name: 'Gene Comparison Tool' })).toBeVisible();

    await expect(page.getByRole('button', { name: 'Filter Genes' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Share URL' })).toBeVisible();

    await expect(page.getByText(category)).toBeVisible();
    await expect(page.getByText(subcategory)).toBeVisible();
  });
};

export const closeGctHelpDialog = async (page: Page) => {
  await test.step('close GCT help dialog', async () => {
    const dialog = page.getByRole('dialog');
    await expect(dialog).toHaveText(/Gene Comparison Overview/i);

    const closeBtn = dialog.getByRole('button').first();
    await closeBtn.click();

    await expect(dialog).toBeHidden();
  });
};

export const getGeneRowButtons = async (page: Page, name: string) => {
  const geneName = page.locator('.gene-controls').filter({ hasText: name });
  await geneName.hover();

  const buttons = page.getByRole('cell', { name: name }).getByRole('button');
  const count = await buttons.count();
  expect(count).toBe(2);

  return {
    open: buttons.first(),
    pin: buttons.nth(1),
  };
};

const changeGctDropdown = async (page: Page, current: string, desired: string) => {
  await test.step(`change dropdown to ${desired}`, async () => {
    const url = page.url();

    await test.step('open dropdown', async () => {
      const menu = page.getByText(current);
      await menu.click();
    });

    await test.step('wait for dropdown to stabilize', async () => {
      // Must wait for the listbox displaying the options to be stable
      // ...before attempting to click on an option
      // ...otherwise, clicking an option will be flaky and intermittently fail
      // eslint-disable-next-line playwright/no-element-handle
      const listbox = await page.$('ul[role=listbox]');
      await listbox?.waitForElementState('stable');
    });

    const option = await test.step('select new option', async () => {
      const option = page.getByRole('option', { name: desired });
      await option.click({ timeout: 30_000 });
      return option;
    });

    await test.step('wait for page to redirect', async () => {
      await expect(() => {
        expect(page.url()).not.toEqual(url);
      }).toPass({ timeout: 60_000 });
    });

    // Handle case where listbox doesn't close after page redirects
    await test.step('ensure dropdown closes', async () => {
      await page.keyboard.press('Escape');
      await expect(option).toBeHidden();
    });
  });
};

export const changeGctSubcategory = async (
  page: Page,
  category: string,
  currentSubCategory: string,
  desiredSubCategory: string,
) => {
  await test.step(`change to ${desiredSubCategory} subcategory`, async () => {
    await changeGctDropdown(page, currentSubCategory, desiredSubCategory);
    await expectGctPageLoaded(page, category, desiredSubCategory, false);
  });
};

export const changeGctCategory = async (
  page: Page,
  currentCategory: string,
  desiredCategory: string,
) => {
  await test.step(`change to ${desiredCategory} category`, async () => {
    const defaultSubcategory =
      desiredCategory === GCT_CATEGORIES.RNA
        ? GCT_RNA_SUBCATEGORIES.AD
        : GCT_PROTEIN_SUBCATEGORIES.SRM;
    await changeGctDropdown(page, currentCategory, desiredCategory);
    await expectGctPageLoaded(page, desiredCategory, defaultSubcategory, false);
  });
};
