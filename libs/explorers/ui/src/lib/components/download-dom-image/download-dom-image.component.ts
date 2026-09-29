import { Component, inject, input } from '@angular/core';
import { DownloadNote } from '@sagebionetworks/explorers/models';
import { LoggerService } from '@sagebionetworks/explorers/services';
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
  private readonly logger = inject(LoggerService);

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
    if (blob) {
      saveAs(blob, this.filename() + fileType);
    } else {
      this.logger.warn('Failed to capture a plot image; no image was downloaded', {
        filename: this.filename(),
      });
    }
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
