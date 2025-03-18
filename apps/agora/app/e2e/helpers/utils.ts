import { Page, expect } from '@playwright/test';

export const waitForSpinnerNotVisible = async (page: Page, timeout = 60 * 4 * 1000) => {
  await expect(page.locator('div:nth-child(4) > div > .spinner')).toBeHidden({ timeout: timeout });
};

export const convertToQueryParam = (list: string[], queryKey: string) => {
  return `${queryKey}=${list.join(',')}`;
};
