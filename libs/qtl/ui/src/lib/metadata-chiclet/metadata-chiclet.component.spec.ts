import { render, screen } from '@testing-library/angular';
import { MetadataChicletComponent } from './metadata-chiclet.component';

async function setup({
  backgroundColor = 'var(--color-gray-300)',
  label = 'variant',
  value = 'rs29475839',
} = {}) {
  return render(MetadataChicletComponent, {
    componentInputs: { backgroundColor, label, value },
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
});
