import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatButtonModule } from '@angular/material/button';
import { NotFoundComponent } from './not-found.component';
import { FooterComponent, UiModule } from '@sagebionetworks/openchallenges/ui';

const routes: Routes = [{ path: '', component: NotFoundComponent }];

@NgModule({
  declarations: [NotFoundComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatCardModule,
    MatButtonModule,
    UiModule,
    FooterComponent,
  ],
  exports: [NotFoundComponent],
})
export class NotFoundModule {}
