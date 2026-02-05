import { Component, Input } from '@angular/core';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-resource-card',
  imports: [SvgImageComponent],
  templateUrl: './resource-card.component.html',
  styleUrls: ['./resource-card.component.scss'],
})
export class ResourceCardComponent {
  @Input({ required: true }) link = '';
  @Input({ required: true }) description = '';
  @Input() title: string | undefined = undefined;

  @Input({ required: true }) imagePath = '';
  @Input() altText = '';
}
