import { CommonModule } from '@angular/common';
import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnChanges,
  ViewChild,
} from '@angular/core';
import {
  Paginator,
  PaginatorModule as PrimengPaginatorModule,
} from 'primeng/paginator';

@Component({
  selector: 'openchallenges-paginator',
  standalone: true,
  imports: [CommonModule, PrimengPaginatorModule],
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss'],
})
export class PaginatorComponent implements OnInit, OnChanges {
  @Input({ required: true }) pageNumber = 0; // index of the new page
  @Input({ required: false }) pageLinkSize = 5;
  @Input({ required: true }) pageSize = 0; // number of rows to display in new page
  @Input({ required: true }) totalRecords = 0;
  @Input({ required: false }) itemsPerPage!: number[];
  @ViewChild('paginator', { static: false }) paginator!: Paginator;
  @Output() pageChange = new EventEmitter();

  itemIndex = 0; // index of the first item in the new page
  isManualReset = false; // set to prevent duplicate calls in onPageChange when using changePage

  ngOnInit(): void {
    this.itemIndex = this.pageNumber * this.pageSize;
  }

  ngOnChanges() {
    this.isManualReset = true;
    this.itemIndex = this.pageNumber * this.pageSize;
    this.paginator.changePage(this.itemIndex);
  }

  onPageChange(event: any): void {
    if (this.isManualReset) {
      this.isManualReset = false;
    } else {
      this.pageChange.emit(event);
    }
  }
}
