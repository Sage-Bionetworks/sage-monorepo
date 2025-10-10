import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { FilterPanelHeaderComponent } from './filter-panel-header.component';

async function setup(heading = 'Test Heading') {
  const user = userEvent.setup();
  let closeClickEmitted = false;

  const component = await render(FilterPanelHeaderComponent, {
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
    componentInputs: {
      heading,
    },
    on: {
      closeClick: () => {
        closeClickEmitted = true;
      },
    },
  });

  return { user, component, closeClickEmitted: () => closeClickEmitted };
}

describe('FilterPanelHeaderComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render header with heading', async () => {
    await setup();

    expect(screen.getByText('Test Heading')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /close/i })).toBeInTheDocument();
  });

  it('should emit closeClick when close button is clicked', async () => {
    const { user, closeClickEmitted } = await setup();

    const closeButton = screen.getByRole('button', { name: /close/i });
    await user.click(closeButton);

    expect(closeClickEmitted()).toBe(true);
  });
});
