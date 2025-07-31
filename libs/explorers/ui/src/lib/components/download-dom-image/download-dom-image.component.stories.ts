import { Meta, StoryObj } from '@storybook/angular';
import { DownloadDomImageComponent } from './download-dom-image.component';
import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'explorers-download-dom-image-wrapper',
  template: `
    <div>
      <explorers-download-dom-image
        [target]="plot"
        [filename]="filename"
        [heading]="heading"
      ></explorers-download-dom-image>
      <div
        #plot
        style="width:300px;height:200px;background:#e0e0e0;display:flex;align-items:center;justify-content:center;"
      >
        <span>Plot Area</span>
      </div>
    </div>
  `,
  standalone: true,
  imports: [DownloadDomImageComponent],
})
class StorybookDownloadDomImageWrapper {
  @ViewChild('plot', { static: true }) plotRef!: ElementRef<HTMLElement>;
  filename = 'my-plot';
  heading = 'Download this plot as:';

  getTarget = () => this.plotRef.nativeElement;
}

const meta: Meta<StorybookDownloadDomImageWrapper> = {
  title: 'UI/DownloadDomImageComponent',
  component: StorybookDownloadDomImageWrapper,
};
export default meta;
type Story = StoryObj<StorybookDownloadDomImageWrapper>;

export const Default: Story = {
  args: {
    filename: 'plot',
    heading: 'Download this plot as:',
  },
};
