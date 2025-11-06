/* eslint-disable @typescript-eslint/no-this-alias */
import { Component, computed, input, signal, ViewChild, ViewEncapsulation } from '@angular/core';
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

  downloadIcon = faDownload;
  spinnerIcon = faSpinner;

  selectedType = '.png';
  types = computed(() => {
    const hasCsvType = this.hasCsvDownload();
    const imageLabelPostfix = hasCsvType ? ' image' : '';

    const imageTypes: Type[] = [
      {
        value: '.png',
        label: `PNG${imageLabelPostfix}`,
      },
      {
        value: '.jpeg',
        label: `JPEG${imageLabelPostfix}`,
      },
    ];
    const types = hasCsvType ? [...imageTypes, { value: '.csv', label: 'CSV data' }] : imageTypes;
    return types;
  });

  error = signal('');
  isLoading = signal(false);
  resizeTimer: ReturnType<typeof setTimeout> | number = 0;

  @ViewChild('op', { static: true }) popover: Popover = {} as Popover;

  async download() {
    if (this.isLoading()) return;

    this.error.set('');
    this.isLoading.set(true);

    try {
      await this.performDownload()(this.selectedType);
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
    const self = this;
    clearTimeout(this.resizeTimer);
    this.resizeTimer = setTimeout(function () {
      self.hide();
    }, 0);
  }
}
