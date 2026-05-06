import { render, screen } from '@testing-library/angular';
import { StatCardComponent } from './stat-card.component';

describe('StatCardComponent', () => {
  it('should display the value', async () => {
    await render(StatCardComponent, {
      componentInputs: { value: '1,234', label: 'Total QTLs' },
    });
    expect(screen.getByText('1,234')).toBeInTheDocument();
  });

  it('should display the label', async () => {
    await render(StatCardComponent, {
      componentInputs: { value: '1,234', label: 'Total QTLs' },
    });
    expect(screen.getByText('Total QTLs')).toBeInTheDocument();
  });

  it('should display an icon when iconPath is provided', async () => {
    await render(StatCardComponent, {
      componentInputs: {
        value: '42',
        label: 'Studies',
        iconPath: '/path/to/icon.svg',
        iconAltText: 'studies icon',
      },
    });
    expect(screen.getByAltText('studies icon')).toBeInTheDocument();
  });

  it('should not display an icon when iconPath is omitted', async () => {
    await render(StatCardComponent, {
      componentInputs: { value: '42', label: 'Studies' },
    });
    expect(screen.queryByRole('img')).not.toBeInTheDocument();
  });
});
