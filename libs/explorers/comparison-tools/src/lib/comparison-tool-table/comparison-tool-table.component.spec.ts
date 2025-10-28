import { provideHttpClient } from '@angular/common/http';
import { provideRouter, RouterModule } from '@angular/router';
import {
  ComparisonToolService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ComparisonToolTableComponent } from './comparison-tool-table.component';

async function setup(
  ctServiceOptions?: { pinnedItems?: string[]; maxPinnedItems?: number },
  ctFilterServiceOptions?: { searchTerm?: string },
) {
  const user = userEvent.setup();

  const component = await render(ComparisonToolTableComponent, {
    imports: [RouterModule],
    providers: [
      provideHttpClient(),
      provideRouter([]),
      ...provideComparisonToolService({
        pinnedItems: ctServiceOptions?.pinnedItems,
        maxPinnedItems: ctServiceOptions?.maxPinnedItems,
      }),
      ...provideComparisonToolFilterService({
        searchTerm: ctFilterServiceOptions?.searchTerm,
      }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });

  const fixture = component.fixture;
  const instance = fixture.componentInstance;
  const service = fixture.debugElement.injector.get(ComparisonToolService);

  return {
    component,
    fixture,
    instance,
    service,
    user,
  };
}

describe('ComparisonToolTableComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});
