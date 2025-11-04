import { Component, input, model, output, ViewEncapsulation } from '@angular/core';
import { ComparisonToolFilterOption } from '@sagebionetworks/explorers/models';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-comparison-tool-filter-list-item',
  standalone: true,
  imports: [SvgIconComponent],
  templateUrl: './comparison-tool-filter-list-item.component.html',
  styleUrls: ['./comparison-tool-filter-list-item.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolFilterListItemComponent {
  item = input<ComparisonToolFilterOption | null>();
  title = input<string>('');
  description = input<string>('');

  isVisible = model<boolean>(false);

  clearEvent = output<object>();

  clearWasClicked() {
    this.isVisible.set(false);
    this.clearEvent.emit(this.item);
  }
}
