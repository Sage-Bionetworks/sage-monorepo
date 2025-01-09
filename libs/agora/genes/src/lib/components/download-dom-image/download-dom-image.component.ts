/* eslint-disable @typescript-eslint/no-this-alias */
import domtoimage from 'dom-to-image-more';

import { Component, ViewChild, Input, ViewEncapsulation } from '@angular/core';
import { OverlayPanel, OverlayPanelModule } from 'primeng/overlaypanel';
import { saveAs } from 'file-saver';
import { RadioButtonModule } from 'primeng/radiobutton';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Type {
  value: string;
  label: string;
}

@Component({
  selector: 'agora-download-dom-image',
  imports: [CommonModule, FormsModule, OverlayPanelModule, RadioButtonModule],
  templateUrl: './download-dom-image.component.html',
  styleUrls: ['./download-dom-image.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DownloadDomImageComponent {
  @Input() target: HTMLElement = {} as HTMLElement;
  @Input() heading = 'Download this plot as:';
  @Input() filename = 'agora';

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

  @ViewChild('op', { static: true }) overlayPanel: OverlayPanel = {} as OverlayPanel;

  download() {
    if (this.isLoading) {
      return;
    }

    const self = this;
    this.error = '';
    this.isLoading = true;

    domtoimage
      .toBlob(this.target, { bgcolor: '#fff' })
      .then((blob: any) => {
        saveAs(blob, this.filename + this.selectedType);
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
    this.overlayPanel.hide();
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
