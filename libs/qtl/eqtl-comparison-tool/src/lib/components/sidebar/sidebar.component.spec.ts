import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { SidebarComponent } from './sidebar.component';

async function setup() {
  return render(SidebarComponent, {
    providers: [provideNoopAnimations()],
  });
}

describe('SidebarComponent', () => {
  it('renders all three chiclets with the expected label/value pairs', async () => {
    await setup();
    expect(screen.getByText('variant:')).toBeInTheDocument();
    expect(screen.getByText(/rs29475839/)).toBeInTheDocument();
    expect(screen.getByText('gene:')).toBeInTheDocument();
    expect(screen.getByText(/PAK1/)).toBeInTheDocument();
    expect(screen.getByText('cell type:')).toBeInTheDocument();
    expect(screen.getByText(/Astrocyte/)).toBeInTheDocument();
  });

  it('renders all three tab headers', async () => {
    await setup();
    expect(screen.getByRole('tab', { name: 'Impact' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Traits' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Resources' })).toBeInTheDocument();
  });

  it('puts only the active tab in the tab order (roving tabindex)', async () => {
    await setup();
    expect(screen.getByRole('tab', { name: 'Impact' })).toHaveAttribute('tabindex', '0');
    expect(screen.getByRole('tab', { name: 'Traits' })).toHaveAttribute('tabindex', '-1');
    expect(screen.getByRole('tab', { name: 'Resources' })).toHaveAttribute('tabindex', '-1');
  });

  it('moves focus and activation to the next tab on ArrowRight (wrapping at the end)', async () => {
    const user = userEvent.setup();
    await setup();

    const impact = screen.getByRole('tab', { name: 'Impact' });
    const traits = screen.getByRole('tab', { name: 'Traits' });
    const resources = screen.getByRole('tab', { name: 'Resources' });

    impact.focus();
    await user.keyboard('{ArrowRight}');
    expect(traits).toHaveFocus();
    expect(traits).toHaveAttribute('aria-selected', 'true');

    await user.keyboard('{ArrowRight}');
    expect(resources).toHaveFocus();

    await user.keyboard('{ArrowRight}');
    expect(impact).toHaveFocus();
  });

  it('jumps to the last tab on End and the first tab on Home', async () => {
    const user = userEvent.setup();
    await setup();

    screen.getByRole('tab', { name: 'Impact' }).focus();

    await user.keyboard('{End}');
    expect(screen.getByRole('tab', { name: 'Resources' })).toHaveFocus();

    await user.keyboard('{Home}');
    expect(screen.getByRole('tab', { name: 'Impact' })).toHaveFocus();
  });
});
