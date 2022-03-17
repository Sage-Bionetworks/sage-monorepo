import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { NotFoundComponent } from './not-found.component';
import { WebUiModule } from '@challenge-registry/web/ui';

const routes: Routes = [{ path: '', component: NotFoundComponent }];

@NgModule({
  declarations: [NotFoundComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatCardModule,
    WebUiModule,
  ],
  exports: [NotFoundComponent],
})
export class NotFoundModule {}
