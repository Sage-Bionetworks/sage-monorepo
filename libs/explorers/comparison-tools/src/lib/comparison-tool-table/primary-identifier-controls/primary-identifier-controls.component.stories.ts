import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { PrimaryIdentifierControlsComponent } from './primary-identifier-controls.component';

const meta: Meta<PrimaryIdentifierControlsComponent> = {
  component: PrimaryIdentifierControlsComponent,
  title: 'Comparison Tools/PrimaryIdentifierControlsComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<PrimaryIdentifierControlsComponent>;

export const Unpinned: Story = {
  args: {
    id: '3xTg-AD',
    viewDetailsTooltip: 'Open model details page',
  },
};

export const Pinned: Story = {
  args: {
    id: '3xTg-AD',
    viewDetailsTooltip: 'Open model details page',
  },
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({ pinnedItems: ['3xTg-AD', 'item2'] }),
      ],
    }),
  ],
  argTypes: {
    viewDetailsEvent: { control: false },
  },
};

export const MaxPinnedReached: Story = {
  args: {
    id: '3xTg-AD',
    viewDetailsTooltip: 'Open model details page',
  },
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({
          maxPinnedItems: 3,
          pinnedItems: ['item1', 'item2', 'item3'],
        }),
      ],
    }),
  ],
};
