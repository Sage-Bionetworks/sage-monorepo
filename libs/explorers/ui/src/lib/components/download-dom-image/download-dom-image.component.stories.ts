import { Component, ElementRef, ViewChild } from '@angular/core';
import { Meta, StoryObj } from '@storybook/angular';
import { DownloadDomImageComponent } from './download-dom-image.component';

@Component({
  selector: 'explorers-download-dom-image-wrapper',
  template: `
    <div>
      <explorers-download-dom-image
        [target]="plot"
        [filename]="filename"
        [heading]="heading"
        [buttonLabel]="buttonLabel"
        [hasCsvDownload]="hasCsvDownload"
        [data]="data"
      />
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
  buttonLabel = '';
  hasCsvDownload = false;
  data: string[][] = [
    ['Column 1', 'Column 2'],
    ['Data 1', 'Data 2'],
  ];

  getTarget = () => this.plotRef.nativeElement;
}

const meta: Meta<StorybookDownloadDomImageWrapper> = {
  title: 'UI/DownloadDomImageComponent',
  component: StorybookDownloadDomImageWrapper,
  args: {
    filename: 'plot',
    heading: 'Download this plot as:',
  },
  argTypes: {
    data: { control: false },
  },
};
export default meta;
type Story = StoryObj<StorybookDownloadDomImageWrapper>;

export const IconActionButton: Story = {
  args: {
    buttonLabel: '',
  },
};

export const TextActionButton: Story = {
  args: {
    buttonLabel: 'Download',
  },
};

export const CsvData: Story = {
  args: {
    buttonLabel: 'Download',
    hasCsvDownload: true,
    data: [
      ['X', 'Y'],
      ['1', '2'],
      ['3', '4'],
    ],
  },
};
