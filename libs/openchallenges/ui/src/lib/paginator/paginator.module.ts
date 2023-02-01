import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaginatorComponent } from './paginator.component';
import { PaginatorModule as PrimengPaginatorModule } from 'primeng/paginator';

@NgModule({
  declarations: [PaginatorComponent],
  imports: [CommonModule, PrimengPaginatorModule],
  exports: [PaginatorComponent],
})
export class PaginatorModule {}
