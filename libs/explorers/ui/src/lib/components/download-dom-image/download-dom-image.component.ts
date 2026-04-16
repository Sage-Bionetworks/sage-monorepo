import { Component, input } from '@angular/core';
import { saveAs } from 'file-saver';
import { BaseDownloadDomImageComponent } from '../base-download-dom-image/base-download-dom-image.component';
import { captureDomToBlob, csvDataToString } from '@sagebionetworks/explorers/util';
import {
  FILE_TYPE_CSV,
  FILE_TYPE_JPEG,
  FILE_TYPE_PNG,
} from '../base-download-dom-image/file-types';

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
  hasCsvDownload = input<boolean>(false);
  hasImageDownload = input<boolean>(true);
  data = input<string[][]>([]);
  downloadImagePaddingPx = input<number>();

  performDownload = async (fileType: string): Promise<void> => {
    if (fileType === FILE_TYPE_JPEG || fileType === FILE_TYPE_PNG) {
      await this.downloadImage(fileType);
    }
    if (fileType === FILE_TYPE_CSV) {
      await this.downloadCsvData(fileType);
    }
  };

  downloadImage = async (fileType: string): Promise<void> => {
    const target = this.target();
    const paddingPx = this.downloadImagePaddingPx() ?? 0;
    const blob = await captureDomToBlob(target, paddingPx);
    if (blob) saveAs(blob, this.filename() + fileType);
  };

  downloadCsvData = async (fileType: string): Promise<void> => {
    const csvType = 'text/csv;charset=utf-8;';
    const data = this.data();

    if (!data || data.length === 0) {
      const emptyBlob = new Blob([], { type: csvType });
      saveAs(emptyBlob, this.filename() + fileType);
      return;
    }

    const blob = new Blob([csvDataToString(data)], { type: csvType });
    saveAs(blob, this.filename() + fileType);
  };
}
