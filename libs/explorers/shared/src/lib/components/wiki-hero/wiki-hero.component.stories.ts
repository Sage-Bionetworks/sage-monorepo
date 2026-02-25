import { provideHttpClient } from '@angular/common/http';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { WikiHeroComponent } from './wiki-hero.component';

const meta: Meta<WikiHeroComponent> = {
  component: WikiHeroComponent,
  title: 'Pages/WikiHero',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(), ...provideLoadingIconColors()],
    }),
  ],
};
export default meta;
type Story = StoryObj<WikiHeroComponent>;

/** Agora About page */
export const About: Story = {
  args: {
    wikiParams: {
      ownerId: 'syn25913473',
      wikiId: '612058',
    },
    heroTitle: 'About',
    heroBackgroundImagePath: 'agora-assets/images/hero-background.svg',
    className: '',
  },
};

/** Agora News page */
export const News: Story = {
  args: {
    wikiParams: {
      ownerId: 'syn25913473',
      wikiId: '611426',
    },
    heroTitle: 'Agora News',
    heroBackgroundImagePath: 'agora-assets/images/hero-background.svg',
    className: '',
  },
};
