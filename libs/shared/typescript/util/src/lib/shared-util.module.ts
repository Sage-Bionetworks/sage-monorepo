import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppShellRenderDirective } from './directives/app-shell-render.directive';
import { AppShellNoRenderDirective } from './directives/app-shell-no-render.directive';
import { SeoService } from './seo/seo.service';
import { JsonLdService } from './seo/json-ld.service';

@NgModule({
  imports: [CommonModule],
  declarations: [AppShellRenderDirective, AppShellNoRenderDirective],
  providers: [JsonLdService, SeoService],
  exports: [AppShellRenderDirective, AppShellNoRenderDirective],
})
export class SharedUtilModule {}
