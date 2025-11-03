import { provideHttpClient } from '@angular/common/http';
import { provideRouter, RouterModule } from '@angular/router';
import {
  ComparisonToolService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { PrimaryIdentifierControlsComponent } from './primary-identifier-controls.component';

async function setup(options?: { pinnedItems?: string[]; maxPinnedItems?: number }) {
  const user = userEvent.setup();
  const viewDetailsClickSpy = jest.fn();

  const component = await render(PrimaryIdentifierControlsComponent, {
    imports: [RouterModule],
    providers: [
      provideHttpClient(),
      provideRouter([]),
      ...provideComparisonToolService({
        pinnedItems: options?.pinnedItems,
        maxPinnedItems: options?.maxPinnedItems,
        viewConfig: { viewDetailsClick: viewDetailsClickSpy },
      }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
    componentInputs: {
      label: '3xTg-AD',
      id: '68fff1aaeb12b9674515fd58',
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
    expect(viewDetailsClickSpy).toHaveBeenCalledWith('68fff1aaeb12b9674515fd58', '3xTg-AD');
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
    expect(service.pinnedItems().size).toBe(2);
  });

  it('should disable pin button when max pinned items is reached and not currently pinned', async () => {
    const { pinButton } = await setup({
      maxPinnedItems: 2,
      pinnedItems: ['68fff1aaeb12b9674515fd5a', '68fff1aaeb12b9674515fd59'],
    });
    expect(pinButton).toBeDisabled();
  });

  it('should allow unpinning when max pinned items is reached and item is pinned', async () => {
    const { pinButton, user, service } = await setup({
      maxPinnedItems: 2,
      pinnedItems: ['68fff1aaeb12b9674515fd58', '68fff1aaeb12b9674515fd59'],
    });
    expect(pinButton).not.toBeDisabled();
    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(true);

    await user.click(pinButton);

    expect(service.isPinned('68fff1aaeb12b9674515fd58')).toBe(false);
    expect(service.pinnedItems().size).toBe(1);
  });

  it('should update pinnedItems when items are pinned', async () => {
    const { user, pinButton, service } = await setup();
    expect(service.pinnedItems().size).toBe(0);

    await user.click(pinButton);
    expect(service.pinnedItems().size).toBe(1);
  });

  it('should update pinnedItems when items are unpinned', async () => {
    const { user, pinButton, service } = await setup({ pinnedItems: ['68fff1aaeb12b9674515fd58'] });
    expect(service.pinnedItems().size).toBe(1);

    await user.click(pinButton);
    expect(service.pinnedItems().size).toBe(0);
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

  it('should display correct tooltip when max pinned items reached and not currently pinned', async () => {
    const { pinButton, user } = await setup({
      maxPinnedItems: 2,
      pinnedItems: ['68fff1aaeb12b9674515fd5a', '68fff1aaeb12b9674515fd59'],
    });
    await user.hover(pinButton);
    const tooltip = screen.getByRole('tooltip');
    expect(tooltip).toHaveTextContent(
      'You have already pinned the maximum number of items (2). You must unpin some items before you can pin more.',
    );
  });
});
