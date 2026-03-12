import { Component, input } from '@angular/core';
import { toBlob } from 'html-to-image';
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
    const target = this.target();
    const paddingPx = this.downloadImagePaddingPx() ?? 0;
    const t0 = performance.now();

    // Monkey-patch getComputedStyle to skip CSS custom properties (--foo vars).
    // Chrome 138+ started enumerating all custom properties in getComputedStyle,
    // causing O(nodes × variables) overhead that makes html-to-image very slow.
    const originalGetComputedStyle = window.getComputedStyle.bind(window);
    window.getComputedStyle = (elt: Element, pseudo?: string | null): CSSStyleDeclaration => {
      const style = originalGetComputedStyle(elt, pseudo);
      return new Proxy(style, {
        get(target, prop) {
          if (prop === 'length') {
            let count = 0;
            for (let i = 0; i < (target as CSSStyleDeclaration).length; i++) {
              if (!(target as CSSStyleDeclaration)[i].startsWith('--')) count++;
            }
            return count;
          }
          if (typeof prop === 'string' && !isNaN(Number(prop))) {
            // Remap numeric index to skip -- properties
            let count = 0;
            for (let i = 0; i < (target as CSSStyleDeclaration).length; i++) {
              const name = (target as CSSStyleDeclaration)[i];
              if (!name.startsWith('--')) {
                if (count === Number(prop)) return name;
                count++;
              }
            }
            return undefined;
          }
          const value = (target as unknown as Record<string | symbol, unknown>)[prop];
          return typeof value === 'function' ? value.bind(target) : value;
        },
      });
    };

    const blob = await toBlob(target, {
      backgroundColor: '#fff',
      width: target.offsetWidth + paddingPx * 2,
      height: target.offsetHeight + paddingPx * 2,
      skipFonts: true,
      ...(paddingPx > 0 && { style: { padding: `${paddingPx}px` } }),
    });

    window.getComputedStyle = originalGetComputedStyle;

    console.log(
      `[benchmark][skipCSSVars] image rendered in ${(performance.now() - t0).toFixed(0)}ms`,
    );
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
