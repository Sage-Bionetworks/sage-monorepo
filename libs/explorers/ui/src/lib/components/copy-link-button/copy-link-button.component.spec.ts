import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { CopyLinkButtonComponent } from './copy-link-button.component';

describe('CopyLinkButtonComponent', () => {
  const onClickSpy = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  async function setup(inputs: Partial<Record<string, any>> = {}) {
    const user = userEvent.setup();
    await render(CopyLinkButtonComponent, {
      componentInputs: {
        onClick: onClickSpy,
        anchorId: 'test-anchor',
        ariaLabel: 'Copy link to Test Section',
        tooltipText: 'Copy link',
        ...inputs,
      },
    });
    return { user };
  }

  it('should render the button', async () => {
    await setup();
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  it('should pass through aria label', async () => {
    await setup();
    expect(screen.getByRole('button', { name: /copy link to test section/i })).toBeInTheDocument();
  });

  it('should call onClick when clicked', async () => {
    const { user } = await setup();
    await user.click(screen.getByRole('button', { name: /copy link to test section/i }));
    expect(onClickSpy).toHaveBeenCalled();
  });
});
