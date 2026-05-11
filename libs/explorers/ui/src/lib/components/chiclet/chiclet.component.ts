import { Component, input, output, ViewEncapsulation } from '@angular/core';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-chiclet',
  standalone: true,
  imports: [SvgIconComponent],
  templateUrl: './chiclet.component.html',
  styleUrls: ['./chiclet.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
/**
 * A chip-style label primitive, optionally removable.
 *
 * Content is supplied via projection between the tags, e.g.
 * `<explorers-chiclet><b>biodomain:</b>&nbsp;Synapse</explorers-chiclet>`.
 */
export class ChicletComponent {
  backgroundColor = input<string>();
  textColor = input<string>();
  closeIconColor = input<string>();
  closeIconSize = input<number>(14);
  fontSize = input<string>();
  borderRadius = input<string>();
  padding = input<string>();
  removable = input<boolean>(false);
  removeAriaLabel = input<string>('Remove');
  removed = output<void>();
}
