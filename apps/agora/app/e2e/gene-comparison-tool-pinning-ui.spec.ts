import { expect, test } from '@playwright/test';
import { baseURL } from '../playwright.config';
import { GCT_CATEGORIES, GCT_RNA_SUBCATEGORIES, URL_GCT } from './helpers/constants';
import { expectGctPageLoaded } from './helpers/gct';
import {
  expectDisplayedGenesCountText,
  expectPinnedGenesCountText,
  expectTooManyPinnedGenesToast,
  formatPinnedGenesQueryParam,
  getGenesTableCount,
  getPinnedItemsFromUrl,
  pinGeneViaSearch,
} from './helpers/gct-pinning';

test.describe('GCT: Pinning Genes via UI', () => {
  test('one gene can be directly pinned', async ({ page }) => {
    const geneName = 'GFAP';
    const geneEnsemblId = 'ENSG00000131095';

    await page.goto(URL_GCT);
    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    await pinGeneViaSearch(page, geneName);
    await expectPinnedGenesCountText(page, 1);

    await test.step('url has been updated', async () => {
      const expectedUrl = `${baseURL}${URL_GCT}?${formatPinnedGenesQueryParam([geneEnsemblId])}`;
      expect(page.url()).toEqual(expectedUrl);
    });
  });

  test('only 50 genes are pinned when pinning all genes matched by a quick filter', async ({
    page,
  }) => {
    await page.goto(URL_GCT);
    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    await test.step('apply a quick filter', async () => {
      await page.getByRole('button', { name: 'Filter Genes' }).click();
      await page.getByRole('button', { name: 'Quick Filters' }).click();
      await page.getByText('All Nominated targets').click();

      const filterPanel = page
        .locator('.gct-filter-panel-pane-inner')
        .filter({ hasText: 'Quick Filters' });
      const filterCloseBtn = filterPanel.locator('.gct-filter-panel-close');
      await filterCloseBtn.click();
      await expect(filterPanel).toBeHidden();
    });

    await test.step('pin genes', async () => {
      const pinAllBtn = page.getByRole('button', { name: 'Pin All' });
      await pinAllBtn.click();
      await expect(pinAllBtn).toBeDisabled();
    });

    await test.step('50 genes were pinned', async () => {
      await expectTooManyPinnedGenesToast(page);
      await expectPinnedGenesCountText(page, 50);

      const genes = getPinnedItemsFromUrl(page.url());
      expect(genes).toHaveLength(50);
    });
  });

  test('pinning genes does not change displayed genes count', async ({ page }) => {
    await page.goto(URL_GCT);
    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    const totalGenes = await getGenesTableCount(page);
    await expectDisplayedGenesCountText(page, totalGenes);

    await pinGeneViaSearch(page, 'GFAP');
    await page.getByPlaceholder('Search', { exact: true }).clear();

    await expectDisplayedGenesCountText(page, totalGenes);

    const newTotalGenes = await getGenesTableCount(page);
    expect(newTotalGenes).toEqual(totalGenes - 1);
  });
});
