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
 * Hides the components annotated with this directed during server-side rendering, and render them
 * when the rendering is performed by the browser.
 */
@Directive({
  selector: '[sageAppShellNoRender]',
  standalone: true,
})
export class AppShellNoRenderDirective implements OnInit {
  private viewContainer = inject(ViewContainerRef);
  private templateRef = inject(TemplateRef<any>);
  readonly platformId = inject(PLATFORM_ID);

  ngOnInit() {
    if (isPlatformServer(this.platformId)) {
      this.viewContainer.clear();
    } else {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
