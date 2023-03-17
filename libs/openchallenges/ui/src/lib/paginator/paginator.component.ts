import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';

@Component({
  selector: 'openchallenges-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss'],
})
export class PaginatorComponent implements OnInit {
  @Input() pageNumber = 0;
  @Input() pageSize = 0;
  @Input() totalPageLinks!: number;
  @Input() totalPageResults = 0;
  @Input() totalRecords = 0;
  @Input() itemsPerPage!: number[];
  @Output() pageChange = new EventEmitter();

  defaultPageLinkSize = 5;

  ngOnInit(): void {
    this.totalPageLinks = this.totalPageLinks
      ? this.totalPageLinks
      : Math.min(this.defaultPageLinkSize, this.totalPageResults);
  }

  onPageChange(event: any): void {
    this.pageChange.emit(event);
  }
}
