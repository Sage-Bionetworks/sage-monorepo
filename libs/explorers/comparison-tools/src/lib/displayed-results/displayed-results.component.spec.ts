import { render } from '@testing-library/angular';
import { DisplayedResultsComponent } from './displayed-results.component';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { signal } from '@angular/core';

function getMockService(total = 5, pinned = 2) {
  return {
    totalResultsCount: signal(total),
    pinnedResultsCount: signal(pinned),
  };
}

async function setup(total = 5, pinned = 2) {
  const mockService = getMockService(total, pinned);
  const { fixture } = await render(DisplayedResultsComponent, {
    providers: [{ provide: ComparisonToolService, useValue: mockService }],
  });
  const component = fixture.componentInstance;
  return { component, fixture, mockService };
}

describe('DisplayedResultsComponent', () => {
  it('should create the component', async () => {
    const { fixture } = await setup();
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should compute displayedResultsCount correctly', async () => {
    const { fixture } = await setup(10, 3);
    expect(fixture.componentInstance.displayedResultsCount()).toBe(13);
  });

  it('should update displayedResultsCount when service values change', async () => {
    const { fixture, mockService } = await setup(1, 1);
    expect(fixture.componentInstance.displayedResultsCount()).toBe(2);
    mockService.totalResultsCount.set(4);
    mockService.pinnedResultsCount.set(5);
    expect(fixture.componentInstance.displayedResultsCount()).toBe(9);
  });
});
