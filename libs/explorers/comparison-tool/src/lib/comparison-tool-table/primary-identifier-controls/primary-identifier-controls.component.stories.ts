import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { PrimaryIdentifierControlsComponent } from './primary-identifier-controls.component';

const meta: Meta<PrimaryIdentifierControlsComponent> = {
  component: PrimaryIdentifierControlsComponent,
  title: 'Comparison Tool/ComparisonToolTable/PrimaryIdentifierControlsComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({
          viewConfig: { viewDetailsTooltip: 'View detailed results' },
        }),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<PrimaryIdentifierControlsComponent>;

export const Unpinned: Story = {
  args: {
    label: '3xTg-AD',
    id: '68fff1aaeb12b9674515fd58',
    rowData: { _id: '68fff1aaeb12b9674515fd58', name: '3xTg-AD' },
  },
};

export const Pinned: Story = {
  args: {
    label: '3xTg-AD',
    id: '68fff1aaeb12b9674515fd58',
    rowData: { _id: '68fff1aaeb12b9674515fd58', name: '3xTg-AD' },
  },
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({
          pinnedItems: ['68fff1aaeb12b9674515fd58', '68fff1aaeb12b9674515fd59'],
          viewConfig: {
            viewDetailsClick: (rowData: unknown) => {
              console.log(`rowData: ${JSON.stringify(rowData)}`);
            },
          },
        }),
      ],
    }),
  ],
};

export const MaxPinnedReached: Story = {
  args: {
    label: '3xTg-AD',
    id: '68fff1aaeb12b9674515fd58',
    rowData: { _id: '68fff1aaeb12b9674515fd58', name: '3xTg-AD' },
  },
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({
          maxPinnedItems: 3,
          pinnedItems: [
            '68fff1aaeb12b9674515fd59',
            '68fff1aaeb12b9674515fd5a',
            '68fff1aaeb12b9674515fd61',
          ],
        }),
      ],
    }),
  ],
};
