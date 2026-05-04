import { Component, input } from '@angular/core';
import { Chiclet } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'explorers-chiclet-card',
  standalone: true,
  templateUrl: './chiclet-card.component.html',
  styleUrls: ['./chiclet-card.component.scss'],
})
export class ChicletCardComponent {
  title = input.required<string>();
  chiclets = input.required<Chiclet[]>();
}
