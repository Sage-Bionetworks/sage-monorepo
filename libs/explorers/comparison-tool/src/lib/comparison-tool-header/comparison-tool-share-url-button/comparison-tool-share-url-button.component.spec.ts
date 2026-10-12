import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { TABLE_DATA_LOADING_MESSAGE } from '@sagebionetworks/explorers/constants';
import {
  ComparisonToolService,
  ComparisonToolServiceOptions,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { MessageService } from 'primeng/api';
import { NEVER } from 'rxjs';
import { ComparisonToolShareURLButtonComponent } from './comparison-tool-share-url-button.component';

async function setup(ctOptions: ComparisonToolServiceOptions = {}) {
  const component = await render(ComparisonToolShareURLButtonComponent, {
    providers: [
      provideRouter([]),
      provideNoopAnimations(),
      provideHttpClient(),
      MessageService,
      ...provideComparisonToolService(ctOptions),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });
  const ctService = component.fixture.debugElement.injector.get(ComparisonToolService);
  const startLoadingTableData = () => {
    ctService.fetchUnpinned(NEVER);
    component.fixture.detectChanges();
  };
  return { startLoadingTableData };
}

const getShareUrlButton = () => screen.getByRole('button', { name: 'Share URL' });

describe('ComparisonToolShareURLButtonComponent', () => {
  it('should be enabled once the table data has loaded', async () => {
    await setup();
    expect(getShareUrlButton()).toBeEnabled();
  });

  it('should be disabled while the table data is loading', async () => {
    const { startLoadingTableData } = await setup();
    startLoadingTableData();
    expect(getShareUrlButton()).toBeDisabled();
  });

  it('should explain that it is waiting for data while the table data is loading', async () => {
    const user = userEvent.setup();
    const { startLoadingTableData } = await setup();
    startLoadingTableData();
    await user.hover(getShareUrlButton());

    await waitFor(() => {
      expect(screen.getByRole('tooltip', { name: TABLE_DATA_LOADING_MESSAGE })).toBeVisible();
    });
  });

  it('should be disabled when the pinned rows failed to load, so it cannot share a URL missing the pins', async () => {
    await setup({ pinnedItems: ['a', 'b'], pinnedFetchFails: true });
    expect(getShareUrlButton()).toBeDisabled();
  });
});
