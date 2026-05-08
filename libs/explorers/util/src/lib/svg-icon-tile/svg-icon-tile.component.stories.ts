import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { SvgIconTileComponent } from './svg-icon-tile.component';

const meta: Meta<SvgIconTileComponent> = {
  component: SvgIconTileComponent,
  title: 'UI/SvgIconTileComponent',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
};
export default meta;
type Story = StoryObj<SvgIconTileComponent>;

const peopleIcon = 'explorers-assets/icons/people.svg';

export const Circle: Story = {
  args: {
    imagePath: peopleIcon,
    altText: 'people',
    width: 24,
    height: 24,
    color: 'white',
    backgroundColor: 'var(--color-primary)',
    shape: 'circle',
  },
};

export const Square: Story = {
  args: {
    imagePath: peopleIcon,
    altText: 'people',
    width: 24,
    height: 24,
    color: 'white',
    backgroundColor: 'var(--color-action-primary)',
    shape: 'square',
  },
};

export const LargerPadding: Story = {
  args: {
    imagePath: peopleIcon,
    altText: 'people',
    width: 24,
    height: 24,
    color: 'white',
    backgroundColor: 'var(--color-tertiary)',
    shape: 'circle',
    padding: 16,
  },
};
