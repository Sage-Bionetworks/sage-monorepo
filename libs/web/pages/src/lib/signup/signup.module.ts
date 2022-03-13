import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { SignupComponent } from './signup.component';
import { WebUiModule } from '@challenge-registry/web/ui';

const routes: Routes = [{ path: '', component: SignupComponent }];

@NgModule({
  declarations: [SignupComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatCardModule,
    WebUiModule,
  ],
  exports: [SignupComponent],
})
export class SignupModule {}
