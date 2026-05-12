import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
  provideExplorersConfig,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolConfigs,
  provideLoadingIconColors,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { render, screen } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { ComparisonToolComponent } from './comparison-tool.component';

function getTestProviders() {
  return [
    provideHttpClient(),
    provideNoopAnimations(),
    provideLoadingIconColors(),
    MessageService,
    provideExplorersConfig({ visualizationOverviewPanes: [] }),
    ...provideComparisonToolService({
      configs: mockComparisonToolConfigs,
    }),
    ...provideComparisonToolFilterService(),
    { provide: SvgIconService, useClass: SvgIconServiceStub },
  ];
}

async function setup() {
  const { fixture } = await render(ComparisonToolComponent, {
    imports: [LoadingContainerComponent],
    providers: getTestProviders(),
  });

  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('Comparison Tool Component', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should have loading results count while loading', async () => {
    const { component } = await setup();
    expect(component.isLoading()).toBe(true);
    expect(component.loadingResultsCount()).toBe(mockComparisonToolConfigs[0].row_count);
  });

  it('does not render the sidebar by default', async () => {
    const { container } = await render(ComparisonToolComponent, {
      imports: [LoadingContainerComponent],
      providers: getTestProviders(),
    });
    expect(container.querySelector('.comparison-tool-sidebar')).toBeNull();
  });

  it('renders projected sidebar content when hasSidebar is true', async () => {
    const { container } = await render(
      '<explorers-comparison-tool [isLoading]="false" [hasSidebar]="true">' +
        '<div ctSidebar>sidebar test content</div>' +
        '</explorers-comparison-tool>',
      {
        imports: [ComparisonToolComponent, LoadingContainerComponent],
        providers: getTestProviders(),
      },
    );
    expect(container.querySelector('.comparison-tool-sidebar')).not.toBeNull();
    expect(screen.getByText('sidebar test content')).toBeInTheDocument();
  });
});
