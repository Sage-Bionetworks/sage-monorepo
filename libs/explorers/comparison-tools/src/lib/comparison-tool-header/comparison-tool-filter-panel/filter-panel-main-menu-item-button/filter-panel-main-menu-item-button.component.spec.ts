import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { mockComparisonToolFilters, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { FilterPanelMainMenuItemButtonComponent } from './filter-panel-main-menu-item-button.component';

const mockFilter = mockComparisonToolFilters[0];

async function setup() {
  const user = userEvent.setup();
  let buttonClickEmitted = false;

  const component = await render(FilterPanelMainMenuItemButtonComponent, {
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
    componentInputs: {
      filter: mockFilter,
    },
    on: {
      buttonClick: () => {
        buttonClickEmitted = true;
      },
    },
  });

  return { user, component, buttonClickEmitted: () => buttonClickEmitted };
}

describe('FilterPanelMainMenuItemButtonComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render filter name', async () => {
    await setup();
    expect(screen.getByText(mockFilter.name)).toBeInTheDocument();
  });

  it('should render button', async () => {
    await setup();
    expect(
      screen.getByRole('button', { name: `open ${mockFilter.name} filter options` }),
    ).toBeInTheDocument();
  });

  it('should emit buttonClick when user clicks the button', async () => {
    const { user, buttonClickEmitted } = await setup();
    const button = screen.getByRole('button');

    await user.click(button);

    expect(buttonClickEmitted()).toBe(true);
  });
});
