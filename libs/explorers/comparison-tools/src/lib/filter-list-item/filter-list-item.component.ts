import { Component, Input, Output, EventEmitter, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'explorers-filter-list-item',
  standalone: true,
  templateUrl: './filter-list-item.component.html',
  styleUrls: ['./filter-list-item.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class FilterListItemComponent {
  // TODO convert to latest signals

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
