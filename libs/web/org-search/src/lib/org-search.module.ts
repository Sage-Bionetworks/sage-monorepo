import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { WebUiModule } from '@sage-bionetworks/web/ui';
import { OrgSearchComponent } from './org-search.component';

const routes: Routes = [{ path: '', component: OrgSearchComponent }];

@NgModule({
  imports: [CommonModule, RouterModule.forChild(routes), WebUiModule],
  declarations: [OrgSearchComponent],
  exports: [OrgSearchComponent],
})
export class OrgSearchModule {}
