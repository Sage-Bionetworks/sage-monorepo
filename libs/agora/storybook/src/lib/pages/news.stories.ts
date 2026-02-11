import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import {
  AGORA_LOADING_ICON_COLORS,
  DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
  DEFAULT_WIKI_OWNER_ID,
} from '@sagebionetworks/agora/config';
import { WikiHeroComponent } from '@sagebionetworks/explorers/shared';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';

const meta: Meta<WikiHeroComponent> = {
  component: WikiHeroComponent,
  title: 'Pages/News',
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

export const News: Story = {
  args: {
    wikiParams: {
      ownerId: DEFAULT_WIKI_OWNER_ID,
      wikiId: '611426',
    },
    heroTitle: 'Agora News',
    className: 'news-page-content',
    heroBackgroundImagePath: DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
  },
};
