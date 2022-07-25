import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthGuard } from './auth-guard.service';
import { KAuthGuard } from './kauth.guard';
import { KeycloakAngularModule } from 'keycloak-angular';

@NgModule({
  declarations: [],
  imports: [CommonModule, KeycloakAngularModule],
  providers: [AuthGuard, KAuthGuard],
})
export class GuardModule {}
