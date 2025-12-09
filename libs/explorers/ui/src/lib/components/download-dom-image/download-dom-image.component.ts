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
  hasCsvDownload = input<boolean>(false);
  hasImageDownload = input<boolean>(true);
  data = input<string[][]>([]);
  downloadImagePaddingPx = input<number>();

  performDownload = async (fileType: string): Promise<void> => {
    if (fileType === '.jpeg' || fileType === '.png') {
      await this.downloadImage(fileType);
    }
    if (fileType === '.csv') {
      await this.downloadCsvData(fileType);
    }
  };

  downloadImage = async (fileType: string): Promise<void> => {
    // width and height need to be specified
    // known issue: https://github.com/1904labs/dom-to-image-more/issues/198
    const paddingPx = this.downloadImagePaddingPx() ?? 0;
    const blob = await domtoimage.toBlob(this.target(), {
      bgcolor: '#fff',
      width: this.target().offsetWidth + paddingPx * 2,
      height: this.target().offsetHeight + paddingPx * 2,
      ...(this.downloadImagePaddingPx() !== undefined && {
        style: {
          padding: `${paddingPx}px`,
        },
      }),
    });
    saveAs(blob, this.filename() + fileType);
  };

  downloadCsvData = async (fileType: string): Promise<void> => {
    const csvType = 'text/csv;charset=utf-8;';
    const data = this.data();

    if (!data || data.length === 0) {
      const emptyBlob = new Blob([], { type: csvType });
      saveAs(emptyBlob, this.filename() + fileType);
      return;
    }

    let csv = '';
    for (const row of data) {
      csv += this.arrayToCSVString(row);
    }

    const blob = new Blob([csv], { type: csvType });
    saveAs(blob, this.filename() + fileType);
  };

  arrayToCSVString(values: string[]): string {
    return (
      values
        .map((value) => {
          const escaped = value.replaceAll('"', '""');
          return `"${escaped}"`;
        })
        .join(',') + '\n'
    );
  }
}
