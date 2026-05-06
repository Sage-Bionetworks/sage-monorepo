import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { StatCardComponent } from './stat-card.component';

const meta: Meta<StatCardComponent> = {
  component: StatCardComponent,
  title: 'UI/Cards/StatCardComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<StatCardComponent>;

export const Default: Story = {
  args: {
    value: '1,234',
    label: 'Total QTLs',
  },
};

export const WithIcon: Story = {
  args: {
    value: '42',
    label: 'Studies analyzed',
    iconPath: 'explorers-assets/images/warning-circle.svg',
    iconAltText: 'studies',
  },
};

export const LongLabel: Story = {
  args: {
    value: '8.6M',
    label: 'Significant variant–gene associations across all tissues',
  },
};
