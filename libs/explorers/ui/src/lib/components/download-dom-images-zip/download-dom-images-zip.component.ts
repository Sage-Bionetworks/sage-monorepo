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
    const totalStart = performance.now();
    const zip = new JSZip();
    const paddingPx = this.downloadImagePaddingPx() ?? 0;

    for (const domFile of this.domFiles()) {
      const imageStart = performance.now();
      const blob = await captureDomToBlob(domFile.target, paddingPx);
      console.log(
        `[DownloadDomImagesZip] Image "${domFile.filename}" captured in ${(performance.now() - imageStart).toFixed(0)}ms`,
      );
      if (blob) {
        zip.file(domFile.filename + fileType, blob);
      }
    }

    const zipStart = performance.now();
    const zipBlob = await zip.generateAsync({ type: 'blob' });
    console.log(
      `[DownloadDomImagesZip] ZIP generated in ${(performance.now() - zipStart).toFixed(0)}ms`,
    );
    saveAs(zipBlob, this.filename() + '.zip');
    console.log(
      `[DownloadDomImagesZip] Total download completed in ${(performance.now() - totalStart).toFixed(0)}ms`,
    );
  };
}
