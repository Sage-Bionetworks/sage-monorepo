import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideExplorersConfig, SvgIconService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { EqtlComparisonToolComponent } from './eqtl-comparison-tool.component';

async function setup() {
  const { fixture } = await render(EqtlComparisonToolComponent, {
    providers: [
      provideHttpClient(),
      provideNoopAnimations(),
      provideLoadingIconColors(),
      provideExplorersConfig({ visualizationOverviewPanes: [] }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });

  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('EqtlComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('renders the sidebar chiclets and tabs', async () => {
    await setup();
    expect(screen.getByText('variant:')).toBeInTheDocument();
    expect(screen.getByText('gene:')).toBeInTheDocument();
    expect(screen.getByText('cell type:')).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Impact' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Traits' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Resources' })).toBeInTheDocument();
  });
});
