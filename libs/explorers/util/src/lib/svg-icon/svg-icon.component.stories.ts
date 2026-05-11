import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { SvgIconComponent } from './svg-icon.component';

const meta: Meta<SvgIconComponent> = {
  component: SvgIconComponent,
  title: 'UI/SvgIconComponent',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
};
export default meta;
type Story = StoryObj<SvgIconComponent>;

const peopleIcon = 'explorers-assets/icons/people.svg';

export const Default: Story = {
  args: {
    imagePath: peopleIcon,
    altText: 'people',
    width: 20,
    height: 20,
    color: 'var(--color-primary)',
  },
};

export const CustomColor: Story = {
  args: {
    imagePath: peopleIcon,
    altText: 'people',
    width: 24,
    height: 24,
    color: '#ff33c2',
  },
};

export const BackgroundDefault: Story = {
  args: {
    imagePath: peopleIcon,
    altText: 'people',
    width: 24,
    height: 24,
    color: 'white',
    enableBackground: true,
  },
};

export const BackgroundSquare: Story = {
  args: {
    imagePath: peopleIcon,
    altText: 'people',
    width: 24,
    height: 24,
    color: 'white',
    enableBackground: true,
    backgroundColor: 'var(--color-action-primary)',
    backgroundShape: 'square',
  },
};

export const BackgroundLargerPadding: Story = {
  args: {
    imagePath: peopleIcon,
    altText: 'people',
    width: 24,
    height: 24,
    color: 'white',
    enableBackground: true,
    backgroundColor: 'var(--color-tertiary)',
    backgroundShape: 'circle',
    backgroundPadding: 16,
  },
};
