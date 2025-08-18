import { Component, input } from '@angular/core';
import domtoimage from 'dom-to-image-more';
import { saveAs } from 'file-saver';
import JSZip from 'jszip';
import { BaseDownloadDomImageComponent } from '../base-download-dom-image/base-download-dom-image.component';

type DomFile = {
  filename: string;
  target: HTMLElement;
};

@Component({
  selector: 'explorers-download-dom-images-zip',
  imports: [BaseDownloadDomImageComponent],
  templateUrl: './download-dom-images-zip.component.html',
  styleUrls: ['./download-dom-images-zip.component.scss'],
})
export class DownloadDomImagesZipComponent {
  domFiles = input.required<DomFile[]>();
  filename = input.required();

  performDownload = async (fileType: string): Promise<void> => {
    const zip = new JSZip();

    for (const domFile of this.domFiles()) {
      // width and height need to be specified
      // known issue: https://github.com/1904labs/dom-to-image-more/issues/198
      const blob = await domtoimage.toBlob(domFile.target, {
        bgcolor: '#fff',
        width: domFile.target.offsetWidth,
        height: domFile.target.offsetHeight,
      });
      zip.file(domFile.filename + fileType, blob);
    }

    const zipBlob = await zip.generateAsync({ type: 'blob' });
    saveAs(zipBlob, this.filename() + '.zip');
  };
}
