import { expect, test } from '@playwright/test';
import {
  GCT_CATEGORIES,
  GCT_PROTEIN_SUBCATEGORIES,
  GCT_RNA_SUBCATEGORIES,
  URL_GCT,
  URL_GCT_PROTEIN,
  URL_GCT_PROTEIN_TMT,
} from './helpers/constants';
import {
  fiftyGenes,
  fiftyOneGenes,
  fiftyProteinsToFiftyUniqueGenesTMT,
  geneWithMultipleProteinsTMT,
} from './helpers/data';
import { expectGctPageLoaded, getGeneRowButtons } from './helpers/gct';
import {
  confirmPinnedItemsByGeneName,
  confirmPinnedItemsCount,
  expectPinnedGenesCountText,
  expectPinnedProteinsCountText,
  expectTooManyPinnedGenesToast,
  formatPinnedGenesQueryParam,
  getPinnedItemsFromUrl,
} from './helpers/gct-pinning';

test.describe('GCT: Pinning Genes from URL', () => {
  test('when RNA url does not include pinned genes, no genes are pinned', async ({ page }) => {
    await page.goto(URL_GCT);
    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    await expect(page.getByText('Pinned Genes')).toBeHidden();
    await expect(page.getByText('All Genes')).toBeHidden();
    await confirmPinnedItemsCount(page, 0);
  });

  test('when Protein url does not include pinned genes, no genes are pinned', async ({ page }) => {
    await page.goto(URL_GCT_PROTEIN);
    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.SRM);

    await expect(page.getByText('Pinned Genes')).toBeHidden();
    await expect(page.getByText('All Genes')).toBeHidden();
    await confirmPinnedItemsCount(page, 0);
  });

  test('when RNA url includes 50 pinned genes, all genes are pinned', async ({ page }) => {
    const url = `${URL_GCT}?${formatPinnedGenesQueryParam(fiftyGenes)}`;
    await page.goto(url);

    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    await test.step('confirm pinned all 50 genes', async () => {
      await expect(page.getByText('All Genes')).toBeVisible();
      await expectPinnedGenesCountText(page, 50);
      await confirmPinnedItemsCount(page, 50);
      await expect(page.getByRole('alert')).toBeHidden();
    });

    await test.step('confirm cannot pin other genes', async () => {
      const btns = await getGeneRowButtons(page, 'PYGL');
      await expect(btns.open).toBeEnabled();
      await expect(btns.pin).toBeDisabled();
    });
  });

  test('when RNA url includes >50 pinned genes, only 50 genes are pinned and toast is displayed', async ({
    page,
  }) => {
    const url = `${URL_GCT}?${formatPinnedGenesQueryParam(fiftyOneGenes)}`;
    await page.goto(url);

    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    await test.step('confirm only pinned 50 genes', async () => {
      await expect(page.getByText('All Genes')).toBeVisible();
      await expectPinnedGenesCountText(page, 50);
      await confirmPinnedItemsCount(page, 50);
      await expectTooManyPinnedGenesToast(page);
    });

    await test.step('confirm url dropped 51st gene', () => {
      const genes = getPinnedItemsFromUrl(page.url());
      expect(genes).toHaveLength(50);
      expect(genes).toEqual(fiftyOneGenes.sort().slice(0, -1));
    });
  });

  test('when RNA url includes invalid gene, that gene is dropped from the url', async ({
    page,
  }) => {
    const validGeneId = geneWithMultipleProteinsTMT.ensemblId;
    const url = `${URL_GCT}?${formatPinnedGenesQueryParam([validGeneId, 'invalidGene'])}`;
    await page.goto(url);

    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    await test.step('confirm toast not shown', async () => {
      await expect(page.getByRole('alert')).toBeHidden();
    });

    await test.step('confirm only pinned 1 gene', async () => {
      await expect(page.getByText('All Genes')).toBeVisible();
      await expectPinnedGenesCountText(page, 1);
      await confirmPinnedItemsCount(page, 1);
    });

    await test.step('confirm url dropped invalid gene', () => {
      const genes = getPinnedItemsFromUrl(page.url());
      expect(genes).toHaveLength(1);
      expect(genes).toEqual([validGeneId]);
    });
  });

  test.fail(
    'when RNA url includes proteins, the related gene is pinned',
    {
      annotation: {
        type: 'fail',
        description: 'Since AG-1425, only genes will be pinned from RNA url',
      },
    },
    async ({ page }) => {
      const geneProteins = geneWithMultipleProteinsTMT.uniProtIds.map(
        (uniProtId) => `${geneWithMultipleProteinsTMT.ensemblId}${uniProtId}`,
      );
      const url = `${URL_GCT}?${formatPinnedGenesQueryParam(geneProteins)}`;
      await page.goto(url);

      await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

      await confirmPinnedItemsByGeneName(page, geneWithMultipleProteinsTMT.name, 1);
      await expectPinnedGenesCountText(page, 1);
      await confirmPinnedItemsCount(page, 1);
    },
  );

  test.fail(
    'when Protein url includes a gene, all related proteins are pinned',
    {
      annotation: {
        type: 'fail',
        description: 'Since AG-1425, only proteins will be pinned from Protein url',
      },
    },
    async ({ page }) => {
      const url = `${URL_GCT_PROTEIN_TMT}&${formatPinnedGenesQueryParam([
        geneWithMultipleProteinsTMT.ensemblId,
      ])}`;
      await page.goto(url);
      await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

      await confirmPinnedItemsByGeneName(
        page,
        geneWithMultipleProteinsTMT.name,
        geneWithMultipleProteinsTMT.uniProtIds.length,
      );

      await test.step('confirm counts', async () => {
        await confirmPinnedItemsCount(page, 5);
        await expectPinnedGenesCountText(page, 1);
        await expectPinnedProteinsCountText(page, 5);
      });
    },
  );

  test('when Protein url includes 50 proteins from 50 unique genes, all proteins are pinned', async ({
    page,
  }) => {
    const url = `${URL_GCT_PROTEIN_TMT}&${formatPinnedGenesQueryParam(
      fiftyProteinsToFiftyUniqueGenesTMT,
    )}`;
    await page.goto(url);
    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

    await test.step('confirm counts', async () => {
      await confirmPinnedItemsCount(page, 50);
      await expectPinnedGenesCountText(page, 50);
      await expectPinnedProteinsCountText(page, 50);
    });
  });

  test('when Protein url includes >50 proteins from 50 unique genes, all proteins are pinned', async ({
    page,
  }) => {
    const fortyNineProteinsToUniqueGenes = fiftyProteinsToFiftyUniqueGenesTMT.slice(0, -1);
    const oneGeneWithManyProteins = geneWithMultipleProteinsTMT.uniProtIds.map(
      (uniProtId) => `${geneWithMultipleProteinsTMT.ensemblId}${uniProtId}`,
    );
    const allProteins = [...fortyNineProteinsToUniqueGenes, ...oneGeneWithManyProteins];
    const url = `${URL_GCT_PROTEIN_TMT}&${formatPinnedGenesQueryParam(allProteins)}`;

    await page.goto(url);

    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

    await test.step('confirm counts', async () => {
      await expectPinnedGenesCountText(page, 50);
      await confirmPinnedItemsCount(page, allProteins.length);
      await expectPinnedProteinsCountText(page, allProteins.length);
    });

    await confirmPinnedItemsByGeneName(
      page,
      geneWithMultipleProteinsTMT.name,
      geneWithMultipleProteinsTMT.uniProtIds.length,
    );
  });

  test('when Protein url includes proteins from 51 unique genes, only proteins from 50 genes are pinned', async ({
    page,
  }) => {
    const oneGeneWithManyProteins = geneWithMultipleProteinsTMT.uniProtIds.map(
      (uniProtId) => `${geneWithMultipleProteinsTMT.ensemblId}${uniProtId}`,
    );
    const allProteins = [...fiftyProteinsToFiftyUniqueGenesTMT, ...oneGeneWithManyProteins];
    const expectedPinnedProteins = [
      ...fiftyProteinsToFiftyUniqueGenesTMT.slice(0, -1),
      ...oneGeneWithManyProteins,
    ];

    const url = `${URL_GCT_PROTEIN_TMT}&${formatPinnedGenesQueryParam(allProteins)}`;

    await page.goto(url);

    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

    await test.step('confirm counts', async () => {
      await expectPinnedGenesCountText(page, 50);
      await confirmPinnedItemsCount(page, expectedPinnedProteins.length);
      await expectPinnedProteinsCountText(page, expectedPinnedProteins.length);
    });
  });

  test('when Protein url includes invalid protein, that protein is dropped from the url', async ({
    page,
  }) => {
    const validGeneProtein = fiftyProteinsToFiftyUniqueGenesTMT[1];
    const url = `${URL_GCT_PROTEIN_TMT}&${formatPinnedGenesQueryParam([
      validGeneProtein,
      'invalidGeneProtein',
    ])}`;
    await page.goto(url);

    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

    await test.step('confirm toast not shown', async () => {
      await expect(page.getByRole('alert')).toBeHidden();
    });

    await test.step('confirm only pinned 1 protein', async () => {
      await expectPinnedGenesCountText(page, 1);
      await confirmPinnedItemsCount(page, 1);
      await expectPinnedProteinsCountText(page, 1);
    });

    await test.step('confirm url dropped invalid protein', () => {
      const geneProteins = getPinnedItemsFromUrl(page.url());
      expect(geneProteins).toHaveLength(1);
      expect(geneProteins).toEqual([validGeneProtein]);
    });
  });
});
