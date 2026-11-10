import { render, screen } from '@testing-library/angular';
import { MetadataChicletComponent } from './metadata-chiclet.component';

async function setup({
  backgroundColor = 'var(--color-gray-300)',
  label = 'variant',
  value = 'rs29475839',
  textColor,
}: { backgroundColor?: string; label?: string; value?: string; textColor?: string } = {}) {
  return render(MetadataChicletComponent, {
    componentInputs: { backgroundColor, label, value, ...(textColor && { textColor }) },
  });
}

describe('MetadataChicletComponent', () => {
  it('renders the label with a trailing colon', async () => {
    await setup({ label: 'gene' });
    expect(screen.getByText('gene:')).toBeInTheDocument();
  });

  it('renders the value', async () => {
    await setup({ value: 'PAK1' });
    expect(screen.getByText(/PAK1/)).toBeInTheDocument();
  });

  it('defaults the text color to dark gray', async () => {
    const { container } = await setup();
    expect(container.querySelector('.explorers-chiclet')).toHaveStyle({
      color: 'var(--color-gray-900)',
    });
  });

  it('applies a custom text color', async () => {
    const lightTextColor = 'var(--color-white)';
    const { container } = await setup({ textColor: lightTextColor });
    expect(container.querySelector('.explorers-chiclet')).toHaveStyle({ color: lightTextColor });
  });
});
