import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { NotFoundComponent } from './not-found.component';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';

const routes: Routes = [{ path: '', component: NotFoundComponent }];

@NgModule({
  declarations: [NotFoundComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatCardModule,
    UiModule,
  ],
  exports: [NotFoundComponent],
})
export class NotFoundModule {}
