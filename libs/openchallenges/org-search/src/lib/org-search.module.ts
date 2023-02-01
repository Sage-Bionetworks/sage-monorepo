import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FooterComponent, UiModule } from '@sagebionetworks/openchallenges/ui';
import { OrgSearchComponent } from './org-search.component';

const routes: Routes = [{ path: '', component: OrgSearchComponent }];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    UiModule,
    FooterComponent,
  ],
  declarations: [OrgSearchComponent],
  exports: [OrgSearchComponent],
})
export class OrgSearchModule {}
