import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AGORA_LOADING_ICON_COLORS, DEFAULT_WIKI_OWNER_ID } from '@sagebionetworks/agora/config';
import { WikiHeroComponent } from '@sagebionetworks/explorers/shared';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';

const meta: Meta<WikiHeroComponent> = {
  component: WikiHeroComponent,
  title: 'Pages/About',
  decorators: [
    applicationConfig({
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        ...provideLoadingIconColors(AGORA_LOADING_ICON_COLORS),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<WikiHeroComponent>;

export const Primary: Story = {
  args: {
    wikiParams: {
      ownerId: DEFAULT_WIKI_OWNER_ID,
      wikiId: '612058',
    },
    heroTitle: 'About',
  },
};
