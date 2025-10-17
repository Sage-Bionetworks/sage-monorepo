import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
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
  @Input() item: any;
  @Input() isVisible = false;
  @Input() title = '';
  @Input() description = '';
  @Output() clearEvent: EventEmitter<object> = new EventEmitter<object>();

  clearWasClicked() {
    this.isVisible = false;
    this.clearEvent.emit(this.item);
  }
}
