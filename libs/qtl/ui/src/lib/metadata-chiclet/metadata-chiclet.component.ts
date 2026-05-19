import { Component, input } from '@angular/core';
import { ChicletComponent } from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'qtl-metadata-chiclet',
  imports: [ChicletComponent],
  templateUrl: './metadata-chiclet.component.html',
  styleUrls: ['./metadata-chiclet.component.scss'],
})
export class MetadataChicletComponent {
  backgroundColor = input.required<string>();
  label = input.required<string>();
  value = input.required<string>();
}
