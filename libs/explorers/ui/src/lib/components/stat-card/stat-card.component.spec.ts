import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { mockSvgTestId, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { StatCardComponent } from './stat-card.component';

const baseInputs = {
  iconPath: '/path/to/icon.svg',
  iconAltText: 'icon',
  header: 'Total QTLs',
};

async function setup(inputs: Partial<typeof baseInputs> & Record<string, unknown> = {}) {
  return render(StatCardComponent, {
    componentInputs: { ...baseInputs, ...inputs },
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
}

describe('StatCardComponent', () => {
  it('should display the header', async () => {
    await setup();
    expect(screen.getByText('Total QTLs')).toBeInTheDocument();
  });

  it('should display the sub-header when provided', async () => {
    await setup({ subHeader: 'across 53 tissues' });
    expect(screen.getByText('across 53 tissues')).toBeInTheDocument();
  });

  it('should not render a sub-header when omitted', async () => {
    await setup();
    expect(document.querySelector('.stat-card-sub-header')).not.toBeInTheDocument();
  });

  it('should render the icon', async () => {
    await setup();
    expect(screen.getByTestId(mockSvgTestId)).toBeInTheDocument();
  });
});
