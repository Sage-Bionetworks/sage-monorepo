import { Component, input } from '@angular/core';
import { saveAs } from 'file-saver';
import JSZip from 'jszip';
import { BaseDownloadDomImageComponent } from '../base-download-dom-image/base-download-dom-image.component';
import { captureDomToBlob, csvDataToString, CSV_MIME_TYPE } from '@sagebionetworks/explorers/util';
import { FILE_TYPE_CSV } from '../base-download-dom-image/file-types';

type DomFile = {
  filename: string;
  target: HTMLElement;
};

type CsvFile = {
  filename: string;
  data: string[][];
};

type DownloadFile = {
  name: string;
  // csv content stays a string, which is what JSZip expects for text
  content: Blob | string;
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
    const files: DownloadFile[] = [];

    if (fileType === FILE_TYPE_CSV) {
      for (const csvFile of this.csvFiles()) {
        files.push({
          name: csvFile.filename + fileType,
          content: csvDataToString(csvFile.data),
        });
      }
    } else {
      const paddingPx = this.downloadImagePaddingPx() ?? 0;
      for (const domFile of this.domFiles()) {
        const blob = await captureDomToBlob(domFile.target, paddingPx);
        if (!blob) {
          // fail the whole download rather than hand over a zip with a plot silently missing
          throw new Error(`Failed to capture a plot image for download: ${domFile.filename}`);
        }
        files.push({ name: domFile.filename + fileType, content: blob });
      }
    }

    if (files.length === 0) {
      throw new Error(`No files were available for download: ${fileType}`);
    }

    if (files.length === 1) {
      const { name, content } = files[0];
      // saveAs treats a bare string as a URL, so string content has to be wrapped
      saveAs(
        content instanceof Blob ? content : new Blob([content], { type: CSV_MIME_TYPE }),
        name,
      );
      return;
    }

    const zip = new JSZip();
    for (const file of files) {
      zip.file(file.name, file.content);
    }

    const zipBlob = await zip.generateAsync({ type: 'blob' });
    saveAs(zipBlob, this.filename() + '.zip');
  };
}
