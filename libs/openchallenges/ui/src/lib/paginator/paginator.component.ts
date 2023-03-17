import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'openchallenges-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss'],
})
export class PaginatorComponent {
  @Input() pageNumber = 0;
  @Input() pageLinkSize = 5;
  @Input() pageSize = 0;
  @Input() totalRecords = 0;
  @Input() itemsPerPage!: number[];
  @Output() pageChange = new EventEmitter();

  onPageChange(event: any): void {
    this.pageChange.emit(event);
  }
}
