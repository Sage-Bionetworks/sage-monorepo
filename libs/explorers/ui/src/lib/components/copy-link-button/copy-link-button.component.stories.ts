import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { CopyLinkButtonComponent } from './copy-link-button.component';

const meta: Meta<CopyLinkButtonComponent> = {
  component: CopyLinkButtonComponent,
  title: 'UI/CopyLinkButton',
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
type Story = StoryObj<CopyLinkButtonComponent>;

const mockOnClick = (): void => {
  console.log('link copied!');
};

export const CopyLinkButton: Story = {
  args: {
    onClick: mockOnClick,
    anchorId: 'evidence-type-anchor',
    ariaLabel: 'Copy link to RNA - Differential Expression',
    tooltipText: 'Copy link',
  },
};
