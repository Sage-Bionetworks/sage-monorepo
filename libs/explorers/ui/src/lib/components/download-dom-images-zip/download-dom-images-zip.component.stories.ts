import { Component, ElementRef, ViewChild } from '@angular/core';
import { Meta, StoryObj } from '@storybook/angular';
import { DownloadDomImagesZipComponent } from './download-dom-images-zip.component';

@Component({
  selector: 'explorers-download-dom-images-zip-wrapper',
  template: `
    <div>
      <explorers-download-dom-images-zip
        [domFiles]="[
          { target: plot, filename: 'plot' },
          { target: plot2, filename: 'plot2' },
        ]"
        [filename]="filename"
      />
      <div
        #plot
        style="width:300px;height:200px;background:#e0e0e0;display:flex;align-items:center;justify-content:center;"
      >
        <span>Plot Area</span>
      </div>
      <div
        #plot2
        style="width:300px;height:200px;background:#e0e0e0;display:flex;align-items:center;justify-content:center;"
      >
        <span>Plot2 Area</span>
      </div>
    </div>
  `,
  standalone: true,
  imports: [DownloadDomImagesZipComponent],
})
class StorybookDownloadDomImagesZipWrapper {
  @ViewChild('plot', { static: true }) plotRef!: ElementRef<HTMLElement>;
  @ViewChild('plot2', { static: true }) plot2Ref!: ElementRef<HTMLElement>;

  filename = 'my-plots-zip';
}

const meta: Meta<StorybookDownloadDomImagesZipWrapper> = {
  title: 'UI/DownloadDomImagesZipComponent',
  component: StorybookDownloadDomImagesZipWrapper,
};
export default meta;
type Story = StoryObj<StorybookDownloadDomImagesZipWrapper>;

export const Default: Story = {
  args: {
    filename: 'many-plots',
  },
};
