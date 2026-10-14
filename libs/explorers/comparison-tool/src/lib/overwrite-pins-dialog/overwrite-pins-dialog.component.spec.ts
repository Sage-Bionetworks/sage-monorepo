import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import {
  AppStorageService,
  ComparisonToolService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfig,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { MessageService } from 'primeng/api';
import { OverwritePinsDialogComponent } from './overwrite-pins-dialog.component';
import { OVERWRITE_PINS_DIALOG } from './overwrite-pins-dialog.constants';

const PINNED_VIEW = ['Category A', 'Option B'];
const configs: ComparisonToolConfig[] = [
  { ...mockComparisonToolDataConfig[0], dropdowns: PINNED_VIEW },
];
// Three pins are cached, but only the first has a row on screen
const cachedPinIds = mockComparisonToolData.slice(0, 3).map((row) => row['_id'] as string);
const rowToPin = mockComparisonToolData[3];

async function setup() {
  const user = userEvent.setup();
  const { fixture } = await render(OverwritePinsDialogComponent, {
    providers: [
      provideNoopAnimations(),
      MessageService,
      ...provideComparisonToolService({
        configs,
        pinnedItems: cachedPinIds,
        pinnedData: [mockComparisonToolData[0]],
        unpinnedData: mockComparisonToolData.slice(3),
      }),
    ],
  });
  const service = fixture.debugElement.injector.get(ComparisonToolService);
  const appStorageService = fixture.debugElement.injector.get(AppStorageService);
  // Stubbed so the choice isn't written to localStorage, where it would leak into other tests
  const setHidden = jest
    .spyOn(appStorageService, 'setOverwritePinsConfirmationHidden')
    .mockImplementation(() => undefined);

  const holdPinEdit = () => {
    service.pinItem(rowToPin);
    fixture.detectChanges();
  };

  return { user, service, setHidden, holdPinEdit };
}

describe('OverwritePinsDialogComponent', () => {
  it('should stay closed while no pin edit is held', async () => {
    await setup();

    expect(screen.queryByRole('dialog')).toBeNull();
  });

  it('should name the hidden pins and the view they were pinned in', async () => {
    const { holdPinEdit } = await setup();

    holdPinEdit();

    const dialog = screen.getByRole('dialog');
    expect(dialog).toHaveTextContent(OVERWRITE_PINS_DIALOG.heading);
    expect(dialog).toHaveTextContent("You pinned 2 results that can't be shown in this view.");
    expect(dialog).toHaveTextContent('To view them, go back to the Category A, Option B view.');
    expect(Array.from(dialog.querySelectorAll('i')).map((i) => i.textContent)).toEqual(PINNED_VIEW);
  });

  it('should apply the held pin edit on Overwrite Pins', async () => {
    const { user, service, holdPinEdit } = await setup();
    holdPinEdit();

    await user.click(
      screen.getByRole('button', { name: OVERWRITE_PINS_DIALOG.confirmButtonLabel }),
    );

    expect(service.isOverwriteConfirmationVisible()).toBe(false);
    expect(service.pinnedItems()).toContain(rowToPin['_id']);
  });

  it('should leave the pins as they were on Cancel', async () => {
    const { user, service, holdPinEdit } = await setup();
    holdPinEdit();

    await user.click(screen.getByRole('button', { name: OVERWRITE_PINS_DIALOG.cancelButtonLabel }));

    expect(service.isOverwriteConfirmationVisible()).toBe(false);
    expect(service.pinnedItems()).toEqual(cachedPinIds);
  });

  it('should leave the pins as they were when closed with Escape', async () => {
    const { user, service, holdPinEdit } = await setup();
    holdPinEdit();

    await user.keyboard('{Escape}');

    expect(service.isOverwriteConfirmationVisible()).toBe(false);
    expect(service.pinnedItems()).toEqual(cachedPinIds);
  });

  it('should stop asking once confirmed with the checkbox checked', async () => {
    const { user, setHidden, holdPinEdit } = await setup();
    holdPinEdit();

    await user.click(screen.getByLabelText(OVERWRITE_PINS_DIALOG.hideCheckboxLabel));
    await user.click(
      screen.getByRole('button', { name: OVERWRITE_PINS_DIALOG.confirmButtonLabel }),
    );

    expect(setHidden).toHaveBeenCalledWith(true);
  });

  it('should keep asking when cancelled with the checkbox checked', async () => {
    const { user, setHidden, holdPinEdit } = await setup();
    holdPinEdit();

    await user.click(screen.getByLabelText(OVERWRITE_PINS_DIALOG.hideCheckboxLabel));
    await user.click(screen.getByRole('button', { name: OVERWRITE_PINS_DIALOG.cancelButtonLabel }));

    expect(setHidden).not.toHaveBeenCalled();
  });
});
