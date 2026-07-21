import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ToggleCardComponent, ToggleCardOption } from './toggle-card.component';

const options: ToggleCardOption[] = [
  {
    label: 'Mouse Models',
    value: 'mouse',
    imagePath: 'explorers-assets/images/gene-comparison-icon.svg',
    imageAltText: 'mouse models icon',
  },
  {
    label: 'Marmoset Models',
    value: 'marmoset',
    imagePath: 'explorers-assets/images/gene-search-icon.svg',
    imageAltText: 'marmoset models icon',
  },
];

const meta: Meta<ToggleCardComponent> = {
  component: ToggleCardComponent,
  title: 'UI/ToggleCardComponent',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
  args: {
    options,
  },
};
export default meta;
type Story = StoryObj<ToggleCardComponent>;

export const Demo: Story = {
  args: {
    value: 'mouse',
  },
};

export const ManyOptions: Story = {
  args: {
    options: [
      { label: 'Mouse', value: 'mouse' },
      { label: 'Marmoset', value: 'marmoset' },
      { label: 'Rat', value: 'rat' },
      { label: 'Zebrafish', value: 'zebrafish' },
    ],
    value: 'mouse',
  },
};
