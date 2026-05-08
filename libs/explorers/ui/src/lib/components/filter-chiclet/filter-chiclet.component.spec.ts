import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, RenderComponentOptions, screen } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { FilterChicletComponent } from './filter-chiclet.component';

const baseProviders = [
  provideHttpClient(),
  { provide: SvgIconService, useClass: SvgIconServiceStub },
];

function renderFilterChiclet(options: RenderComponentOptions<FilterChicletComponent> = {}) {
  return render(FilterChicletComponent, {
    ...options,
    providers: [...baseProviders, ...(options.providers ?? [])],
  });
}

describe('FilterChicletComponent', () => {
  it('should render the name with a bolded prefix and the value', async () => {
    await renderFilterChiclet({ inputs: { name: 'biodomain', value: 'Synapse' } });
    const bold = screen.getByText('biodomain:', { exact: false });
    expect(bold.tagName).toBe('B');
    expect(screen.getByText('Synapse', { exact: false })).toBeInTheDocument();
  });

  it('should omit the bolded prefix when name is empty', async () => {
    const { fixture } = await renderFilterChiclet({ inputs: { value: 'Synapse' } });
    expect(fixture.nativeElement.querySelector('b')).toBeNull();
    expect(screen.getByText('Synapse')).toBeInTheDocument();
  });

  it('should label the remove control with the value for screen readers', async () => {
    await renderFilterChiclet({ inputs: { name: 'biodomain', value: 'Synapse' } });
    expect(screen.getByRole('button', { name: 'Clear Synapse' })).toBeInTheDocument();
  });

  it('should fall back to the name when value is empty', async () => {
    await renderFilterChiclet({ inputs: { name: 'biodomain' } });
    expect(screen.getByRole('button', { name: 'Clear biodomain' })).toBeInTheDocument();
  });

  it('should emit cleared when the remove control is clicked', async () => {
    const user = userEvent.setup();
    const cleared = jest.fn();
    await renderFilterChiclet({
      inputs: { name: 'sex', value: 'Female' },
      on: { cleared },
    });
    await user.click(screen.getByRole('button'));
    expect(cleared).toHaveBeenCalledTimes(1);
  });
});
