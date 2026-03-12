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

    const restore = this.patchGetComputedStyleToSkipCSSVars();

    let blob: Blob | null;
    try {
      blob = await toBlob(target, {
        backgroundColor: '#fff',
        width: target.offsetWidth + paddingPx * 2,
        height: target.offsetHeight + paddingPx * 2,
        skipFonts: true, // skip fetching/embedding @font-face files
        ...(paddingPx > 0 && { style: { padding: `${paddingPx}px` } }),
      });
    } finally {
      restore();
    }

    if (blob) saveAs(blob, this.filename() + fileType);
  };

  /**
   * Patches getComputedStyle to hide CSS custom properties (--vars) from html-to-image,
   * reducing the number of properties copied per node during serialization.
   * Returns a restore function to undo the patch after capture.
   */
  private readonly patchGetComputedStyleToSkipCSSVars = (): (() => void) => {
    const original = window.getComputedStyle.bind(window);

    window.getComputedStyle = (elt: Element, pseudo?: string | null): CSSStyleDeclaration => {
      const style = original(elt, pseudo);
      // Wrap in a Proxy to make --vars invisible to html-to-image:
      // - style.length returns only the count of non-custom properties
      // - style[0], style[1], ... skip over --vars so html-to-image never sees them
      return new Proxy(style, {
        get(target, prop) {
          const allProps = target as CSSStyleDeclaration;

          // style.length — return how many non-custom properties there are
          if (prop === 'length') {
            let count = 0;
            for (let i = 0; i < allProps.length; i++) {
              if (!allProps[i].startsWith('--')) count++;
            }
            return count;
          }

          // style[0], style[1], ...: remap indices to skip --vars
          if (typeof prop === 'string' && !isNaN(Number(prop))) {
            let count = 0;
            for (let i = 0; i < allProps.length; i++) {
              if (!allProps[i].startsWith('--')) {
                if (count === Number(prop)) return allProps[i];
                count++;
              }
            }
            return undefined;
          }

          // All other accesses (e.g. style.color, style.getPropertyValue) pass through
          const value = (target as unknown as Record<string | symbol, unknown>)[prop];
          return typeof value === 'function' ? value.bind(target) : value;
        },
      });
    };

    return () => {
      window.getComputedStyle = original;
    };
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
