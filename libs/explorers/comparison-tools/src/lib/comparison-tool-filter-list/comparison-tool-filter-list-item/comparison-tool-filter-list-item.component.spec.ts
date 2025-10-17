import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { ComparisonToolFilterListItemComponent } from './comparison-tool-filter-list-item.component';

const MOCK_TITLE = '1234';
const MOCK_DESCRIPTION = '5678';

async function setup() {
  const user = userEvent.setup();
  const component = await render(ComparisonToolFilterListItemComponent, {
    componentProperties: {
      item: { label: 'some option', selected: true },
      title: MOCK_TITLE,
      description: MOCK_DESCRIPTION,
      isVisible: true,
    },
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
  return { user, component };
}

describe('Component: Comparison Tool - Filter List Item', () => {
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
