import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import {
  PlatformService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  provideExplorersConfig,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { ComparisonToolConfigService, ComparisonToolPage } from '@sagebionetworks/qtl/api-client';
import { render, screen } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { EqtlComparisonToolComponent } from './eqtl-comparison-tool.component';
import { EqtlComparisonToolService } from './services/eqtl-comparison-tool.service';

async function setup() {
  const { fixture } = await render(EqtlComparisonToolComponent, {
    providers: [
      provideHttpClient(),
      provideNoopAnimations(),
      provideLoadingIconColors(),
      provideExplorersConfig({ tutorialPanes: [] }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
      provideRouter([]),
      {
        provide: PlatformService,
        useValue: { isBrowser: true },
      },
      {
        provide: ComparisonToolConfigService,
        useValue: {
          getComparisonToolConfig: jest.fn().mockReturnValue(of([])),
        },
      },
      MessageService,
      ...provideComparisonToolService(),
      ...provideComparisonToolFilterService(),
      EqtlComparisonToolService,
    ],
  });

  const component = fixture.componentInstance;
  const comparisonToolConfigService = fixture.debugElement.injector.get(
    ComparisonToolConfigService,
  );
  const comparisonToolService = fixture.debugElement.injector.get(EqtlComparisonToolService);
  return { component, fixture, comparisonToolConfigService, comparisonToolService };
}

describe('EqtlComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should request the eQTLs comparison tool config', async () => {
    const { comparisonToolConfigService } = await setup();
    expect(comparisonToolConfigService.getComparisonToolConfig).toHaveBeenCalledWith(
      ComparisonToolPage.EQtls,
    );
  });

  it('should hand its view config to the comparison tool service', async () => {
    const { component, comparisonToolService } = await setup();
    expect(comparisonToolService.viewConfig()).toEqual(
      expect.objectContaining(component.viewConfig),
    );
  });

  it('renders the sidebar chiclets and tabs', async () => {
    await setup();
    expect(screen.getByText('variant:')).toBeInTheDocument();
    expect(screen.getByText('gene:')).toBeInTheDocument();
    expect(screen.getByText('cell class:')).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Impact' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Traits' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Resources' })).toBeInTheDocument();
  });
});
