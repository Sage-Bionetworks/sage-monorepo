import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  HelperService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { ComparisonToolControlsComponent } from './comparison-tool-controls.component';

async function setup(showTableSearch: boolean) {
  const component = await render(ComparisonToolControlsComponent, {
    providers: [
      provideRouter([]),
      HelperService,
      provideHttpClient(),
      MessageService,
      ...provideComparisonToolService({ viewConfig: { showTableSearch } }),
      ...provideComparisonToolFilterService(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });
  component.fixture.detectChanges();
  return component;
}

describe('ComparisonToolControlsComponent', () => {
  it('should show search input when showTableSearch is true', async () => {
    await setup(true);
    expect(screen.queryByPlaceholderText('Value1, Value2')).toBeInTheDocument();
  });

  it('should hide search input when showTableSearch is false', async () => {
    await setup(false);
    expect(screen.queryByPlaceholderText('Value1, Value2')).not.toBeInTheDocument();
  });
});
