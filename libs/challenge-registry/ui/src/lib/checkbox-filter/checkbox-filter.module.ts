import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckboxFilterComponent } from './checkbox-filter.component';
import { CheckboxModule } from 'primeng/checkbox';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [CheckboxFilterComponent],
  imports: [CommonModule, CheckboxModule, FormsModule],
  exports: [CheckboxFilterComponent],
})
export class CheckboxFilterModule {}
