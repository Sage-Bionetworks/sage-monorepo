import {
  Component,
  computed,
  effect,
  input,
  signal,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faDownload, faSpinner } from '@fortawesome/free-solid-svg-icons';
import { ButtonModule } from 'primeng/button';
import { Popover, PopoverModule } from 'primeng/popover';
import { RadioButtonModule } from 'primeng/radiobutton';

interface Type {
  value: string;
  label: string;
}

@Component({
  selector: 'explorers-base-download-dom-image',
  imports: [FormsModule, PopoverModule, RadioButtonModule, ButtonModule, FontAwesomeModule],
  templateUrl: './base-download-dom-image.component.html',
  styleUrls: ['./base-download-dom-image.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class BaseDownloadDomImageComponent {
  heading = input('Download this plot as:');
  modalButtonLabel = input('Download');
  buttonLabel = input('Download');
  filename = input.required();
  performDownload = input.required<(fileType: string) => Promise<void>>();
  hasCsvDownload = input<boolean>(false);
  hasImageDownload = input<boolean>(true);

  downloadIcon = faDownload;
  spinnerIcon = faSpinner;

  types = computed(() => {
    const hasCsv = this.hasCsvDownload();
    const hasImage = this.hasImageDownload();

    const imageTypes: Type[] = [
      { value: '.png', label: hasCsv ? 'PNG image' : 'PNG' },
      { value: '.jpeg', label: hasCsv ? 'JPEG image' : 'JPEG' },
    ];

    const csvType: Type = { value: '.csv', label: 'CSV data' };

    if (hasImage && hasCsv) return [...imageTypes, csvType];
    if (hasImage) return imageTypes;
    if (hasCsv) return [csvType];
    return [];
  });

  selectedType = signal('');

  constructor() {
    effect(() => {
      const types = this.types();
      if (types.length > 0) {
        this.selectedType.set(types[0].value);
      }
    });
  }

  error = signal('');
  isLoading = signal(false);
  resizeTimer: ReturnType<typeof setTimeout> | number = 0;

  @ViewChild('op', { static: true }) popover: Popover = {} as Popover;

  async download() {
    if (this.isLoading()) return;

    this.error.set('');
    this.isLoading.set(true);

    try {
      // Yield so the loading spinner paints before expensive DOM serialization starts
      await this.waitForNextPaint();
      await this.performDownload()(this.selectedType());
      this.hide();
    } catch (err) {
      this.error.set('Oops, something went wrong!');
      console.error(err);
    } finally {
      this.isLoading.set(false);
    }
  }

  hide() {
    this.error.set('');
    this.popover.hide();
  }

  onRotate() {
    this.hide();
  }

  onResize() {
    clearTimeout(this.resizeTimer);
    this.resizeTimer = setTimeout(() => {
      this.hide();
    }, 0);
  }

  private waitForNextPaint(): Promise<void> {
    return new Promise((resolve) => {
      requestAnimationFrame(() => {
        requestAnimationFrame(() => resolve());
      });
    });
  }
}
