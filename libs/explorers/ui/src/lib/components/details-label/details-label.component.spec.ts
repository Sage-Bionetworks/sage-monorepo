import { render, screen } from '@testing-library/angular';
import { DetailsLabelComponent } from './details-label.component';

async function setup(componentInputs: Partial<{ left: string; right: string }> = {}) {
  return render(DetailsLabelComponent, { inputs: componentInputs });
}

describe('DetailsLabelComponent', () => {
  it('should render the left and right values separated by a pipe', async () => {
    await setup({ left: 'Trem2', right: 'ENSMUSG00000023992' });
    expect(screen.getByText('ENSMUSG00000023992', { exact: false })).toHaveTextContent(
      'Trem2 | ENSMUSG00000023992',
    );
  });

  it('should omit the separator when there is no right value', async () => {
    const { container } = await setup({ left: 'Trem2' });
    expect(screen.getByText('Trem2')).toHaveTextContent('Trem2');
    expect(container.querySelector('.details-label-separator')).toBeNull();
  });

  it('should render nothing when there is no left value', async () => {
    const { container } = await setup({ right: 'ENSMUSG00000023992' });
    expect(container.querySelector('.details-label')).toBeNull();
  });
});
