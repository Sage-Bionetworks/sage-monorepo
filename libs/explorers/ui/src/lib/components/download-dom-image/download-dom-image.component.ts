import { Component, input } from '@angular/core';
import { DownloadNote } from '@sagebionetworks/explorers/models';
import { saveAs } from 'file-saver';
import { BaseDownloadDomImageComponent } from '../base-download-dom-image/base-download-dom-image.component';
import { captureDomToBlob, csvDataToBlob, CSV_MIME_TYPE } from '@sagebionetworks/explorers/util';
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
  buttonTooltip = input('');
  note = input<DownloadNote>();
  hasCsvDownload = input<boolean>(false);
  hasImageDownload = input<boolean>(true);
  data = input<string[][]>([]);
  downloadImagePaddingPx = input<number>();
  disabled = input<boolean>(false);

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
    if (!blob) {
      // throwing keeps the popover open and surfaces the base component's error message
      throw new Error(`Failed to capture the plot image for download: ${this.filename()}`);
    }
    saveAs(blob, this.filename() + fileType);
  };

  downloadCsvData = async (fileType: string): Promise<void> => {
    const data = this.data();

    if (!data || data.length === 0) {
      const emptyBlob = new Blob([], { type: CSV_MIME_TYPE });
      saveAs(emptyBlob, this.filename() + fileType);
      return;
    }

    saveAs(csvDataToBlob(data), this.filename() + fileType);
  };
}
