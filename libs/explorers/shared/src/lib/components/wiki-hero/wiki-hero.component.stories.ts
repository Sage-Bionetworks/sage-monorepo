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

export const About: Story = {
  args: {
    heroTitle: 'About',
    wikiParams: {
      wikiId: '631750',
      ownerId: 'syn66271427',
    } as SynapseWikiParams,
    heroBackgroundImagePath: '',
  },
};

export const News: Story = {
  args: {
    heroTitle: 'News',
    wikiParams: {
      wikiId: '631751',
      ownerId: 'syn66271427',
    } as SynapseWikiParams,
    heroBackgroundImagePath: '',
  },
};
