import { signal } from '@angular/core';
import { NO_NOUNS, TABLE_DATA_LOADING_MESSAGE_SHORT } from '@sagebionetworks/explorers/constants';
import { ComparisonToolNoun, ComparisonToolNouns } from '@sagebionetworks/explorers/models';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { render, screen } from '@testing-library/angular';
import { DisplayedResultsComponent } from './displayed-results.component';

const PARENT_NOUN: ComparisonToolNoun = { singular: 'Parent', plural: 'Parents' };
const CHILD_NOUN: ComparisonToolNoun = { singular: 'Child', plural: 'Children' };
const VIEW_NOUNS: ComparisonToolNouns = { viewNoun: PARENT_NOUN, parentNoun: null };
const CHILD_NOUNS: ComparisonToolNouns = { viewNoun: CHILD_NOUN, parentNoun: PARENT_NOUN };

function getMockService(total = 5, pinned = 2, nouns = NO_NOUNS) {
  return {
    unpinnedRowCount: signal(total),
    pinnedRowCount: signal(pinned),
    nouns: signal(nouns),
    isLoadingTableData: signal(false),
  };
}

async function setup(total = 5, pinned = 2, nouns = NO_NOUNS) {
  const mockService = getMockService(total, pinned, nouns);
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
    mockService.unpinnedRowCount.set(4);
    mockService.pinnedRowCount.set(5);
    expect(fixture.componentInstance.displayedResultsCount()).toBe(9);
  });

  it('should show the loading message in place of the count while table data loads', async () => {
    const { fixture, mockService } = await setup(10, 3);
    mockService.isLoadingTableData.set(true);
    fixture.detectChanges();
    expect(screen.getByText(TABLE_DATA_LOADING_MESSAGE_SHORT)).toBeInTheDocument();
    expect(screen.queryByText('13')).not.toBeInTheDocument();
  });

  it('should show the count once table data has loaded', async () => {
    const { fixture, mockService } = await setup(10, 3);
    mockService.isLoadingTableData.set(true);
    fixture.detectChanges();
    mockService.isLoadingTableData.set(false);
    fixture.detectChanges();
    expect(screen.getByText('13')).toBeInTheDocument();
    expect(screen.queryByText(TABLE_DATA_LOADING_MESSAGE_SHORT)).not.toBeInTheDocument();
  });

  it('should show a zero count rather than the loading message', async () => {
    await setup(0, 0);
    expect(screen.getByText('0')).toBeInTheDocument();
    expect(screen.queryByText(TABLE_DATA_LOADING_MESSAGE_SHORT)).not.toBeInTheDocument();
  });

  it('should fall back to results', async () => {
    await setup();
    expect(screen.getByRole('heading', { name: 'Displayed Results' })).toBeInTheDocument();
  });

  it('should use the view noun', async () => {
    await setup(5, 2, VIEW_NOUNS);
    expect(screen.getByRole('heading', { name: 'Displayed Parents' })).toBeInTheDocument();
  });

  it('should use the view noun rather than the parent noun in a child view', async () => {
    await setup(5, 2, CHILD_NOUNS);
    expect(screen.getByRole('heading', { name: 'Displayed Children' })).toBeInTheDocument();
  });
});
