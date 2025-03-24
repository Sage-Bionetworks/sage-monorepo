import { render, screen } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { GeneComparisonToolFilterListItemComponent } from './gene-comparison-tool-filter-list-item.component';

const MOCK_TITLE = '1234';
const MOCK_DESCRIPTION = '5678';

async function setup() {
  const user = userEvent.setup();
  const component = await render(GeneComparisonToolFilterListItemComponent, {
    componentProperties: {
      item: { label: 'some option', selected: true },
      title: MOCK_TITLE,
      description: MOCK_DESCRIPTION,
      isVisible: true,
    },
  });
  return { user, component };
}

describe('Component: Gene Comparison Tool - Filter List Item', () => {
  it('should display text', async () => {
    await setup();

    expect(screen.getByText(MOCK_TITLE, { exact: false })).toBeVisible();
    expect(screen.getByText(MOCK_DESCRIPTION, { exact: false })).toBeVisible();
  });

  it('should remove filter item', async () => {
    const { user } = await setup();

    const item = screen.getByText(MOCK_TITLE, { exact: false });
    expect(item).toBeVisible();

    const clearButton = screen.getByRole('button', { name: /clear/i });
    await user.click(clearButton);

    expect(item).not.toBeVisible();
  });
});
