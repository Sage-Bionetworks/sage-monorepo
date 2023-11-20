import { CommonModule } from '@angular/common';
import {
  Component,
  Input,
  Output,
  EventEmitter,
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
export class PaginatorComponent {
  @Input({ required: true }) pageNumber = 0; // index of the new page
  @Input({ required: false }) pageLinkSize = 5;
  @Input({ required: true }) pageSize = 0; // number of rows to display in new page
  @Input({ required: true }) totalRecords = 0;
  @Input({ required: false }) itemsPerPage!: number[];
  @ViewChild('paginator', { static: false }) paginator!: Paginator;
  @Output() pageChange = new EventEmitter();

  itemIndex = 0; // index of the first item in the new page

  // change itemIndex's value will not dynamically trigger selection updates - seems a primeng issue
  // ngOnInit(): void {
  //   this.itemIndex = this.pageNumber * this.pageSize;
  // }

  onPageChange(event: any): void {
    this.pageChange.emit(event);
  }

  // Reset paginator to first page
  resetPage(): void {
    this.paginator.changePage(0);
  }
}
