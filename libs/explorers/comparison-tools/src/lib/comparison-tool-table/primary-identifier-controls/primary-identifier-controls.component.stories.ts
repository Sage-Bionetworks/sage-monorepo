import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { PrimaryIdentifierControlsComponent } from './primary-identifier-controls.component';

const meta: Meta<PrimaryIdentifierControlsComponent> = {
  component: PrimaryIdentifierControlsComponent,
  title: 'Comparison Tools/ComparisonToolTable/PrimaryIdentifierControlsComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
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
  },
};

export const Pinned: Story = {
  args: {
    label: '3xTg-AD',
    id: '68fff1aaeb12b9674515fd58',
  },
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({
          pinnedItems: ['68fff1aaeb12b9674515fd58', '68fff1aaeb12b9674515fd59'],
          viewConfig: {
            viewDetailsClick: (id: string, label: string) => {
              console.log(`id: ${id}, label: ${label}`);
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
  },
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
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
