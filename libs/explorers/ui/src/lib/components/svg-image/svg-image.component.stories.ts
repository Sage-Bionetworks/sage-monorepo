import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig, componentWrapperDecorator } from '@storybook/angular';
import { SvgImageComponent } from './svg-image.component';

const meta: Meta<SvgImageComponent> = {
  component: SvgImageComponent,
  title: 'UI/SvgImageComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
    // The decorator should be modified to give the component container proper dimensions
    componentWrapperDecorator(
      (story) => `
    <div style="max-width: 900px; margin: 0 auto;">
      <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px;">
        <div style="padding: 15px; background-color: #f8f8f8;">
          <h3>Fixed Container</h3>
          <div style="width: 150px; height: 80px; display: flex; align-items: center; justify-content: center; background-color: white; overflow: hidden;">
            <div style="width: 100%; height: 100%;">
              ${story}
            </div>
          </div>
        </div>
        
        <div style="padding: 15px; background-color: #f8f8f8;">
          <h3>Full Width Container</h3>
          <div style="width: 100%; height: 80px; display: flex; align-items: center; justify-content: center; background-color: white; overflow: hidden;">
            <div style="width: 100%; height: 100%;">
              ${story}
            </div>
          </div>
        </div>
        
        <div style="padding: 15px; background-color: #f8f8f8;">
          <h3>Tall Container</h3>
          <div style="width: 80px; height: 200px; display: flex; align-items: center; justify-content: center; background-color: white; overflow: hidden;">
            <div style="width: 100%; height: 100%;">
              ${story}
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
    ),
  ],
  argTypes: {
    sizeMode: {
      control: 'inline-radio',
      options: ['full-height', 'full-width', 'original'],
      defaultValue: 'original',
    },
  },
};
export default meta;
type Story = StoryObj<SvgImageComponent>;

export const SvgImage: Story = {
  args: {
    imagePath: 'explorers-assets/images/gene-search-icon.svg',
    sizeMode: 'original',
  },
};
