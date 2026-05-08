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
 * Label content can be supplied two ways, and both can coexist:
 * - `[label]` — a plain or pre-composed HTML string, rendered via sanitized
 *   `[innerHTML]`. Best for dynamic strings (e.g. computed labels). Angular's
 *   `DomSanitizer` strips scripts and event handlers.
 * - Content projection — declarative markup between the tags, e.g.
 *   `<explorers-chiclet><b>biodomain:</b> Synapse</explorers-chiclet>`.
 *   Best when the markup is static enough to express in the template.
 *
 * When both are set, `label` renders first, then projected content.
 */
export class ChicletComponent {
  label = input<string>();
  backgroundColor = input<string>();
  textColor = input<string>();
  closeIconColor = input<string>();
  closeIconSize = input<number>(14);
  fontSize = input<string>();
  removable = input<boolean>(false);
  removeAriaLabel = input<string>('Remove');
  removed = output<void>();
}
