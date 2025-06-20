import { isPlatformServer } from '@angular/common';
import {
  Directive,
  inject,
  OnInit,
  PLATFORM_ID,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';

/**
 * Renders the components annotated with this directed during server-side rendering, and hide them
 * when the rendering is performed by the browser.
 */
@Directive({
  selector: '[sageAppShellRender]',
  standalone: true,
})
export class AppShellRenderDirective implements OnInit {
  private viewContainer = inject(ViewContainerRef);
  private templateRef = inject(TemplateRef<any>);
  readonly platformId = inject(PLATFORM_ID);

  ngOnInit() {
    if (isPlatformServer(this.platformId)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
