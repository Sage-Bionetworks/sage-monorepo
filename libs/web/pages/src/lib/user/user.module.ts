import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { UserComponent } from './user.component';
import { WebFeatureUserModule } from '@challenge-registry/web/feature-user';
import { WebUiModule } from '@challenge-registry/web/ui';

const routes: Routes = [{ path: '', component: UserComponent }];

@NgModule({
  declarations: [UserComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatTabsModule,
    WebFeatureUserModule,
    WebUiModule,
  ],
  exports: [UserComponent],
})
export class UserModule {}
