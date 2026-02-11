import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH, SUPPORT_EMAIL } from '@sagebionetworks/agora/config';
import { NotFoundComponent } from '@sagebionetworks/explorers/shared';
import { type Meta, type StoryObj } from '@storybook/angular';

const meta: Meta<NotFoundComponent> = {
  component: NotFoundComponent,
  title: 'Pages/Not Found',
};
export default meta;
type Story = StoryObj<NotFoundComponent>;

export const NotFound: Story = {
  args: {
    supportEmail: SUPPORT_EMAIL,
    heroBackgroundImagePath: DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
  },
};
