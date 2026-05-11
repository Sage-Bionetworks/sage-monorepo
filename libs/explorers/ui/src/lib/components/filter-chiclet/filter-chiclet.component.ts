import { Component, input, output } from '@angular/core';
import { ChicletComponent } from '../chiclet/chiclet.component';

@Component({
  selector: 'explorers-filter-chiclet',
  standalone: true,
  imports: [ChicletComponent],
  templateUrl: './filter-chiclet.component.html',
})
/**
 * A removable chiclet styled for filter-summary lists. Renders an optional
 * bolded `name:` prefix followed by a `value`. The remove control's
 * aria-label is composed as `Clear ${value || name}` so screen readers
 * announce per-chiclet context rather than a generic label.
 */
export class FilterChicletComponent {
  name = input<string>('');
  value = input<string>('');
  cleared = output<void>();
}
