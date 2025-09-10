import { Page } from '@playwright/test';

export const headerSearchPlaceholder = 'Search models';

export const searchAndGetSearchListItems = async (
  query: string,
  page: Page,
  searchPlaceholder = headerSearchPlaceholder,
) => {
  const responsePromise = page.waitForResponse(`**/models/search?q=${query}`);
  const input = page.getByPlaceholder(searchPlaceholder);
  await input.pressSequentially(query);
  await responsePromise;

  const searchList = page.getByRole('list').filter({ hasText: query });
  return searchList.getByRole('listitem');
};
