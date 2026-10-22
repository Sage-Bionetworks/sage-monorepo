import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { MessageBarComponent, MessageBarSeverity } from './message-bar.component';

const MESSAGE = 'We encountered a problem loading results.';
const BUTTON_LABEL = 'Retry Loading Results';

async function setup(
  inputs: { severity?: MessageBarSeverity; buttonLabel?: string | null; loading?: boolean } = {},
) {
  const user = userEvent.setup();
  const buttonClick = jest.fn();
  await render(MessageBarComponent, {
    inputs: { message: MESSAGE, buttonLabel: BUTTON_LABEL, ...inputs },
    on: { buttonClick },
  });
  return { user, buttonClick };
}

describe('MessageBarComponent', () => {
  it('should announce a warn message as an alert', async () => {
    await setup();

    expect(screen.getByRole('alert')).toHaveTextContent(MESSAGE);
  });

  it('should not announce an info message as an alert', async () => {
    await setup({ severity: 'info', buttonLabel: null });

    expect(screen.queryByRole('alert')).toBeNull();
    expect(screen.getByText(MESSAGE)).toBeInTheDocument();
  });

  it('should emit buttonClick when the button is clicked', async () => {
    const { user, buttonClick } = await setup();

    await user.click(screen.getByRole('button', { name: BUTTON_LABEL }));

    expect(buttonClick).toHaveBeenCalledTimes(1);
  });

  it('should disable the button while loading', async () => {
    await setup({ loading: true });

    expect(screen.getByRole('button', { name: BUTTON_LABEL })).toBeDisabled();
  });

  it('should show no button without a label', async () => {
    await setup({ buttonLabel: null });

    expect(screen.queryByRole('button')).toBeNull();
  });
});
