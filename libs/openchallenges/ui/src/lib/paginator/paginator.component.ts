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
  @Input({ required: true }) pageNumber = 0; // index of the new page
  @Input({ required: false }) pageLinkSize = 5;
  @Input({ required: true }) pageSize = 0; // number of rows to display in new page
  @Input({ required: true }) totalRecords = 0;
  @Input({ required: true }) itemsPerPage!: number[];
  @Output() pageChange = new EventEmitter();

  itemIndex = 0; // index of the first item in the new page

  ngOnInit(): void {
    this.itemIndex = this.pageNumber * this.pageSize;
  }

  onPageChange(event: any): void {
    this.pageChange.emit(event);
  }
}
