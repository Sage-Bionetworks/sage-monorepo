import { Component, input } from '@angular/core';
import { DescriptionListItem } from '@sagebionetworks/explorers/models';

/**
 * Label/value pairs rendered as a `<dl>`, so screen readers announce each value with its term.
 *
 * Restyle with CSS custom properties on the host, e.g. `--description-list-term-width`.
 */
@Component({
  selector: 'explorers-description-list',
  templateUrl: './description-list.component.html',
  styleUrls: ['./description-list.component.scss'],
})
export class DescriptionListComponent {
  items = input.required<DescriptionListItem[]>();
}
