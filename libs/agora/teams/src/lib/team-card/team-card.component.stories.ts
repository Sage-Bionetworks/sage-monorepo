import type { Meta, StoryObj } from '@storybook/angular';
import { TeamCardComponent } from './team-card.component';

const meta: Meta<TeamCardComponent> = {
  component: TeamCardComponent,
  title: 'Teams/TeamCard',
};
export default meta;
type Story = StoryObj<TeamCardComponent>;

const team = {
  team: 'amp-ad-asu',
  team_full: 'Arizona State University',
  program: 'AMP-AD',
  description:
    "The ASU team, led by Ben Readhead, uses multi-omic profiling, 3D-cerebral organoids and CRISPR-Cas9 approaches to understand the role of pathogens in the etiology and progression of Alzheimer's disease.",
  members: [
    { name: 'Ben Readhead', isprimaryinvestigator: true },
    { name: 'Alice Johnson', isprimaryinvestigator: false },
    { name: 'Carlos Rivera', isprimaryinvestigator: false, url: 'https://sagebionetworks.org' },
    { name: 'Diana Chen', isprimaryinvestigator: false },
  ],
};

export const Default: Story = {
  args: { team, images: {} },
};

export const WithImages: Story = {
  args: {
    team,
    images: {
      'Ben Readhead': 'agora-assets/images/og-social.png',
      'Alice Johnson': 'agora-assets/images/og-social.png',
    },
  },
};

export const NoProgram: Story = {
  args: {
    team: { ...team, program: '' },
    images: {},
  },
};

export const NoMembers: Story = {
  args: {
    team: { ...team, members: [] },
    images: {},
  },
};
