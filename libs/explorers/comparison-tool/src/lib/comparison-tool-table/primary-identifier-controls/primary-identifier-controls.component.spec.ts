import { provideHttpClient } from '@angular/common/http';
import { provideRouter, RouterModule } from '@angular/router';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolDataConfig,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { MessageService } from 'primeng/api';
import { PrimaryIdentifierControlsComponent } from './primary-identifier-controls.component';

const MODEL_ROW = { _id: '68fff1aaeb12b9674515fd58', name: '3xTg-AD' };

async function setup(options?: {
  configs?: ComparisonToolConfig[];
  pinnedItems?: string[];
  pinnedData?: Record<string, unknown>[];
  pinLimit?: number;
  rowData?: Record<string, unknown>;
}) {
  const user = userEvent.setup();
  const viewDetailsClickSpy = jest.fn();

  const component = await render(PrimaryIdentifierControlsComponent, {
    imports: [RouterModule],
    providers: [
      provideHttpClient(),
      provideRouter([]),
      MessageService,
      ...provideComparisonToolService({
        configs: options?.configs,
        pinnedItems: options?.pinnedItems,
        pinnedData: options?.pinnedData,
        pinLimit: options?.pinLimit,
        viewConfig: { viewDetailsClick: viewDetailsClickSpy },
      }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
    componentInputs: {
      label: '3xTg-AD',
      rowData: options?.rowData ?? MODEL_ROW,
    },
  });

  const fixture = component.fixture;
  const instance = fixture.componentInstance;
  const service = fixture.debugElement.injector.get(ComparisonToolService);
  const pinButton = screen.getByRole('button', { name: /pin/i });
  const viewDetailsButton = screen.getByRole('button', { name: /view details/i });

  return {
    component,
    fixture,
    instance,
    service,
    user,
    viewDetailsClickSpy,
    pinButton,
    viewDetailsButton,
  };
}

describe('PrimaryIdentifierControlsComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should display the label', async () => {
    await setup();
    expect(screen.getByText('3xTg-AD')).toBeInTheDocument();
  });

  it('should call viewDetailsClick when view details button is clicked', async () => {
    const { user, viewDetailsButton, viewDetailsClickSpy } = await setup();
    await user.click(viewDetailsButton);
    expect(viewDetailsClickSpy).toHaveBeenCalledWith(MODEL_ROW);
  });

  it('should toggle pin state when pin button is clicked', async () => {
    const { user, pinButton, service } = await setup();
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(false);

    await user.click(pinButton);
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(true);

    await user.click(pinButton);
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(false);
  });

  it('should show item as pinned when initialized with pinnedItems', async () => {
    const { service } = await setup({
      pinnedItems: ['68fff1aaeb12b9674515fd58', '68fff1aaeb12b9674515fd59'],
    });
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(true);
    expect(service.pinnedItemsSet().size).toBe(2);
  });

  it('should disable pin button when the pin limit is reached and not currently pinned', async () => {
    const { pinButton } = await setup({
      pinLimit: 2,
      pinnedItems: ['68fff1aaeb12b9674515fd5a', '68fff1aaeb12b9674515fd59'],
    });
    expect(pinButton).toBeDisabled();
  });

  it('should allow unpinning when the pin limit is reached and item is pinned', async () => {
    const { pinButton, user, service } = await setup({
      pinLimit: 2,
      pinnedItems: ['68fff1aaeb12b9674515fd58', '68fff1aaeb12b9674515fd59'],
    });
    expect(pinButton).not.toBeDisabled();
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(true);

    await user.click(pinButton);

    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(false);
    expect(service.pinnedItemsSet().size).toBe(1);
  });

  it('should update pinnedItemsSet when items are pinned', async () => {
    const { user, pinButton, service } = await setup();
    expect(service.pinnedItemsSet().size).toBe(0);

    await user.click(pinButton);
    expect(service.pinnedItemsSet().size).toBe(1);
  });

  it('should update pinnedItemsSet when items are unpinned', async () => {
    const { user, pinButton, service } = await setup({ pinnedItems: ['68fff1aaeb12b9674515fd58'] });
    expect(service.pinnedItemsSet().size).toBe(1);

    await user.click(pinButton);
    expect(service.pinnedItemsSet().size).toBe(0);
  });

  it('should display correct tooltip for pin button when not at max', async () => {
    const { pinButton, user } = await setup();
    await user.hover(pinButton);
    const tooltip = screen.getByRole('tooltip');
    expect(tooltip).toHaveTextContent('Pin this row to the top of the list');
  });

  it('should display correct tooltip for unpin button', async () => {
    const { pinButton, user } = await setup({ pinnedItems: ['68fff1aaeb12b9674515fd58'] });
    await user.hover(pinButton);
    const tooltip = screen.getByRole('tooltip');
    expect(tooltip).toHaveTextContent('Unpin this row');
  });

  it('should display correct tooltip when the pin limit is reached and not currently pinned', async () => {
    const { pinButton, user } = await setup({
      pinLimit: 2,
      pinnedItems: ['68fff1aaeb12b9674515fd5a', '68fff1aaeb12b9674515fd59'],
    });
    await user.hover(pinButton);
    const tooltip = screen.getByRole('tooltip');
    expect(tooltip).toHaveTextContent(
      'You have already pinned the maximum number of items (2). You must unpin some items before you can pin more.',
    );
  });

  it('should call viewDetailsClick when activated with the keyboard', async () => {
    const { user, viewDetailsButton, viewDetailsClickSpy } = await setup();

    await user.tab();
    expect(viewDetailsButton).toHaveFocus();

    await user.keyboard('[Enter]');
    expect(viewDetailsClickSpy).toHaveBeenCalledWith(MODEL_ROW);
  });

  it('should toggle pin state when activated with the keyboard', async () => {
    const { user, pinButton, service } = await setup();

    await user.tab();
    await user.tab();
    expect(pinButton).toHaveFocus();
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(false);

    await user.keyboard('[Space]');
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(true);

    await user.keyboard('[Space]');
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(false);
  });

  describe('in a view with a parent key', () => {
    const PARENT_ID_DATA_KEY = 'parent_id';
    const childViewConfig: ComparisonToolConfig = {
      ...mockComparisonToolDataConfig[0],
      row_id_data_key: '_id',
      parent_id_data_key: PARENT_ID_DATA_KEY,
    };
    const childRow = (id: string, parentId: string) => ({
      _id: id,
      [PARENT_ID_DATA_KEY]: parentId,
    });

    const setupAtPinLimit = (rowData: Record<string, unknown>) =>
      setup({
        configs: [childViewConfig],
        pinLimit: 1,
        pinnedItems: ['child1a'],
        pinnedData: [childRow('child1a', 'parent1')],
        rowData,
      });

    it('should enable the pin button at the pin limit for a child of a pinned parent', async () => {
      const { pinButton, service } = await setupAtPinLimit(childRow('child1b', 'parent1'));
      expect(service.hasReachedPinLimit()).toBe(true);
      expect(pinButton).not.toBeDisabled();
    });

    it('should disable the pin button at the pin limit for a child of a new parent', async () => {
      const { pinButton, service } = await setupAtPinLimit(childRow('child2a', 'parent2'));
      expect(service.hasReachedPinLimit()).toBe(true);
      expect(pinButton).toBeDisabled();
    });
  });
});
