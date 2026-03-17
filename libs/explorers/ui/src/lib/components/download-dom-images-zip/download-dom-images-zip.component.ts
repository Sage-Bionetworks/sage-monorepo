import { Component, input } from '@angular/core';
import { saveAs } from 'file-saver';
import JSZip from 'jszip';
import { BaseDownloadDomImageComponent } from '../base-download-dom-image/base-download-dom-image.component';
import { captureDomToBlob } from '@sagebionetworks/explorers/util';

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
  downloadImagePaddingPx = input<number>();

  performDownload = async (fileType: string): Promise<void> => {
    const zip = new JSZip();
    const paddingPx = this.downloadImagePaddingPx() ?? 0;

    for (const domFile of this.domFiles()) {
      const blob = await captureDomToBlob(domFile.target, paddingPx);
      if (blob) {
        zip.file(domFile.filename + fileType, blob);
      }
    }

    const zipBlob = await zip.generateAsync({ type: 'blob' });
    saveAs(zipBlob, this.filename() + '.zip');
  };
}
