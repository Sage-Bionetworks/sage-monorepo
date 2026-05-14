import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ChicletComponent } from './chiclet.component';

interface ChicletStoryArgs {
  content: string;
  backgroundColor?: string;
  textColor?: string;
  closeIconColor?: string;
  closeIconSize?: number;
  fontSize?: string;
  borderRadius?: string;
  padding?: string;
  removable?: boolean;
  removeAriaLabel?: string;
}

const meta: Meta<ChicletStoryArgs> = {
  component: ChicletComponent,
  title: 'UI/Chiclets/ChicletComponent',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
  render: (args) => ({
    props: args,
    template: `
      <explorers-chiclet
        [backgroundColor]="backgroundColor"
        [textColor]="textColor"
        [closeIconColor]="closeIconColor"
        [closeIconSize]="closeIconSize ?? 14"
        [fontSize]="fontSize"
        [borderRadius]="borderRadius"
        [padding]="padding"
        [removable]="removable ?? false"
        [removeAriaLabel]="removeAriaLabel ?? 'Remove'"
      >${args.content}</explorers-chiclet>
    `,
    moduleMetadata: { imports: [ChicletComponent] },
  }),
};
export default meta;
type Story = StoryObj<ChicletStoryArgs>;

export const Default: Story = {
  args: { content: 'APOE' },
};

export const WithBackgroundColor: Story = {
  args: { content: 'PAK1', backgroundColor: '#4caf50' },
};

export const WithTextColor: Story = {
  args: { content: 'PAK1', backgroundColor: '#4caf50', textColor: 'white' },
};

export const WithFontSize: Story = {
  args: { content: 'APOE', fontSize: '14px' },
};

export const WithSquareShape: Story = {
  args: {
    content: '<b>variant:</b>&nbsp;rs29475839',
    backgroundColor: 'var(--color-gray-300)',
    textColor: 'var(--color-gray-900)',
    fontSize: '14px',
    borderRadius: '3px',
    padding: '6px',
  },
};

export const WithBoldPrefix: Story = {
  args: { content: '<b>biodomain:</b>&nbsp;Synapse' },
};

export const Removable: Story = {
  args: { content: 'sex: Female', removable: true },
};

export const RemovableWithCloseIconColor: Story = {
  args: { content: 'sex: Female', removable: true, closeIconColor: '#5081a7' },
};

export const RemovableWithCloseIconSize: Story = {
  args: { content: 'sex: Female', removable: true, closeIconSize: 10 },
};

export const RemovableWithCustomAriaLabel: Story = {
  args: { content: 'sex: Female', removable: true, removeAriaLabel: 'Clear sex: Female' },
};

export const Playground: Story = {
  args: {
    content: '<b>biodomain:</b>&nbsp;Synapse',
    backgroundColor: '#3f51b5',
    textColor: 'white',
    closeIconColor: 'white',
    closeIconSize: 10,
    fontSize: '14px',
    removable: true,
  },
};
