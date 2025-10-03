import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { FilterPanelCloseButtonComponent } from './filter-panel-close-button.component';

async function setup() {
  const user = userEvent.setup();
  let closeClickEmitted = false;

  const component = await render(FilterPanelCloseButtonComponent, {
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
    on: {
      closeClick: () => {
        closeClickEmitted = true;
      },
    },
  });

  return { user, component, closeClickEmitted: () => closeClickEmitted };
}

describe('FilterPanelCloseButtonComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render close button', async () => {
    await setup();
    expect(screen.getByRole('button', { name: 'close' })).toBeInTheDocument();
  });

  it('should emit closeClick when user clicks the button', async () => {
    const { user, closeClickEmitted } = await setup();
    const closeButton = screen.getByRole('button');

    await user.click(closeButton);

    expect(closeClickEmitted()).toBe(true);
  });
});
