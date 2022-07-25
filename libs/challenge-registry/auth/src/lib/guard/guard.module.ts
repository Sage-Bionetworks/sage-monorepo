import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthGuard } from './auth-guard.service';
import { KAuthGuard } from './kauth.guard';

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [AuthGuard, KAuthGuard],
})
export class GuardModule {}
