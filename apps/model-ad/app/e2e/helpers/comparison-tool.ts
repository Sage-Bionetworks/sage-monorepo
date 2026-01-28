import { Page, expect, test } from '@playwright/test';
import { getUnpinnedTable } from '@sagebionetworks/explorers/testing/e2e';
import {
  ComparisonToolConfig,
  DiseaseCorrelation,
  DiseaseCorrelationsPage,
  GeneExpression,
  GeneExpressionsPage,
  ModelOverview,
  ModelOverviewsPage,
} from '@sagebionetworks/model-ad/api-client';
import { baseURL } from '../../playwright.config';
import {
  COMPARISON_TOOL_API_PATHS,
  COMPARISON_TOOL_CONFIG_PATH,
  COMPARISON_TOOL_DEFAULT_SORTS,
  COMPARISON_TOOL_PATHS,
} from '../constants';

export const closeVisualizationOverviewDialog = async (page: Page) => {
  await test.step('close visualization overview dialog', async () => {
    const dialog = page.getByRole('dialog');

    const closeBtn = dialog.getByRole('button').first();
    await closeBtn.click();

    await expect(dialog).toBeHidden();
  });
};

export const navigateToComparison = async (
  page: Page,
  name: string,
  shouldCloseVisualizationOverviewDialog = false,
  navigateBy: 'url' | 'link' = 'url',
  queryParameters?: string,
) => {
  if (navigateBy === 'url') {
    const path = COMPARISON_TOOL_PATHS[name];
    const url = queryParameters ? `${path}?${queryParameters}` : path;
    await page.goto(url);
  } else {
    // Open the hamburger menu if the button is visible (mobile breakpoint)
    const menuButton = page.locator('.hamburger-menu-button');
    if (await menuButton.isVisible().catch(() => false)) {
      await menuButton.click();
    }
    await page.getByRole('link', { name: name }).click();
  }

  if (shouldCloseVisualizationOverviewDialog) {
    await closeVisualizationOverviewDialog(page);
  }

  await expect(page.getByRole('heading', { level: 1, name })).toBeVisible();
  await expect(page.locator('explorers-base-table')).toHaveCount(2);
  await expect(getUnpinnedTable(page).locator('tbody tr').first()).toBeVisible();
};

export const fetchComparisonToolData = async <T>(
  page: Page,
  name: string,
  categories: string[] = [],
): Promise<T> => {
  const params = new URLSearchParams();
  params.append('itemFilterType', 'exclude');
  for (const category of categories) {
    params.append('categories', category);
  }

  // sortFields and sortOrders are required by the API
  const defaultSort = COMPARISON_TOOL_DEFAULT_SORTS[name];
  for (const sort of defaultSort) {
    params.append('sortFields', sort.field);
    params.append('sortOrders', sort.order.toString());
  }

  const response = await page.request.get(`${baseURL}/api/v1/${COMPARISON_TOOL_API_PATHS[name]}`, {
    params,
  });
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as T;
  return data;
};

export const fetchModelOverviews = async (page: Page): Promise<ModelOverview[]> => {
  const data = await fetchComparisonToolData<ModelOverviewsPage>(page, 'Model Overview');
  return data.modelOverviews;
};

export const fetchDiseaseCorrelations = async (
  page: Page,
  categories = ['CONSENSUS NETWORK MODULES', 'Consensus Cluster A - ECM Organization'],
): Promise<DiseaseCorrelation[]> => {
  const data = await fetchComparisonToolData<DiseaseCorrelationsPage>(
    page,
    'Disease Correlation',
    categories,
  );
  return data.diseaseCorrelations;
};

export const fetchGeneExpressions = async (
  page: Page,
  categories = ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Cortex', 'female'],
): Promise<GeneExpression[]> => {
  const data = await fetchComparisonToolData<GeneExpressionsPage>(
    page,
    'Gene Expression',
    categories,
  );
  return data.geneExpressions;
};

export const fetchComparisonToolConfig = async (
  page: Page,
  name: string,
): Promise<ComparisonToolConfig[]> => {
  const response = await page.request.get(`${baseURL}/api/v1/${COMPARISON_TOOL_CONFIG_PATH}`, {
    params: { page: name },
  });
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as ComparisonToolConfig[];
  return data;
};
