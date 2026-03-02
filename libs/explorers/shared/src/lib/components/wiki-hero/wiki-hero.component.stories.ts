import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { WikiHeroComponent } from './wiki-hero.component';

const meta: Meta<WikiHeroComponent> = {
  component: WikiHeroComponent,
  title: 'Pages/WikiHero',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideLoadingIconColors(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<WikiHeroComponent>;

/** Model-AD About page */
export const ModelADAbout: Story = {
  args: {
    heroTitle: 'About',
    wikiParams: {
      wikiId: '631750',
      ownerId: 'syn66271427',
    } as SynapseWikiParams,
    heroBackgroundImagePath: '',
  },
};

/** Model-AD News page */
export const ModelADNews: Story = {
  args: {
    heroTitle: 'News',
    wikiParams: {
      wikiId: '631751',
      ownerId: 'syn66271427',
    } as SynapseWikiParams,
    heroBackgroundImagePath: '',
  },
};

/** Agora About page */
export const AgoraAbout: Story = {
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
export const AgoraNews: Story = {
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
