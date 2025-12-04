import { Page, expect, test } from '@playwright/test';
import { getUnpinnedTable } from '@sagebionetworks/explorers/testing/e2e';
import { DiseaseCorrelation, ModelOverview } from '@sagebionetworks/model-ad/api-client';
import { baseURL } from '../../playwright.config';
import { COMPARISON_TOOL_API_PATHS, COMPARISON_TOOL_PATHS } from '../constants';

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
    await page.getByRole('link', { name: name }).click();
  }

  if (shouldCloseVisualizationOverviewDialog) {
    await closeVisualizationOverviewDialog(page);
  }

  await expect(page.getByRole('heading', { level: 1, name })).toBeVisible();
  await expect(page.locator('explorers-base-table')).toHaveCount(2);
  await expect(getUnpinnedTable(page).locator('tbody tr').first()).toBeVisible();
};

export const fetchModelOverviews = async (page: Page): Promise<ModelOverview[]> => {
  const response = await page.request.get(
    `${baseURL}/api/v1/${COMPARISON_TOOL_API_PATHS['Model Overview']}`,
    {
      params: {
        itemFilterType: 'exclude',
      },
    },
  );
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as { modelOverviews: ModelOverview[] };
  return data.modelOverviews;
};

export const fetchDiseaseCorrelations = async (page: Page): Promise<DiseaseCorrelation[]> => {
  const params = new URLSearchParams();
  params.append('itemFilterType', 'exclude');
  params.append('category', 'CONSENSUS NETWORK MODULES');
  params.append('category', 'Consensus Cluster A - ECM Organization');

  const response = await page.request.get(
    `${baseURL}/api/v1/${COMPARISON_TOOL_API_PATHS['Disease Correlation']}?${params.toString()}`,
  );
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as { diseaseCorrelations: DiseaseCorrelation[] };
  return data.diseaseCorrelations;
};
