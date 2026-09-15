import { Page, expect } from '@playwright/test';
import {
  expectComparisonToolTableLoaded,
  navigateViaHeaderNav,
} from '@sagebionetworks/explorers/testing/e2e';
import {
  ComparisonToolConfig,
  ComparisonToolPage,
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
  COMPARISON_TOOL_HEADER_TITLES,
  COMPARISON_TOOL_NAV_TRAILS,
  COMPARISON_TOOL_PATHS,
} from '../constants';

export const navigateToComparison = async (
  page: Page,
  name: ComparisonToolPage,
  shouldCloseVisualizationOverviewDialog = false,
  navigateBy: 'url' | 'link' = 'url',
  queryParameters?: string,
) => {
  if (navigateBy === 'url') {
    const path = COMPARISON_TOOL_PATHS[name];
    const urlPath = queryParameters ? `${path}?${queryParameters}` : path;
    await page.goto(urlPath);
  } else {
    await navigateViaHeaderNav(page, COMPARISON_TOOL_NAV_TRAILS[name]);
  }

  await expectComparisonToolTableLoaded(
    page,
    COMPARISON_TOOL_HEADER_TITLES[name] ?? name,
    shouldCloseVisualizationOverviewDialog,
  );
};

export const fetchComparisonToolData = async <T>(
  page: Page,
  name: ComparisonToolPage,
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
  name: ComparisonToolPage,
): Promise<ComparisonToolConfig[]> => {
  const response = await page.request.get(`${baseURL}/api/v1/${COMPARISON_TOOL_CONFIG_PATH}`, {
    params: { page: name },
  });
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as ComparisonToolConfig[];
  return data;
};
