import { render, screen } from '@testing-library/angular';
import { StatCardComponent } from './stat-card.component';

const baseInputs = {
  iconPath: '/path/to/icon.svg',
  iconAltText: 'icon',
  header: 'Total QTLs',
};

describe('StatCardComponent', () => {
  it('should display the header', async () => {
    await render(StatCardComponent, { componentInputs: baseInputs });
    expect(screen.getByText('Total QTLs')).toBeInTheDocument();
  });

  it('should display the sub-header when provided', async () => {
    await render(StatCardComponent, {
      componentInputs: { ...baseInputs, subHeader: 'across 53 tissues' },
    });
    expect(screen.getByText('across 53 tissues')).toBeInTheDocument();
  });

  it('should not render a sub-header when omitted', async () => {
    await render(StatCardComponent, { componentInputs: baseInputs });
    expect(document.querySelector('.stat-card-sub-header')).not.toBeInTheDocument();
  });

  it('should always display the icon using the provided alt text', async () => {
    await render(StatCardComponent, {
      componentInputs: { ...baseInputs, iconAltText: 'studies icon' },
    });
    expect(screen.getByAltText('studies icon')).toBeInTheDocument();
  });
});
