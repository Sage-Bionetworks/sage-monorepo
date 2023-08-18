import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { PaginatorModule as PrimengPaginatorModule } from 'primeng/paginator';

@Component({
  selector: 'openchallenges-paginator',
  standalone: true,
  imports: [CommonModule, PrimengPaginatorModule],
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss'],
})
export class PaginatorComponent implements OnInit {
  @Input() pageNumber = 0; // index of the new page
  @Input() pageLinkSize = 5;
  @Input() pageSize = 0; // number of rows to display in new page
  @Input() totalRecords = 0;
  @Input() itemsPerPage!: number[];
  @Output() pageChange = new EventEmitter();

  itemIndex = 0; // index of the first item in the new page

  ngOnInit(): void {
    this.itemIndex = this.pageNumber * this.pageSize;
  }

  onPageChange(event: any): void {
    this.pageChange.emit(event);
  }
}
