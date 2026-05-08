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
    componentInputs: {
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
  it('should display composed title and description', async () => {
    await setup();

    expect(screen.getByText(MOCK_TITLE, { exact: false })).toBeVisible();
    expect(screen.getByText(MOCK_DESCRIPTION, { exact: false })).toBeVisible();
  });

  it('should remove filter item when the chip remove control is activated', async () => {
    const { user } = await setup();

    const item = screen.getByText(MOCK_TITLE, { exact: false });
    expect(item).toBeVisible();

    const removeButton = screen.getByRole('button');
    await user.click(removeButton);

    expect(item).not.toBeVisible();
  });

  it('should label the remove control with the description for screen readers', async () => {
    await setup();

    expect(screen.getByRole('button', { name: `Clear ${MOCK_DESCRIPTION}` })).toBeInTheDocument();
  });

  it('should fall back to title when description is empty', async () => {
    await render(ComparisonToolFilterListItemComponent, {
      componentInputs: {
        item: { label: 'some option', selected: true },
        title: MOCK_TITLE,
        description: '',
        isVisible: true,
      },
      providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
    });

    expect(screen.getByRole('button', { name: `Clear ${MOCK_TITLE}` })).toBeInTheDocument();
  });
});
