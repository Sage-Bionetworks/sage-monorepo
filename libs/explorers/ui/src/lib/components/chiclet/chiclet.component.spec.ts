import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { mockSvgTestId, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, RenderComponentOptions, screen } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { ChicletComponent } from './chiclet.component';

const baseProviders = [
  provideHttpClient(),
  { provide: SvgIconService, useClass: SvgIconServiceStub },
];

function renderChiclet(options: RenderComponentOptions<ChicletComponent> = {}) {
  return render(ChicletComponent, {
    ...options,
    providers: [...baseProviders, ...(options.providers ?? [])],
  });
}

function renderTemplate(template: string, options: RenderComponentOptions<unknown> = {}) {
  return render(template, {
    imports: [ChicletComponent],
    ...options,
    providers: [...baseProviders, ...(options.providers ?? [])],
  });
}

describe('ChicletComponent', () => {
  it('should render the label input as text', async () => {
    await renderChiclet({ inputs: { label: 'PAK1' } });
    expect(screen.getByText('PAK1')).toBeInTheDocument();
  });

  it('should render HTML markup in the label input', async () => {
    await renderChiclet({ inputs: { label: '<b>biodomain:</b>&nbsp;Synapse' } });
    const bold = screen.getByText('biodomain:');
    expect(bold.tagName).toBe('B');
    expect(screen.getByText(/Synapse/)).toBeInTheDocument();
  });

  it('should render projected content', async () => {
    await renderTemplate(`<explorers-chiclet><b>biodomain:</b> Synapse</explorers-chiclet>`);
    const bold = screen.getByText('biodomain:');
    expect(bold.tagName).toBe('B');
    expect(screen.getByText(/Synapse/)).toBeInTheDocument();
  });

  it('should render both the label and projected content together, label first', async () => {
    const { fixture } = await renderTemplate(
      `<explorers-chiclet label="from input"><span data-testid="projected">from projection</span></explorers-chiclet>`,
    );
    expect(screen.getByText('from input')).toBeInTheDocument();
    expect(screen.getByTestId('projected')).toBeInTheDocument();

    const labelEl = fixture.nativeElement.querySelector('.chiclet-label') as HTMLElement;
    const inputIndex = labelEl.textContent?.indexOf('from input') ?? -1;
    const projectionIndex = labelEl.textContent?.indexOf('from projection') ?? -1;
    expect(inputIndex).toBeGreaterThanOrEqual(0);
    expect(projectionIndex).toBeGreaterThan(inputIndex);
  });

  it('should apply backgroundColor and textColor inputs to the chiclet root', async () => {
    const { fixture } = await renderChiclet({
      inputs: { label: 'PAK1', backgroundColor: '#4caf50', textColor: 'white' },
    });
    const root = fixture.nativeElement.querySelector('.explorers-chiclet') as HTMLElement;
    expect(root.style.backgroundColor).toBe('rgb(76, 175, 80)');
    expect(root.style.color).toBe('white');
  });

  it('should apply closeIconColor to the remove button and pass closeIconSize through to the icon', async () => {
    const { fixture } = await renderChiclet({
      inputs: { label: 'PAK1', closeIconColor: '#000', closeIconSize: 10, removable: true },
    });
    const removeBtn = screen.getByRole('button');
    expect(removeBtn.style.color).toBe('rgb(0, 0, 0)');
    const iconBox = fixture.nativeElement.querySelector('explorers-svg-icon > div') as HTMLElement;
    expect(iconBox.style.width).toBe('10px');
    expect(iconBox.style.height).toBe('10px');
  });

  it('should leave color styles unset and default close icon size to 14 when inputs are omitted', async () => {
    const { fixture } = await renderChiclet({ inputs: { label: 'PAK1', removable: true } });
    const root = fixture.nativeElement.querySelector('.explorers-chiclet') as HTMLElement;
    expect(root.style.backgroundColor).toBe('');
    expect(root.style.color).toBe('');
    const removeBtn = screen.getByRole('button');
    expect(removeBtn.style.color).toBe('');
    const iconBox = fixture.nativeElement.querySelector('explorers-svg-icon > div') as HTMLElement;
    expect(iconBox.style.width).toBe('14px');
    expect(iconBox.style.height).toBe('14px');
  });

  it('should not render a remove control by default', async () => {
    await renderChiclet({ inputs: { label: 'PAK1' } });
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  it('should emit removed when the remove control is clicked', async () => {
    const user = userEvent.setup();
    const removed = jest.fn();
    await renderChiclet({
      inputs: { label: 'sex: Female', removable: true },
      on: { removed },
    });

    const removeButton = screen.getByRole('button');
    await user.click(removeButton);

    expect(removed).toHaveBeenCalledTimes(1);
  });

  it('should apply removeAriaLabel to the remove control when provided', async () => {
    await renderChiclet({
      inputs: { label: 'sex: Female', removable: true, removeAriaLabel: 'Clear sex: Female' },
    });
    expect(screen.getByRole('button', { name: 'Clear sex: Female' })).toBeInTheDocument();
  });

  it('should default the remove control aria-label to "Remove" when removeAriaLabel is omitted', async () => {
    await renderChiclet({ inputs: { label: 'sex: Female', removable: true } });
    expect(screen.getByRole('button', { name: 'Remove' })).toBeInTheDocument();
  });

  it('should render the close icon via the shared SvgIconComponent', async () => {
    const { fixture } = await renderChiclet({
      inputs: { label: 'PAK1', removable: true, closeIconSize: 10 },
    });
    expect(fixture.nativeElement.querySelector('explorers-svg-icon')).not.toBeNull();
    expect(fixture.nativeElement.querySelector(`[data-testid="${mockSvgTestId}"]`)).not.toBeNull();
  });

  it('should apply the fontSize input to the projected label', async () => {
    const { fixture } = await renderChiclet({
      inputs: { label: 'PAK1', fontSize: '14px' },
    });
    const labelEl = fixture.nativeElement.querySelector('.chiclet-label') as HTMLElement;
    expect(labelEl.style.fontSize).toBe('14px');
  });

  it('should not set an inline font-size when fontSize is omitted', async () => {
    const { fixture } = await renderChiclet({ inputs: { label: 'PAK1' } });
    const labelEl = fixture.nativeElement.querySelector('.chiclet-label') as HTMLElement;
    expect(labelEl.style.fontSize).toBe('');
  });
});
