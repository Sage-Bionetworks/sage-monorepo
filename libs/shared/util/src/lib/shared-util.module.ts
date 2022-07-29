import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppShellRenderDirective } from './directives/app-shell-render.directive';
import { AppShellNoRenderDirective } from './directives/app-shell-no-render.directive';

@NgModule({
  imports: [CommonModule],
  declarations: [AppShellRenderDirective, AppShellNoRenderDirective],
  exports: [AppShellRenderDirective, AppShellNoRenderDirective],
})
export class SharedUtilModule {}
