import { Component, input } from '@angular/core';
import { Chiclet } from '@sagebionetworks/explorers/models';
import { ChicletComponent } from '../chiclet/chiclet.component';

@Component({
  selector: 'explorers-chiclet-card',
  standalone: true,
  imports: [ChicletComponent],
  templateUrl: './chiclet-card.component.html',
  styleUrls: ['./chiclet-card.component.scss'],
})
export class ChicletCardComponent {
  title = input.required<string>();
  chiclets = input.required<Chiclet[]>();
}
