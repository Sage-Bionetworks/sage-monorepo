import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user.component';
import { RouterModule, Routes } from '@angular/router';
import { WebUiModule } from '@challenge-registry/web/ui';

const routes: Routes = [{ path: '', component: UserComponent }];

@NgModule({
  declarations: [UserComponent],
  imports: [CommonModule, RouterModule.forChild(routes), WebUiModule],
  exports: [UserComponent],
})
export class UserModule {}
