import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { TooltipButtonComponent } from './tooltip-button.component';

const meta: Meta<TooltipButtonComponent> = {
  component: TooltipButtonComponent,
  title: 'UI/TooltipButtonComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
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
    iconImagePath: 'explorers-assets/icons/link.svg',
    iconAltText: 'Icon button',
    iconColor: 'blue',
    iconHeight: 12,
    iconWidth: 24,
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
    iconImagePath: 'explorers-assets/icons/info-circle.svg',
    iconAltText: 'Icon button',
    tooltipText: 'This is a text icon button',
    onClick: mockOnClick,
  },
};
