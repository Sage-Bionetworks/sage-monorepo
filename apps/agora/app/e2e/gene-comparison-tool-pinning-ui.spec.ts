import { expect, test } from '@playwright/test';
import { baseURL } from '../playwright.config';
import {
  GCT_CATEGORIES,
  GCT_PROTEIN_SUBCATEGORIES,
  GCT_RNA_SUBCATEGORIES,
  URL_GCT,
  URL_GCT_PROTEIN_TMT,
} from './helpers/constants';
import { expectGctPageLoaded } from './helpers/gct';
import {
  expectDisplayedGenesCountText,
  expectPinnedGenesCountText,
  expectPinnedProteinsCountText,
  expectTooManyPinnedGenesToast,
  expectTooManyPinnedProteinsToast,
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

  test('only 50 genes are pinned when pinning all rows matching a search in protein mode', async ({
    page,
  }) => {
    await page.goto(URL_GCT_PROTEIN_TMT);
    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

    await test.step(`search for a`, async () => {
      const search = page.getByPlaceholder('Search', { exact: true });
      await search.fill('a');
    });

    await test.step('pin all search results', async () => {
      const pinAllBtn = page.getByRole('button', { name: 'Pin All' });
      await pinAllBtn.click();
      await expect(pinAllBtn).toBeDisabled();
    });

    await test.step('confirm only 50 genes were pinned and toast is displayed', async () => {
      const expectedPinnedProteinCount = 72;
      await expectTooManyPinnedProteinsToast(page, expectedPinnedProteinCount);
      await expectPinnedGenesCountText(page, 50);
      await expectPinnedProteinsCountText(page, expectedPinnedProteinCount);
    });
  });

  test('pin all adds proteins from already-pinned genes when at gene limit', async ({ page }) => {
    const initialProteinCount = 71;
    const url = `${URL_GCT_PROTEIN_TMT}&pinned=ENSG00000042493P40121,ENSG00000069974P51159,ENSG00000103723Q13367,ENSG00000103723Q13367-2,ENSG00000104728O15013,ENSG00000104763Q13510,ENSG00000107951Q9NVV4,ENSG00000110514Q8WXG6,ENSG00000110514Q8WXG6-4,ENSG00000110514Q8WXG6-7,ENSG00000112294P51649,ENSG00000113643P54136,ENSG00000115840O75746,ENSG00000124164O95292,ENSG00000125505Q96N66,ENSG00000125741Q9H6K4,ENSG00000125741Q9H6K4-2,ENSG00000125877Q9BY32,ENSG00000126602Q12931,ENSG00000130203P02649,ENSG00000130414O95299,ENSG00000130702O15230,ENSG00000130707P00966,ENSG00000132561O00339,ENSG00000133706Q9P2J5,ENSG00000137411Q5ST30,ENSG00000137806Q9Y375,ENSG00000138448P06756,ENSG00000139180F5GY40,ENSG00000139180Q16795,ENSG00000141338O94911-3,ENSG00000142156P12109,ENSG00000142192A0A0A0MRG2,ENSG00000142192P05067,ENSG00000153113D6RBR1,ENSG00000153113P20810-6,ENSG00000161618Q8IZ83,ENSG00000162688P35573,ENSG00000174469Q9UHC6,ENSG00000180228O75569,ENSG00000183454Q12879,ENSG00000184983P56556,ENSG00000186868P10636,ENSG00000186868P10636-4,ENSG00000186868P10636-6,ENSG00000186868P10636-8,ENSG00000188157O00468-3,ENSG00000188157O00468-6,ENSG00000196126P20039,ENSG00000196126Q5Y7A7,ENSG00000196655Q9Y296,ENSG00000197535Q9Y4I1-2,ENSG00000197858O43292,ENSG00000198087Q9Y5K6,ENSG00000198836O60313-2,ENSG00000204287P01903,ENSG00000204525Q9TNN7,ENSG00000204681Q9UBS5,ENSG00000206503P01891,ENSG00000206503P01892,ENSG00000206503P05534,ENSG00000206503P13746,ENSG00000206503P30450,ENSG00000206503P30455,ENSG00000213995Q8IW45,ENSG00000214960A4D126,ENSG00000234745P01889,ENSG00000234745P18463,ENSG00000234745P30460,ENSG00000234745P30483,ENSG00000234745Q29836`;
    await page.goto(url);
    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);
    await expectPinnedGenesCountText(page, 50);
    await expectPinnedProteinsCountText(page, initialProteinCount);

    await test.step(`search for a`, async () => {
      const search = page.getByPlaceholder('Search', { exact: true });
      await search.fill('a');
    });

    await test.step('pin all search results', async () => {
      const pinAllBtn = page.getByRole('button', { name: 'Pin All' });
      await expect(pinAllBtn).toBeEnabled();
      await pinAllBtn.click();
      await expect(pinAllBtn).toBeDisabled();
    });

    await test.step('confirm only 1 row was pinned and toast is displayed', async () => {
      await expectTooManyPinnedProteinsToast(page, 1);
      await expectPinnedGenesCountText(page, 50);
      await expectPinnedProteinsCountText(page, initialProteinCount + 1);
    });
  });
});
