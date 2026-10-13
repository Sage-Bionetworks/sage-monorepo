import { inject, provideAppInitializer } from '@angular/core';
import { ComparisonToolConfig, ComparisonToolNoun } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfigWithDropdowns,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { MessageService } from 'primeng/api';
import { OverwritePinsDialogComponent } from './overwrite-pins-dialog.component';

const PARENT_NOUN: ComparisonToolNoun = { singular: 'Parent', plural: 'Parents' };
const CHILD_NOUN: ComparisonToolNoun = { singular: 'Child', plural: 'Children' };
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

const meta: Meta<OverwritePinsDialogComponent> = {
  component: OverwritePinsDialogComponent,
  title: 'Comparison Tool/OverwritePinsDialogComponent',
};
export default meta;
type Story = StoryObj<OverwritePinsDialogComponent>;

export const Standard: Story = {
  decorators: [applicationConfig({ providers: holdPinEdit([pinnedViewConfig]) })],
};

export const ParentChild: Story = {
  decorators: [
    applicationConfig({
      providers: holdPinEdit([
        {
          ...pinnedViewConfig,
          row_id_data_key: '_id',
          parent_id_data_key: 'model_type',
          view_noun: CHILD_NOUN,
          parent_noun: PARENT_NOUN,
        },
      ]),
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
