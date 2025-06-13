import type { Meta, StoryObj } from '@storybook/angular';
import { SectionComponent } from './section.component';

const meta: Meta<SectionComponent> = {
  component: SectionComponent,
  title: 'UI/SectionComponent',
  render: (args) => {
    return {
      args,
      template: `<explorers-section containerSize="${args.containerSize}">
                    <div class="demo-content">
                      <p>Change the page width to see how this component works.</p>
                    </div>
                  </explorers-section>
                  <style>
                    .demo-content { 
                      border: 2px dashed #ccc; 
                      background-color: #f9f9f9;
                      min-height: 500px; 
                    }
                  </style>`,
    };
  },
};
export default meta;
type Story = StoryObj<SectionComponent>;

export const Demo: Story = {
  args: {
    containerSize: 'md',
  },
};
