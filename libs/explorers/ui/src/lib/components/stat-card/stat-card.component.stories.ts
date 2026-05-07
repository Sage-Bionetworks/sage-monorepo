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

const defaultIcon = 'explorers-assets/images/warning-circle.svg';

export const Default: Story = {
  args: {
    iconPath: defaultIcon,
    iconAltText: 'icon',
    header: 'Total QTLs',
  },
};

export const WithSubHeader: Story = {
  args: {
    iconPath: defaultIcon,
    iconAltText: 'icon',
    header: 'Total QTLs',
    subHeader: 'across 53 tissues',
  },
};

export const LongHeader: Story = {
  args: {
    iconPath: defaultIcon,
    iconAltText: 'icon',
    header: 'Significant variant–gene associations',
    subHeader: 'across all tissues',
  },
};
