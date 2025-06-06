import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { Router } from '@angular/router';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-resource-card',
  imports: [CommonModule, SvgImageComponent],
  templateUrl: './resource-card.component.html',
  styleUrls: ['./resource-card.component.scss'],
})
export class ResourceCardComponent {
  router = inject(Router);

  @Input({ required: true }) link = '';
  @Input({ required: true }) description = '';
  @Input() title: string | undefined = undefined;

  @Input({ required: true }) imagePath = '';
  @Input() altText = '';

  onClick() {
    if (this.link.startsWith('http')) {
      window.open(this.link, '_blank');
    } else {
      // https://github.com/angular/angular/issues/45202
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      this.router.navigate([this.link]);
    }
  }
}
