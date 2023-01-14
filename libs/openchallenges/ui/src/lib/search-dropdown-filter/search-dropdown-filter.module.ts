import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchDropdownFilterComponent } from './search-dropdown-filter.component';
import { MultiSelectModule } from 'primeng/multiselect';
import { AvatarModule } from '../avatar/avatar.module';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [SearchDropdownFilterComponent],
  imports: [AvatarModule, CommonModule, FormsModule, MultiSelectModule],
  exports: [SearchDropdownFilterComponent],
})
export class SearchDropdownFilterModule {}
