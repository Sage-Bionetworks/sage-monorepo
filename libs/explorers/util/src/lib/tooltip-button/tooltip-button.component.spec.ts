import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { TooltipButtonComponent } from './tooltip-button.component';

describe('TooltipButtonComponent', () => {
  const defaultTooltipText = 'Helpful tooltip';
  const onClickSpy = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  async function setup(inputs: Partial<Record<string, any>> = {}) {
    const user = userEvent.setup();
    await render(TooltipButtonComponent, {
      componentInputs: {
        tooltipText: defaultTooltipText,
        onClick: onClickSpy,
        ...inputs,
      },
    });
    return { user };
  }

  it('should render the button', async () => {
    await setup();
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  it('should render provided label text', async () => {
    await setup({ buttonLabel: 'Info' });
    expect(screen.getByRole('button', { name: /info/i })).toBeInTheDocument();
  });

  it('should pass through aria label', async () => {
    await setup({ buttonProps: { ariaLabel: 'Custom label' } });
    expect(screen.getByRole('button', { name: /custom label/i })).toBeInTheDocument();
  });

  it('should call onClick when clicked', async () => {
    const { user } = await setup({ buttonProps: { ariaLabel: 'Do action' } });
    await user.click(screen.getByRole('button', { name: /do action/i }));
    expect(onClickSpy).toHaveBeenCalled();
  });

  it('should show tooltip text on hover', async () => {
    const { user } = await setup({ buttonProps: { ariaLabel: 'Has tooltip' } });
    const btn = screen.getByRole('button', { name: /has tooltip/i });
    await user.hover(btn);
    expect(await screen.findByText(defaultTooltipText)).toBeInTheDocument();
  });
});
