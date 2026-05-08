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

function renderTemplate(template: string, options: RenderComponentOptions<unknown> = {}) {
  return render(template, {
    imports: [ChicletComponent],
    ...options,
    providers: [...baseProviders, ...(options.providers ?? [])],
  });
}

describe('ChicletComponent', () => {
  it('should render projected text content', async () => {
    await renderTemplate(`<explorers-chiclet>PAK1</explorers-chiclet>`);
    expect(screen.getByText('PAK1')).toBeInTheDocument();
  });

  it('should render projected markup content', async () => {
    await renderTemplate(`<explorers-chiclet><b>biodomain:</b> Synapse</explorers-chiclet>`);
    const bold = screen.getByText('biodomain:');
    expect(bold.tagName).toBe('B');
    expect(screen.getByText(/Synapse/)).toBeInTheDocument();
  });

  it('should apply backgroundColor and textColor inputs to the chiclet root', async () => {
    const { fixture } = await renderTemplate(
      `<explorers-chiclet backgroundColor="#4caf50" textColor="white">PAK1</explorers-chiclet>`,
    );
    const root = fixture.nativeElement.querySelector('.explorers-chiclet') as HTMLElement;
    expect(root.style.backgroundColor).toBe('rgb(76, 175, 80)');
    expect(root.style.color).toBe('white');
  });

  it('should apply closeIconColor to the remove button and pass closeIconSize through to the icon', async () => {
    const { fixture } = await renderTemplate(
      `<explorers-chiclet [removable]="true" closeIconColor="#000" [closeIconSize]="10">PAK1</explorers-chiclet>`,
    );
    const removeBtn = screen.getByRole('button');
    expect(removeBtn.style.color).toBe('rgb(0, 0, 0)');
    const iconBox = fixture.nativeElement.querySelector('explorers-svg-icon > div') as HTMLElement;
    expect(iconBox.style.width).toBe('10px');
    expect(iconBox.style.height).toBe('10px');
  });

  it('should leave color styles unset and default close icon size to 14 when inputs are omitted', async () => {
    const { fixture } = await renderTemplate(
      `<explorers-chiclet [removable]="true">PAK1</explorers-chiclet>`,
    );
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
    await renderTemplate(`<explorers-chiclet>PAK1</explorers-chiclet>`);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  it('should emit removed when the remove control is clicked', async () => {
    const user = userEvent.setup();
    const removed = jest.fn();
    await renderTemplate(
      `<explorers-chiclet [removable]="true" (removed)="onRemoved()">sex: Female</explorers-chiclet>`,
      { componentProperties: { onRemoved: removed } },
    );

    const removeButton = screen.getByRole('button');
    await user.click(removeButton);

    expect(removed).toHaveBeenCalledTimes(1);
  });

  it('should apply removeAriaLabel to the remove control when provided', async () => {
    await renderTemplate(
      `<explorers-chiclet [removable]="true" removeAriaLabel="Clear sex: Female">sex: Female</explorers-chiclet>`,
    );
    expect(screen.getByRole('button', { name: 'Clear sex: Female' })).toBeInTheDocument();
  });

  it('should default the remove control aria-label to "Remove" when removeAriaLabel is omitted', async () => {
    await renderTemplate(`<explorers-chiclet [removable]="true">sex: Female</explorers-chiclet>`);
    expect(screen.getByRole('button', { name: 'Remove' })).toBeInTheDocument();
  });

  it('should render the close icon via the shared SvgIconComponent', async () => {
    const { fixture } = await renderTemplate(
      `<explorers-chiclet [removable]="true" [closeIconSize]="10">PAK1</explorers-chiclet>`,
    );
    expect(fixture.nativeElement.querySelector('explorers-svg-icon')).not.toBeNull();
    expect(fixture.nativeElement.querySelector(`[data-testid="${mockSvgTestId}"]`)).not.toBeNull();
  });

  it('should apply the fontSize input to the projected label', async () => {
    const { fixture } = await renderTemplate(
      `<explorers-chiclet fontSize="14px">PAK1</explorers-chiclet>`,
    );
    const labelEl = fixture.nativeElement.querySelector('.chiclet-label') as HTMLElement;
    expect(labelEl.style.fontSize).toBe('14px');
  });

  it('should not set an inline font-size when fontSize is omitted', async () => {
    const { fixture } = await renderTemplate(`<explorers-chiclet>PAK1</explorers-chiclet>`);
    const labelEl = fixture.nativeElement.querySelector('.chiclet-label') as HTMLElement;
    expect(labelEl.style.fontSize).toBe('');
  });
});
