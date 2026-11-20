import { expect, Page } from '@playwright/test';
import {
  buildComparisonToolFetchParams,
  ComparisonToolFetchOptions,
  expectComparisonToolTableLoaded,
  HeaderNavTrail,
  navigateViaHeaderNav,
} from '@sagebionetworks/explorers/testing/e2e';
import type { ComparisonToolPage, Eqtl, EqtlsPage } from '@sagebionetworks/qtl/api-client';
import { baseURL } from '../../playwright.config';

export const EQTL_CT_PAGE: ComparisonToolPage = 'eQTLs';
export const EQTL_CT_PATH = '/comparison/eqtl';
export const EQTL_CT_NAV_TRAIL: HeaderNavTrail = { link: 'eQTL Explorer' };
export const EQTL_CT_DEFAULT_SORT = { field: 'adj_p_val', order: 1 } as const;

export const navigateToEqtlComparison = async (
  page: Page,
  shouldCloseTutorialDialog = false,
  navigateBy: 'url' | 'link' = 'url',
  queryParameters?: string,
) => {
  if (navigateBy === 'url') {
    await page.goto(queryParameters ? `${EQTL_CT_PATH}?${queryParameters}` : EQTL_CT_PATH);
  } else {
    await navigateViaHeaderNav(page, EQTL_CT_NAV_TRAIL);
  }

  await expectComparisonToolTableLoaded(page, EQTL_CT_PAGE, shouldCloseTutorialDialog);
};

export const fetchEqtls = async (
  page: Page,
  options: ComparisonToolFetchOptions = {},
  filters: Record<string, string[]> = {},
): Promise<Eqtl[]> => {
  const params = buildComparisonToolFetchParams(
    options,
    new URLSearchParams({
      itemFilterType: 'exclude',
      sortFields: EQTL_CT_DEFAULT_SORT.field,
      sortOrders: EQTL_CT_DEFAULT_SORT.order.toString(),
    }),
  );
  for (const [key, values] of Object.entries(filters)) {
    for (const value of values) {
      params.append(key, value);
    }
  }

  const response = await page.request.get(`${baseURL}/api/v1/comparison-tools/eqtl`, { params });
  expect(response.ok()).toBeTruthy();
  return ((await response.json()) as EqtlsPage).eqtls;
};
