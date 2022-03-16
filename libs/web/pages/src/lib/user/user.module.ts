import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { WebUiModule } from '@challenge-registry/web/ui';
import { UserComponent } from './user.component';
import { WebFeatureUserModule } from '@challenge-registry/web/feature-user';

const routes: Routes = [{ path: '', component: UserComponent }];

@NgModule({
  declarations: [UserComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    WebFeatureUserModule,
    WebUiModule,
  ],
  exports: [UserComponent],
})
export class UserModule {}
