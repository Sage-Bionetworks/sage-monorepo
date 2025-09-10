import { expect, Page, test } from '@playwright/test';

const headerSearchPlaceholder = 'Search models';

const searchAndGetSearchListItems = async (
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

test.describe('search', () => {
  test('can search for model and aliases then navigate to model details from search result', async ({
    page,
  }) => {
    const modelQuery = 'abc';
    const modelName = 'Abca7*V1599M';

    await page.goto('/');

    const searchListItems = await searchAndGetSearchListItems(modelQuery, page);
    await expect(searchListItems).toHaveCount(2);

    const searchListItem = searchListItems.first();
    await expect(searchListItem).toHaveText(modelName);

    await searchListItem.click();

    await page.waitForURL(`/models/${modelName}`);
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(modelName);
  });

  test('can navigate to another model details page from a model details page', async ({ page }) => {
    const initialModel = '3xTg-AD';
    const nextModel = 'APOE4';

    await page.goto(`/models/${initialModel}`);
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(initialModel);

    const searchListItems = await searchAndGetSearchListItems(nextModel, page);
    const firstResult = searchListItems.first();
    await expect(firstResult).toHaveText(nextModel);
    await firstResult.click();

    await page.waitForURL(`/models/${nextModel}`);
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(nextModel);
  });

  test('can search for jax id', async ({ page }) => {
    const expectedResultsCount = 3;
    const modelQuery = '306';

    await page.goto('/');
    const searchListItems = await searchAndGetSearchListItems(modelQuery, page);

    await expect(searchListItems).toHaveCount(expectedResultsCount);

    for (let i = 0; i < expectedResultsCount; i++) {
      await expect(searchListItems.nth(i)).toHaveText(/Jax ID: 0306/i);
    }
  });

  test('can search for rrid', async ({ page }) => {
    const modelQuery = '348';

    await page.goto('/');
    const searchListItems = await searchAndGetSearchListItems(modelQuery, page);

    expect(await searchListItems.count()).toBe(3);
    await expect(searchListItems.first()).toHaveText(/3xtg-ad \(rrid:/i);
  });

  test('orders search results by match precedence and lexographical order', async ({ page }) => {
    const modelQuery = 'trem2';

    await page.goto('/');
    const searchListItems = await searchAndGetSearchListItems(modelQuery, page);

    await expect(searchListItems.first()).toHaveText(/trem2 ko/i);
    await expect(searchListItems.nth(1)).toHaveText(/trem2-r47h_nss/i);
    await expect(searchListItems.nth(2)).toHaveText(/trem2r47h/i);
    await expect(searchListItems.nth(3)).toHaveText(/load1 \(alias apoe4\/trem2\*r47h\)/i);
    await expect(searchListItems.last()).toHaveText(/load2 \(alias habeta\/apoe4\/trem2\*r47h\)/i);
  });

  test('can search using home card input', async ({ page }) => {
    const model = 'APOE4';
    await page.goto('/');
    const searchListItems = await searchAndGetSearchListItems(
      model,
      page,
      'Find model by name or ID...',
    );
    await expect(searchListItems.first()).toHaveText(model);
  });

  test('can search and navigate to model with special characters', async ({ page }) => {
    const modelQuery = '(iu';
    const modelName = '5xFAD (IU/Jax/Pitt)';

    await page.goto('/');

    const searchListItems = await searchAndGetSearchListItems(modelQuery, page);
    expect(await searchListItems.count()).toBe(1);
    const firstResult = searchListItems.first();
    await expect(firstResult).toHaveText(modelName);

    await firstResult.click();

    await page.waitForURL(/5xfad/i);
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(modelName);
  });

  test('shows error when query is too short', async ({ page }) => {
    await page.goto('/');

    const input = page.getByPlaceholder(headerSearchPlaceholder);
    await input.pressSequentially('ab');
    await expect(
      page.getByRole('listitem').filter({ hasText: /please enter at least three characters/i }),
    ).toBeVisible();
  });

  test('shows error when no results are returned', async ({ page }) => {
    await page.goto('/');
    const input = page.getByPlaceholder(headerSearchPlaceholder);
    await input.pressSequentially('this-model-does-not-exist');
    await expect(
      page.getByRole('listitem').filter({ hasText: /no results match your search string/i }),
    ).toBeVisible();
  });

  test('can search again after unknown error', async ({ page }) => {
    const query = 'a'.repeat(101);
    await page.goto('/');

    const input = page.getByPlaceholder(headerSearchPlaceholder);
    await input.pressSequentially(query);

    await expect(
      page
        .getByRole('listitem')
        .filter({ hasText: /an unknown error occurred, please try again./i }),
    ).toBeVisible();

    await input.press('Backspace');

    await expect(
      page.getByRole('listitem').filter({ hasText: /no results match your search string/i }),
    ).toBeVisible();
  });
});
