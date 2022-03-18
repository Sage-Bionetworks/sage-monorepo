import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { UserProfileComponent } from './user-profile.component';
import { WebFeatureUserProfileModule } from '@challenge-registry/web/feature-user-profile';
import { WebUiModule } from '@challenge-registry/web/ui';

const routes: Routes = [{ path: '', component: UserProfileComponent }];

@NgModule({
  declarations: [UserProfileComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatTabsModule,
    WebFeatureUserProfileModule,
    WebUiModule,
  ],
  exports: [UserProfileComponent],
})
export class UserProfileModule {}
