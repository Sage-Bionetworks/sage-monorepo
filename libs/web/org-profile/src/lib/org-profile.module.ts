import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { OrgProfileComponent } from './org-profile.component';
import { WebUiModule } from '@challenge-registry/web/ui';

const routes: Routes = [{ path: '', component: OrgProfileComponent }];

@NgModule({
  declarations: [OrgProfileComponent],
  imports: [CommonModule, RouterModule.forChild(routes), WebUiModule],
  exports: [OrgProfileComponent],
})
export class OrgProfileModule {}
