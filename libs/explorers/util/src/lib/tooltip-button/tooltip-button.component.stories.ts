import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { TooltipButtonComponent } from './tooltip-button.component';

const meta: Meta<TooltipButtonComponent> = {
  component: TooltipButtonComponent,
  title: 'UI/TooltipButtonComponent',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
  argTypes: {
    onClick: { control: false },
  },
};
export default meta;
type Story = StoryObj<TooltipButtonComponent>;

const mockOnClick = (): void => {
  console.log('clicked!');
};

export const IconButton: Story = {
  args: {
    buttonSvgIconConfig: {
      imagePath: '/explorers-assets/icons/link.svg',
      altText: 'Icon button',
      color: 'blue',
      height: 12,
      width: 24,
    },
    buttonProps: { ariaLabel: 'Icon button', link: true },
    tooltipText: 'This is an icon button',
    onClick: mockOnClick,
  },
};

export const TextButton: Story = {
  args: {
    buttonLabel: 'Text Button',
    tooltipText: 'This is a text button',
    onClick: mockOnClick,
  },
};

export const TextIconButton: Story = {
  args: {
    buttonLabel: 'Text Icon Button',
    buttonSvgIconConfig: {
      imagePath: '/explorers-assets/icons/info-circle.svg',
      altText: 'Icon button',
    },
    tooltipText: 'This is a text icon button',
    onClick: mockOnClick,
  },
};
