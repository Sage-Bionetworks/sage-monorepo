import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import {
  AppStorageService,
  ComparisonToolService,
  ComparisonToolServiceOptions,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockChildViewSelection,
  mockComparisonToolData,
  mockComparisonToolDataConfig,
  mockOtherChildViewSelection,
  mockParentChildComparisonToolConfigs,
  mockParentViewSelection,
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

async function setup({
  pinnedFetchFails = false,
  serviceOptions,
  pinnedRow = rowToPin,
}: {
  pinnedFetchFails?: boolean;
  // Replaces the default pins and configs above
  serviceOptions?: ComparisonToolServiceOptions;
  pinnedRow?: Record<string, unknown>;
} = {}) {
  const user = userEvent.setup();
  const { fixture } = await render(OverwritePinsDialogComponent, {
    providers: [
      provideNoopAnimations(),
      MessageService,
      ...provideComparisonToolService(
        serviceOptions ?? {
          configs,
          pinnedItems: cachedPinIds,
          ...(pinnedFetchFails
            ? { pinnedFetchFails }
            : { pinnedData: [mockComparisonToolData[0]] }),
          unpinnedData: mockComparisonToolData.slice(3),
        },
      ),
    ],
  });
  const service = fixture.debugElement.injector.get(ComparisonToolService);
  const appStorageService = fixture.debugElement.injector.get(AppStorageService);
  // Stubbed so the choice isn't written to localStorage, where it would leak into other tests
  const setHidden = jest
    .spyOn(appStorageService, 'setOverwritePinsConfirmationHidden')
    .mockImplementation(() => undefined);

  const holdPinEdit = () => {
    service.pinItem(pinnedRow);
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

  it('should say the pins could not be loaded, without a view, when they failed in this view', async () => {
    const { holdPinEdit } = await setup({ pinnedFetchFails: true });

    holdPinEdit();

    const dialog = screen.getByRole('dialog');
    expect(dialog).toHaveTextContent(
      "You pinned 3 results that couldn't be loaded. Changing your pins here will unpin those results.",
    );
    expect(dialog).not.toHaveTextContent('go back to');
    expect(dialog.querySelectorAll('i')).toHaveLength(0);
  });

  // The pins span two parents, the way protein pins span two genes: rows 0-3 share the parent
  // 'Familial AD', and row 4 has the parent 'Late Onset AD'
  describe('by where the pins were made', () => {
    const [familialChild, otherFamilialChild, unpinnedFamilialChild, , lateOnsetChild] =
      mockComparisonToolData;
    const childPins = [familialChild, otherFamilialChild, lateOnsetChild];
    const childPinIds = childPins.map((row) => row['_id'] as string);
    const parentPinIds = ['Familial AD', 'Late Onset AD'];
    const holdPinEditIn = async (
      serviceOptions: ComparisonToolServiceOptions,
      pinnedRow = unpinnedFamilialChild,
    ) => {
      const { holdPinEdit } = await setup({
        serviceOptions: { configs: mockParentChildComparisonToolConfigs, ...serviceOptions },
        pinnedRow,
      });
      holdPinEdit();
      return screen.getByRole('dialog');
    };

    it('should count the hidden results per parent in a parent view showing pins made in a child view', async () => {
      // Both parents of the mock rows are pinned, so pinning in the parent view needs a third
      const dialog = await holdPinEditIn(
        {
          selection: mockParentViewSelection,
          pinnedItems: childPinIds,
          pinnedIn: { selection: mockChildViewSelection, data: childPins },
          pinnedData: [familialChild],
        },
        { ...unpinnedFamilialChild, model_type: 'Early Onset AD' },
      );

      expect(dialog).toHaveTextContent(
        "You pinned 1 result for 1 parent that can't be shown in this view.",
      );
    });

    it('should count the hidden results per parent in a child view showing pins made in a parent view', async () => {
      const dialog = await holdPinEditIn({
        selection: mockChildViewSelection,
        pinnedItems: parentPinIds,
        pinnedIn: { selection: mockParentViewSelection },
        pinnedData: [familialChild, otherFamilialChild],
      });

      expect(dialog).toHaveTextContent(
        "You pinned 1 result for 1 parent that can't be shown in this view.",
      );
    });

    it('should name the hidden rows in a child view showing pins made under another selection with the same keys', async () => {
      const dialog = await holdPinEditIn({
        selection: mockChildViewSelection,
        pinnedItems: childPinIds,
        pinnedIn: { selection: mockOtherChildViewSelection, data: childPins },
        pinnedData: [familialChild],
      });

      expect(dialog).toHaveTextContent(
        "You pinned 2 children that can't be shown in this view. Changing your pins here will unpin those children.",
      );
      expect(Array.from(dialog.querySelectorAll('i')).map((i) => i.textContent)).toEqual(
        mockOtherChildViewSelection,
      );
    });

    it('should name the unloaded rows after a failed fetch of pins made under another selection with the same keys', async () => {
      const dialog = await holdPinEditIn({
        selection: mockChildViewSelection,
        pinnedItems: childPinIds,
        pinnedIn: { selection: mockOtherChildViewSelection, data: childPins },
        pinnedFetchFails: true,
      });

      expect(dialog).toHaveTextContent(
        "You pinned 3 children that couldn't be loaded. Changing your pins here will unpin those children.",
      );
    });

    it('should count the unloaded results per parent after a failed fetch of pins made in a parent view', async () => {
      const dialog = await holdPinEditIn({
        selection: mockChildViewSelection,
        pinnedItems: parentPinIds,
        pinnedIn: { selection: mockParentViewSelection },
        pinnedFetchFails: true,
      });

      expect(dialog).toHaveTextContent(
        "You pinned 2 results for 2 parents that couldn't be loaded. Changing your pins here will unpin the results for those parents.",
      );
    });
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
