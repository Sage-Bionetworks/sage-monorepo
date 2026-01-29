import { expect, test } from '@playwright/test';
import {
  GCT_CATEGORIES,
  GCT_PROTEIN_SUBCATEGORIES,
  GCT_RNA_SUBCATEGORIES,
  URL_GCT,
  URL_GCT_PROTEIN_TMT,
} from './helpers/constants';
import { baseURL } from '../playwright.config';
import { gene2WithMultipleProteinsTMT, geneWithMultipleProteinsTMT } from './helpers/data';
import { changeGctCategory, changeGctSubcategory, expectGctPageLoaded } from './helpers/gct';
import {
  confirmPinnedItemsByGeneName,
  confirmPinnedItemsByItemName,
  confirmPinnedItemsCount,
  confirmPinnedProteins,
  expectPinnedGenesCountText,
  expectPinnedProteinsCountText,
  pinAllItemsViaSearchByGene,
  pinGeneViaSearch,
  pinMultipleGenesViaSearch,
} from './helpers/gct-pinning';

test.describe('GCT: Caching pinned genes', () => {
  test('Pinned genes are maintained when switching between RNA subcategories', async ({ page }) => {
    const genes = ['CYB561A3', 'MANBAL', 'PLEC', 'GFAP'];
    const nGenes = genes.length;

    await page.goto(`${baseURL}${URL_GCT}`);
    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    await pinMultipleGenesViaSearch(page, genes);

    await test.step('confirm # genes pinned on AD subcategory page', async () => {
      await expectPinnedGenesCountText(page, nGenes);
      await confirmPinnedItemsCount(page, nGenes);
    });

    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.RNA,
      GCT_RNA_SUBCATEGORIES.AD,
      GCT_RNA_SUBCATEGORIES.AD_AOD,
    );

    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.RNA,
      GCT_RNA_SUBCATEGORIES.AD_AOD,
      GCT_RNA_SUBCATEGORIES.AD_SEX_F,
    );

    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.RNA,
      GCT_RNA_SUBCATEGORIES.AD_SEX_F,
      GCT_RNA_SUBCATEGORIES.AD_SEX_M,
    );

    // back to original subcategory
    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.RNA,
      GCT_RNA_SUBCATEGORIES.AD_SEX_M,
      GCT_RNA_SUBCATEGORIES.AD,
    );

    await test.step('confirm same genes pinned on AD subcategory page', async () => {
      await expectPinnedGenesCountText(page, nGenes);
      await confirmPinnedItemsCount(page, nGenes);
      await confirmPinnedItemsByItemName(page, genes);
    });
  });

  test('Pinned genes are maintained when switching categories: RNA -> Protein -> RNA', async ({
    page,
  }) => {
    const genes = ['CYB561A3', 'MANBAL', 'PLEC', 'GFAP'];
    const nGenes = genes.length;

    await page.goto(`${baseURL}${URL_GCT}`);
    await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

    await pinMultipleGenesViaSearch(page, genes);

    await test.step('confirm # genes pinned on RNA page', async () => {
      await expectPinnedGenesCountText(page, nGenes);
      await confirmPinnedItemsCount(page, nGenes);
    });

    await changeGctCategory(page, GCT_CATEGORIES.RNA, GCT_CATEGORIES.PROTEIN);

    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.PROTEIN,
      GCT_PROTEIN_SUBCATEGORIES.SRM,
      GCT_PROTEIN_SUBCATEGORIES.TMT,
    );

    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.PROTEIN,
      GCT_PROTEIN_SUBCATEGORIES.TMT,
      GCT_PROTEIN_SUBCATEGORIES.LFQ,
    );

    // back to original category
    await changeGctCategory(page, GCT_CATEGORIES.PROTEIN, GCT_CATEGORIES.RNA);

    await test.step('confirm same genes pinned on RNA page', async () => {
      await expectPinnedGenesCountText(page, nGenes);
      await confirmPinnedItemsCount(page, nGenes);
      await confirmPinnedItemsByItemName(page, genes);
    });
  });

  test('Pinned proteins are maintained when switching between Protein subcategories', async ({
    page,
  }) => {
    const gene1 = geneWithMultipleProteinsTMT.name;
    const gene2 = gene2WithMultipleProteinsTMT.name;
    const genes = [gene1, gene2];
    const nGenes = genes.length;

    const proteins1 = geneWithMultipleProteinsTMT.uniProtIds;
    const proteins2 = gene2WithMultipleProteinsTMT.uniProtIds;
    const nProteins = proteins1.length + proteins2.length;

    await page.goto(`${baseURL}${URL_GCT_PROTEIN_TMT}`);
    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

    for (const gene of genes) {
      await pinAllItemsViaSearchByGene(page, gene);
    }

    await test.step('confirm # genes and proteins pinned on Protein page', async () => {
      await expectPinnedGenesCountText(page, nGenes);
      await expectPinnedProteinsCountText(page, nProteins);
      await confirmPinnedItemsCount(page, nProteins);
    });

    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.PROTEIN,
      GCT_PROTEIN_SUBCATEGORIES.TMT,
      GCT_PROTEIN_SUBCATEGORIES.SRM,
    );

    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.PROTEIN,
      GCT_PROTEIN_SUBCATEGORIES.SRM,
      GCT_PROTEIN_SUBCATEGORIES.LFQ,
    );

    // back to original subcategory
    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.PROTEIN,
      GCT_PROTEIN_SUBCATEGORIES.LFQ,
      GCT_PROTEIN_SUBCATEGORIES.TMT,
    );

    await test.step('confirm same genes and proteins pinned on Protein page', async () => {
      await expectPinnedGenesCountText(page, nGenes);
      await expectPinnedProteinsCountText(page, nProteins);
      await confirmPinnedItemsCount(page, nProteins);

      await confirmPinnedProteins(page, gene1, proteins1);
      await confirmPinnedProteins(page, gene2, proteins2);
    });
  });

  test('Pinned proteins are maintained when switching categories: Protein -> RNA -> Protein', async ({
    page,
  }) => {
    const gene1 = geneWithMultipleProteinsTMT.name;
    const gene2 = gene2WithMultipleProteinsTMT.name;
    const genes = [gene1, gene2];
    const nGenes = genes.length;

    const proteins1 = geneWithMultipleProteinsTMT.uniProtIds;
    const proteins2 = gene2WithMultipleProteinsTMT.uniProtIds;
    const nProteins = proteins1.length + proteins2.length;

    await page.goto(`${baseURL}${URL_GCT_PROTEIN_TMT}`);
    await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

    for (const gene of genes) {
      await pinAllItemsViaSearchByGene(page, gene);
    }

    await test.step('confirm # genes and proteins pinned on Protein page', async () => {
      await expectPinnedGenesCountText(page, nGenes);
      await expectPinnedProteinsCountText(page, nProteins);
      await confirmPinnedItemsCount(page, nProteins);
    });

    await changeGctCategory(page, GCT_CATEGORIES.PROTEIN, GCT_CATEGORIES.RNA);

    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.RNA,
      GCT_RNA_SUBCATEGORIES.AD,
      GCT_RNA_SUBCATEGORIES.AD_AOD,
    );

    // back to original category
    await changeGctCategory(page, GCT_CATEGORIES.RNA, GCT_CATEGORIES.PROTEIN);

    // back to original subcategory
    await changeGctSubcategory(
      page,
      GCT_CATEGORIES.PROTEIN,
      GCT_PROTEIN_SUBCATEGORIES.SRM,
      GCT_PROTEIN_SUBCATEGORIES.TMT,
    );

    await test.step('confirm same genes and proteins pinned on Protein page', async () => {
      await expectPinnedGenesCountText(page, nGenes);
      await expectPinnedProteinsCountText(page, nProteins);

      await confirmPinnedItemsCount(page, nProteins);
      await confirmPinnedProteins(page, gene1, proteins1);
      await confirmPinnedProteins(page, gene2, proteins2);
    });
  });

  test('Last pinned genes are maintained even if proteins were pinned initially', async ({
    page,
  }) => {
    const rnaCategoryGene = geneWithMultipleProteinsTMT.name;
    const rnaCategoryProteins = geneWithMultipleProteinsTMT.uniProtIds;
    const proteinCategoryGene = gene2WithMultipleProteinsTMT.name;

    await test.step('pin proteins in Protein category', async () => {
      await page.goto(`${baseURL}${URL_GCT_PROTEIN_TMT}`);
      await expectGctPageLoaded(page, GCT_CATEGORIES.PROTEIN, GCT_PROTEIN_SUBCATEGORIES.TMT);

      await pinAllItemsViaSearchByGene(page, proteinCategoryGene);
    });

    await test.step('pin genes in RNA category', async () => {
      await changeGctCategory(page, GCT_CATEGORIES.PROTEIN, GCT_CATEGORIES.RNA);

      await test.step('clear all items', async () => {
        await page.getByRole('button', { name: 'Clear All' }).click();
        await expect(page.getByText(/Pinned Genes/i)).toBeHidden();
      });

      await pinGeneViaSearch(page, rnaCategoryGene);

      await test.step('confirm pinned gene', async () => {
        await expectPinnedGenesCountText(page, 1);
        await confirmPinnedItemsByGeneName(page, rnaCategoryGene, 1);
      });
    });

    await test.step('Protein category pins have changed', async () => {
      await changeGctCategory(page, GCT_CATEGORIES.RNA, GCT_CATEGORIES.PROTEIN);
      await changeGctSubcategory(
        page,
        GCT_CATEGORIES.PROTEIN,
        GCT_PROTEIN_SUBCATEGORIES.SRM,
        GCT_PROTEIN_SUBCATEGORIES.TMT,
      );
      await expectPinnedGenesCountText(page, 1);
      await expectPinnedProteinsCountText(page, rnaCategoryProteins.length);
      await confirmPinnedProteins(page, rnaCategoryGene, rnaCategoryProteins);
    });

    await test.step('RNA category pins are maintained', async () => {
      await changeGctCategory(page, GCT_CATEGORIES.PROTEIN, GCT_CATEGORIES.RNA);

      await test.step('confirm pinned gene', async () => {
        await expectPinnedGenesCountText(page, 1);
        await confirmPinnedItemsByGeneName(page, rnaCategoryGene, 1);
      });
    });
  });

  test('Last pinned proteins are maintained even if genes were pinned initially', async ({
    page,
  }) => {
    const rnaCategoryGene = geneWithMultipleProteinsTMT.name;
    const proteinCategoryGene = gene2WithMultipleProteinsTMT.name;
    const proteinCategoryProteins = gene2WithMultipleProteinsTMT.uniProtIds;

    await test.step('pin genes in RNA category', async () => {
      await page.goto(`${baseURL}${URL_GCT}`);
      await expectGctPageLoaded(page, GCT_CATEGORIES.RNA, GCT_RNA_SUBCATEGORIES.AD);

      await pinGeneViaSearch(page, rnaCategoryGene);
      await confirmPinnedItemsByGeneName(page, rnaCategoryGene, 1);
    });

    await test.step('pin proteins in Protein category', async () => {
      await changeGctCategory(page, GCT_CATEGORIES.RNA, GCT_CATEGORIES.PROTEIN);
      await changeGctSubcategory(
        page,
        GCT_CATEGORIES.PROTEIN,
        GCT_PROTEIN_SUBCATEGORIES.SRM,
        GCT_PROTEIN_SUBCATEGORIES.TMT,
      );

      await test.step('clear all items', async () => {
        await page.getByRole('button', { name: 'Clear All' }).click();
        await expect(page.getByText(/Pinned Genes/i)).toBeHidden();
      });

      await pinAllItemsViaSearchByGene(page, proteinCategoryGene);

      await test.step('confirm pinned items', async () => {
        await expectPinnedGenesCountText(page, 1);
        await expectPinnedProteinsCountText(page, proteinCategoryProteins.length);
        await confirmPinnedProteins(page, proteinCategoryGene, proteinCategoryProteins);
      });
    });

    await test.step('RNA category pins have changed', async () => {
      await changeGctCategory(page, GCT_CATEGORIES.PROTEIN, GCT_CATEGORIES.RNA);
      await expectPinnedGenesCountText(page, 1);
      await confirmPinnedItemsByGeneName(page, proteinCategoryGene, 1);
    });

    await test.step('Protein category pins are maintained', async () => {
      await changeGctCategory(page, GCT_CATEGORIES.RNA, GCT_CATEGORIES.PROTEIN);
      await changeGctSubcategory(
        page,
        GCT_CATEGORIES.PROTEIN,
        GCT_PROTEIN_SUBCATEGORIES.SRM,
        GCT_PROTEIN_SUBCATEGORIES.TMT,
      );

      await test.step('confirm pinned items', async () => {
        await expectPinnedGenesCountText(page, 1);
        await expectPinnedProteinsCountText(page, proteinCategoryProteins.length);
        await confirmPinnedProteins(page, proteinCategoryGene, proteinCategoryProteins);
      });
    });
  });
});
