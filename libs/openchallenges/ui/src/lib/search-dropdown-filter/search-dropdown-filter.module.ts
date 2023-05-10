import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchDropdownFilterComponent } from './search-dropdown-filter.component';
import { MultiSelectModule } from 'primeng/multiselect';
import { FormsModule } from '@angular/forms';
import { AvatarComponent } from '../avatar/avatar.component';

@NgModule({
  declarations: [SearchDropdownFilterComponent],
  imports: [AvatarComponent, CommonModule, FormsModule, MultiSelectModule],
  exports: [SearchDropdownFilterComponent],
})
export class SearchDropdownFilterModule {}
