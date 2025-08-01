import { Component, inject, Input } from '@angular/core';
import { Router } from '@angular/router';
import { SvgImageComponent } from '../svg-image/svg-image.component';
import { isExternalLink } from '@sagebionetworks/shared/util';

@Component({
  selector: 'explorers-resource-card',
  imports: [SvgImageComponent],
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
    if (isExternalLink(this.link)) {
      window.open(this.link, '_blank');
    } else {
      // https://github.com/angular/angular/issues/45202
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      this.router.navigateByUrl(this.link);
    }
  }
}
