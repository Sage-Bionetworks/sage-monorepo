import { Page, expect } from '@playwright/test';
import { expectComparisonToolTableLoaded } from '@sagebionetworks/explorers/testing/e2e';
import {
  ComparisonToolConfig,
  DiseaseCorrelation,
  DiseaseCorrelationsPage,
  MarmosetModelOverview,
  MarmosetModelOverviewsPage,
  MouseModelOverview,
  MouseModelOverviewsPage,
  Transcriptomics,
  TranscriptomicsPage,
} from '@sagebionetworks/model-ad/api-client';
import { baseURL } from '../../playwright.config';
import {
  COMPARISON_TOOL_API_PATHS,
  COMPARISON_TOOL_CONFIG_PATH,
  COMPARISON_TOOL_DEFAULT_SORTS,
  COMPARISON_TOOL_NAV_TRAILS,
  COMPARISON_TOOL_PATHS,
} from '../constants';

export const navigateToComparison = async (
  page: Page,
  name: string,
  shouldCloseVisualizationOverviewDialog = false,
  navigateBy: 'url' | 'link' = 'url',
  queryParameters?: string,
) => {
  if (navigateBy === 'url') {
    const path = COMPARISON_TOOL_PATHS[name];
    const urlPath = queryParameters ? `${path}?${queryParameters}` : path;
    await page.goto(urlPath);
  } else {
    // Open the hamburger menu if the button is visible (mobile breakpoint)
    const menuButton = page.locator('.hamburger-menu-button');
    if (await menuButton.isVisible().catch(() => false)) {
      await menuButton.click();
    }

    const navTrail = COMPARISON_TOOL_NAV_TRAILS[name];
    if (navTrail.length > 1) {
      // Desktop renders the parent nav item as a dropdown trigger that must be opened first;
      // mobile renders the children directly, so there is no trigger to click.
      const dropdownTrigger = page.getByRole('button', { name: navTrail[0] });
      if (await dropdownTrigger.isVisible().catch(() => false)) {
        await dropdownTrigger.click();
      }
    }
    await page.getByRole('link', { name: navTrail[navTrail.length - 1] }).click();
  }

  await expectComparisonToolTableLoaded(page, name, shouldCloseVisualizationOverviewDialog);
};

export const fetchComparisonToolData = async <T>(
  page: Page,
  name: string,
  categories: string[] = [],
  filterParams: Record<string, string[]> = {},
): Promise<T> => {
  const params = new URLSearchParams();
  params.append('itemFilterType', 'exclude');
  for (const category of categories) {
    params.append('categories', category);
  }

  for (const [key, values] of Object.entries(filterParams)) {
    for (const value of values) {
      params.append(key, value);
    }
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

export const fetchMouseModelOverviews = async (page: Page): Promise<MouseModelOverview[]> => {
  const data = await fetchComparisonToolData<MouseModelOverviewsPage>(page, 'Model Overview');
  return data.mouseModelOverviews;
};

export const fetchMarmosetModelOverviews = async (page: Page): Promise<MarmosetModelOverview[]> => {
  const data = await fetchComparisonToolData<MarmosetModelOverviewsPage>(
    page,
    'Marmoset Model Overview',
  );
  return data.marmosetModelOverviews;
};

export const fetchDiseaseCorrelations = async (
  page: Page,
  categories = ['CONSENSUS NETWORK MODULES', 'ECM Organization - Consensus Cluster A'],
): Promise<DiseaseCorrelation[]> => {
  const data = await fetchComparisonToolData<DiseaseCorrelationsPage>(
    page,
    'Disease Correlation',
    categories,
  );
  return data.diseaseCorrelations;
};

export const fetchTranscriptomics = async (
  page: Page,
  categories = ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Cerebral Cortex'],
  filterParams: Record<string, string[]> = {},
): Promise<Transcriptomics[]> => {
  const data = await fetchComparisonToolData<TranscriptomicsPage>(
    page,
    'Differential Expression',
    categories,
    filterParams,
  );
  return data.transcriptomics;
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
