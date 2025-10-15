import { provideHttpClient } from '@angular/common/http';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { PopoverLinkComponent } from './popover-link.component';

const meta: Meta<PopoverLinkComponent> = {
  component: PopoverLinkComponent,
  title: 'UI/PopoverLinkComponent',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(), ...provideLoadingIconColors()],
    }),
  ],
};
export default meta;
type Story = StoryObj<PopoverLinkComponent>;

export const Default: Story = {
  args: {
    wikiParams: {
      ownerId: 'syn66271427',
      wikiId: '632874',
    },
  },
};
