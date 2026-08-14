import { Component, input } from '@angular/core';

@Component({
  selector: 'explorers-details-label',
  templateUrl: './details-label.component.html',
  styleUrls: ['./details-label.component.scss'],
})
export class DetailsLabelComponent {
  left = input<string>('');
  right = input<string>('');
}
