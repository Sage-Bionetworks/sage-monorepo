import { expect, test } from '@playwright/test';
import { baseURL } from '../playwright.config';
import { GCT_CATEGORIES, GCT_RNA_SUBCATEGORIES, URL_GCT_PROTEIN } from './helpers/constants';
import { changeGctCategory, closeGctHelpDialog, expectGctPageLoaded } from './helpers/gct';
import { waitForSpinnerNotVisible } from './helpers/utils';

const URL_GCT = '/genes/comparison';
const URL_RNA = '/genes/comparison?';
const URL_PROTEIN = '/genes/comparison?category=Protein+-+Differential+Expression';

test.describe('specific viewport block', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('has title', async ({ page }) => {
    await page.goto(`${URL_GCT}`);

    // wait for page to load (i.e. spinner to disappear)
    await waitForSpinnerNotVisible(page, 250_000);

    // Expect a title "to contain" a substring.
    await expect(page).toHaveTitle('Gene Comparison | Visual comparison tool for AD genes');
  });

  test('protein has sub-category of SRM by default', async ({ page }) => {
    // set category for Protein - Differential Expression
    await page.goto(URL_GCT_PROTEIN);

    // wait for page to load (i.e. spinner to disappear)
    await waitForSpinnerNotVisible(page, 150_000);

    // expect sub-category dropdown to be SRM
    const dropdown = page.locator('#subCategory');
    await expect(dropdown).toHaveText('Targeted Selected Reaction Monitoring (SRM)');
  });

  test('switching from RNA to Protein with RNA-specific column ordering reverts back to Risk Score descending', async ({
    page,
  }) => {
    // set category to RNA
    await page.goto(`${URL_RNA}`);

    // wait for page to load (i.e. spinner to disappear)
    await waitForSpinnerNotVisible(page, 150_000);

    // Gene Comparison Overview tutorial modal
    const tutorialModal = page.getByText('Gene Comparison Overview');
    await expect(tutorialModal).toBeVisible({ timeout: 10_000 });

    // close the Gene Comparison Overview tutorial modal
    await closeGctHelpDialog(page);

    // Gene Comparison Overview tutorial modal
    await expect(tutorialModal).toBeHidden({ timeout: 10_000 });

    // sort by FP ascending
    const FPColumn = page.getByText('FP');
    // first click sorts descending
    await FPColumn.click();
    // second click sorts ascending
    await FPColumn.click();

    // expect url to be correct
    expect(page.url()).toBe(`${baseURL}${URL_RNA}sortField=FP&sortOrder=1`);

    // change category to Protein
    await changeGctCategory(page, GCT_CATEGORIES.RNA, GCT_CATEGORIES.PROTEIN);

    // expect url to be correct
    expect(page.url()).toBe(`${baseURL}${URL_PROTEIN}`);

    // expect sort arrow to be descending
    await expect(page.getByRole('columnheader', { name: 'RISK SCORE' })).toHaveAttribute(
      'aria-sort',
      'descending',
    );
  });

  test('AG-1704: entering a comma in search does not show all genes', async ({ page }) => {
    await page.goto(URL_GCT);
    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    const searchInput = page.getByPlaceholder('Search', { exact: true });

    await searchInput.fill('app,');
    await expect(page.getByText('1-1 of 1')).toBeVisible();

    await searchInput.pressSequentially(' , pten');
    await expect(page.getByText('1-2 of 2')).toBeVisible();

    await searchInput.pressSequentially(',,,,,,');
    await expect(page.getByText('1-2 of 2')).toBeVisible();
  });
});
