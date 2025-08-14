/* eslint-disable @typescript-eslint/no-this-alias */
import domtoimage from 'dom-to-image-more';

import { Component, input, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faDownload, faSpinner } from '@fortawesome/free-solid-svg-icons';
import { saveAs } from 'file-saver';
import { ButtonModule } from 'primeng/button';
import { Popover, PopoverModule } from 'primeng/popover';
import { RadioButtonModule } from 'primeng/radiobutton';

interface Type {
  value: string;
  label: string;
}

@Component({
  selector: 'explorers-download-dom-image',
  imports: [FormsModule, PopoverModule, RadioButtonModule, ButtonModule, FontAwesomeModule],
  templateUrl: './download-dom-image.component.html',
  styleUrls: ['./download-dom-image.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DownloadDomImageComponent {
  target = input.required<HTMLElement>();
  heading = input('Download this plot as:');
  filename = input.required();
  buttonLabel = input('');

  downloadIcon = faDownload;
  spinnerIcon = faSpinner;

  selectedType = '.png';
  types: Type[] = [
    {
      value: '.png',
      label: 'PNG',
    },
    {
      value: '.jpeg',
      label: 'JPEG',
    },
  ];

  error = '';
  isLoading = false;
  resizeTimer: ReturnType<typeof setTimeout> | number = 0;

  @ViewChild('op', { static: true }) popover: Popover = {} as Popover;

  download() {
    if (this.isLoading) {
      return;
    }

    const self = this;
    this.error = '';
    this.isLoading = true;

    // width and height need to be specified
    // known issue: https://github.com/1904labs/dom-to-image-more/issues/198
    domtoimage
      .toBlob(this.target(), {
        bgcolor: '#fff',
        width: this.target().offsetWidth,
        height: this.target().offsetHeight,
      })
      .then((blob: any) => {
        saveAs(blob, this.filename() + this.selectedType);
        this.isLoading = false;
        this.hide();
      })
      .catch(function (err: string) {
        self.error = 'Oops, something went wrong!';
        console.error(err);
      });
  }

  hide() {
    this.error = '';
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
