import { render, screen } from '@testing-library/angular';
import { ChicletCardComponent } from './chiclet-card.component';
import { Chiclet } from '@sagebionetworks/explorers/models';

describe('ChicletCardComponent', () => {
  const chiclets: Chiclet[] = [
    { label: 'PAK1', color: '#4caf50' },
    { label: 'chr1:109.8Mb', color: '#3f51b5' },
    { label: 'rs1801133', color: '#009688' },
  ];

  it('should render the title', async () => {
    await render(ChicletCardComponent, {
      inputs: { title: 'Example searches', chiclets },
    });
    expect(screen.getByText('Example searches')).toBeInTheDocument();
  });

  it('should render all chiclets', async () => {
    await render(ChicletCardComponent, {
      inputs: { title: 'Example searches', chiclets },
    });
    expect(screen.getByText('PAK1')).toBeInTheDocument();
    expect(screen.getByText('chr1:109.8Mb')).toBeInTheDocument();
    expect(screen.getByText('rs1801133')).toBeInTheDocument();
  });

  it('should apply the background color inline style', async () => {
    await render(ChicletCardComponent, {
      inputs: { title: 'Example searches', chiclets },
    });
    expect(screen.getByText('PAK1')).toHaveStyle('background-color: #4caf50');
    expect(screen.getByText('chr1:109.8Mb')).toHaveStyle('background-color: #3f51b5');
    expect(screen.getByText('rs1801133')).toHaveStyle('background-color: #009688');
  });
});
