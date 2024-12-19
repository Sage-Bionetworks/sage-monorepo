import { Component, Input, Output, EventEmitter, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'agora-gene-comparison-tool-filter-list-item',
  standalone: true,
  templateUrl: './gene-comparison-tool-filter-list-item.component.html',
  styleUrls: ['./gene-comparison-tool-filter-list-item.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneComparisonToolFilterListItemComponent {
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
