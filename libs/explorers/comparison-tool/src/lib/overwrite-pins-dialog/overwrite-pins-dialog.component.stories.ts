import { inject, provideAppInitializer } from '@angular/core';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
  ComparisonToolServiceOptions,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockChildViewSelection,
  mockComparisonToolData,
  mockComparisonToolDataConfigWithDropdowns,
  mockOtherChildViewSelection,
  mockParentChildComparisonToolConfigs,
  mockParentViewSelection,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { MessageService } from 'primeng/api';
import { OverwritePinsDialogComponent } from './overwrite-pins-dialog.component';

// A view with dropdowns, so the message names the view the pins were made in
const pinnedViewConfig = mockComparisonToolDataConfigWithDropdowns[0];

// Three pins are cached, but only the first has a row on screen, so pinning another row is held
// for the confirmation. With `pinnedFetchFails`, none load, so all three are hidden in their own view
const holdPinEdit = (configs: ComparisonToolConfig[], { pinnedFetchFails = false } = {}) => [
  MessageService,
  ...provideComparisonToolService({
    configs,
    pinnedItems: mockComparisonToolData.slice(0, 3).map((row) => row['_id'] as string),
    ...(pinnedFetchFails
      ? { pinnedFetchFails }
      : { pinnedData: mockComparisonToolData.slice(0, 1) }),
    unpinnedData: mockComparisonToolData.slice(3),
  }),
  provideAppInitializer(() => {
    inject(ComparisonToolService).pinItem(mockComparisonToolData[3]);
  }),
];

const [familialChild, otherFamilialChild, unpinnedFamilialChild, , lateOnsetChild] =
  mockComparisonToolData;
const childPins = [familialChild, otherFamilialChild, lateOnsetChild];

const holdPinEditInChildView = (options: ComparisonToolServiceOptions) => [
  MessageService,
  ...provideComparisonToolService({
    configs: mockParentChildComparisonToolConfigs,
    selection: mockChildViewSelection,
    unpinnedData: [unpinnedFamilialChild],
    ...options,
  }),
  provideAppInitializer(() => {
    inject(ComparisonToolService).pinItem(unpinnedFamilialChild);
  }),
];

const meta: Meta<OverwritePinsDialogComponent> = {
  component: OverwritePinsDialogComponent,
  title: 'Comparison Tool/OverwritePinsDialogComponent',
};
export default meta;
type Story = StoryObj<OverwritePinsDialogComponent>;

export const Standard: Story = {
  decorators: [applicationConfig({ providers: holdPinEdit([pinnedViewConfig]) })],
};

export const HiddenParentsChildView: Story = {
  decorators: [
    applicationConfig({
      providers: holdPinEditInChildView({
        pinnedItems: ['Familial AD', 'Late Onset AD'],
        pinnedIn: { selection: mockParentViewSelection },
        pinnedData: [familialChild, otherFamilialChild],
      }),
    }),
  ],
};

export const HiddenChildrenChildView: Story = {
  decorators: [
    applicationConfig({
      providers: holdPinEditInChildView({
        pinnedItems: childPins.map((row) => row['_id'] as string),
        pinnedIn: { selection: mockOtherChildViewSelection, data: childPins },
        pinnedData: [familialChild],
      }),
    }),
  ],
};

export const FailedToLoad: Story = {
  decorators: [
    applicationConfig({
      providers: holdPinEdit([pinnedViewConfig], { pinnedFetchFails: true }),
    }),
  ],
};
