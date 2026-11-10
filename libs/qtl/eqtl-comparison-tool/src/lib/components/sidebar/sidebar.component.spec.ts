import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { render, screen, within } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { SidebarComponent } from './sidebar.component';

async function setup() {
  return render(SidebarComponent, {
    providers: [provideNoopAnimations()],
  });
}

function getChicletText(label: string): string | undefined {
  return screen.getByText(`${label}:`).parentElement?.textContent?.replace(/\s+/g, ' ').trim();
}

describe('SidebarComponent', () => {
  it('renders all four chiclets with the expected label/value pairs', async () => {
    await setup();
    expect(getChicletText('variant')).toBe('variant: rs29475839');
    expect(getChicletText('gene')).toBe('gene: PAK1');
    expect(getChicletText('cell class')).toBe('cell class: Astrocyte');
    expect(getChicletText('cell subclass')).toBe('cell subclass: Astrocyte');
  });

  it('shows the impact details in the Impact tab, with the same cell class as the chiclet', async () => {
    await setup();
    const impactPanel = screen.getByRole('tabpanel');
    const cellClassRow = within(impactPanel).getByRole('row', { name: /Cell Class/ });
    expect(within(impactPanel).getAllByRole('row')).toHaveLength(5);
    expect(within(cellClassRow).getByRole('cell', { name: 'Astrocyte' })).toBeInTheDocument();
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
