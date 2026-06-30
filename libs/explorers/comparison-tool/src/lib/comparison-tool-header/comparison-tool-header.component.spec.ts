import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  ComparisonToolService,
  HelperService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { ComparisonToolHeaderComponent } from './comparison-tool-header.component';

const configWithFilters = {
  page: 'Model Overview' as const,
  dropdowns: ['Option A'],
  row_count: null,
  columns: [],
  filters: [{ name: 'Type', data_key: 'type', query_param_key: 'type', values: ['A', 'B'] }],
};

const configWithNoFilters = {
  page: 'Model Overview' as const,
  dropdowns: ['Option B'],
  row_count: null,
  columns: [],
  filters: [],
};

async function setup(configs: (typeof configWithFilters)[]) {
  const component = await render(ComparisonToolHeaderComponent, {
    providers: [
      provideRouter([]),
      HelperService,
      provideHttpClient(),
      MessageService,
      ...provideComparisonToolService({ configs }),
      ...provideComparisonToolFilterService(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });
  const ctService = component.fixture.debugElement.injector.get(ComparisonToolService);
  component.fixture.detectChanges();
  return { component, ctService };
}

describe('ComparisonToolHeaderComponent', () => {
  it('should show Filter Results button when current config has filters', async () => {
    await setup([configWithFilters]);
    expect(screen.queryByRole('button', { name: 'Filter Results' })).toBeInTheDocument();
  });

  it('should hide Filter Results button when current config has no filters', async () => {
    await setup([configWithNoFilters]);
    expect(screen.queryByRole('button', { name: 'Filter Results' })).not.toBeInTheDocument();
  });

  it('should close filter panel when switching to a config with no filters', async () => {
    const { ctService, component } = await setup([configWithFilters, configWithNoFilters]);
    ctService.openFilterPanel();
    component.fixture.detectChanges();
    expect(ctService.isFilterPanelOpen()).toBe(true);

    ctService.setDropdownSelection(['Option B']);
    component.fixture.detectChanges();
    expect(ctService.isFilterPanelOpen()).toBe(false);
  });
});
