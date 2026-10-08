import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { FetchFailedBarComponent } from './fetch-failed-bar.component';

const MESSAGE = 'We encountered a problem loading results.';
const BUTTON_LABEL = 'Retry Loading Results';

async function setup(loading = false) {
  const user = userEvent.setup();
  const retry = jest.fn();
  await render(FetchFailedBarComponent, {
    inputs: { message: MESSAGE, buttonLabel: BUTTON_LABEL, loading },
    on: { retry },
  });
  return { user, retry };
}

describe('FetchFailedBarComponent', () => {
  it('should announce the message as an alert', async () => {
    await setup();

    expect(screen.getByRole('alert')).toHaveTextContent(MESSAGE);
  });

  it('should emit retry when the button is clicked', async () => {
    const { user, retry } = await setup();

    await user.click(screen.getByRole('button', { name: BUTTON_LABEL }));

    expect(retry).toHaveBeenCalledTimes(1);
  });

  it('should disable the button while loading', async () => {
    await setup(true);

    expect(screen.getByRole('button', { name: BUTTON_LABEL })).toBeDisabled();
  });
});
