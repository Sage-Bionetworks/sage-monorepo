import { Component, input } from '@angular/core';
import { saveAs } from 'file-saver';
import JSZip from 'jszip';
import { BaseDownloadDomImageComponent } from '../base-download-dom-image/base-download-dom-image.component';
import { captureDomToBlob, csvDataToString } from '@sagebionetworks/explorers/util';
import { FILE_TYPE_CSV } from '../base-download-dom-image/file-types';

type DomFile = {
  filename: string;
  target: HTMLElement;
};

type CsvFile = {
  filename: string;
  data: string[][];
};

@Component({
  selector: 'explorers-download-dom-images-zip',
  imports: [BaseDownloadDomImageComponent],
  templateUrl: './download-dom-images-zip.component.html',
  styleUrls: ['./download-dom-images-zip.component.scss'],
})
export class DownloadDomImagesZipComponent {
  domFiles = input.required<DomFile[]>();
  csvFiles = input<CsvFile[]>([]);
  filename = input.required();
  downloadImagePaddingPx = input<number>();
  hasCsvDownload = input<boolean>(false);
  hasImageDownload = input<boolean>(true);

  performDownload = async (fileType: string): Promise<void> => {
    const zip = new JSZip();

    if (fileType === FILE_TYPE_CSV) {
      for (const csvFile of this.csvFiles()) {
        zip.file(csvFile.filename + fileType, csvDataToString(csvFile.data));
      }
    } else {
      const paddingPx = this.downloadImagePaddingPx() ?? 0;
      for (const domFile of this.domFiles()) {
        const blob = await captureDomToBlob(domFile.target, paddingPx);
        if (blob) {
          zip.file(domFile.filename + fileType, blob);
        }
      }
    }

    const zipBlob = await zip.generateAsync({ type: 'blob' });
    saveAs(zipBlob, this.filename() + '.zip');
  };
}
