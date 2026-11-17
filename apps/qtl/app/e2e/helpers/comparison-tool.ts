import { Page } from '@playwright/test';
import {
  expectComparisonToolTableLoaded,
  HeaderNavTrail,
  navigateViaHeaderNav,
} from '@sagebionetworks/explorers/testing/e2e';
import type { ComparisonToolPage } from '@sagebionetworks/qtl/api-client';

export const EQTL_CT_PAGE: ComparisonToolPage = 'eQTLs';
export const EQTL_CT_PATH = '/comparison/eqtl';
export const EQTL_CT_NAV_TRAIL: HeaderNavTrail = { link: 'eQTL Explorer' };

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
