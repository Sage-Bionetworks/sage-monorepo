import { Component, input } from '@angular/core';
import domtoimage from 'dom-to-image-more';
import { saveAs } from 'file-saver';
import { BaseDownloadDomImageComponent } from '../base-download-dom-image/base-download-dom-image.component';

@Component({
  selector: 'explorers-download-dom-image',
  imports: [BaseDownloadDomImageComponent],
  templateUrl: './download-dom-image.component.html',
  styleUrls: ['./download-dom-image.component.scss'],
})
export class DownloadDomImageComponent {
  target = input.required<HTMLElement>();
  heading = input('Download this plot as:');
  filename = input.required();
  buttonLabel = input('');

  performDownload = async (fileType: string): Promise<void> => {
    // width and height need to be specified
    // known issue: https://github.com/1904labs/dom-to-image-more/issues/198
    const blob = await domtoimage.toBlob(this.target(), {
      bgcolor: '#fff',
      width: this.target().offsetWidth,
      height: this.target().offsetHeight,
    });
    saveAs(blob, this.filename() + fileType);
  };
}
