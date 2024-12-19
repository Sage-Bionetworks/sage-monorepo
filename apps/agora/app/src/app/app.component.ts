import { Component, inject, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterModule } from '@angular/router';
import {
  FooterComponent,
  HeaderComponent,
  LoadingOverlayComponent,
} from '@sagebionetworks/agora/ui';
import { filter } from 'rxjs';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  standalone: true,
  imports: [RouterModule, HeaderComponent, FooterComponent, LoadingOverlayComponent, ToastModule],
  providers: [MessageService],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);
  titleService = inject(Title);
  metaService = inject(Meta);

  ngOnInit(): void {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      const route = this.getChildRoute(this.activatedRoute);
      route.data.subscribe((data: any) => {
        this.titleService.setTitle(data.title || 'Agora');

        if (data.description) {
          this.metaService.updateTag({ name: 'description', content: data.description });
        } else {
          this.metaService.removeTag("name='description'");
        }
      });
    });
  }

  getChildRoute(activatedRoute: ActivatedRoute): ActivatedRoute {
    if (activatedRoute.firstChild) {
      return this.getChildRoute(activatedRoute.firstChild);
    } else {
      return activatedRoute;
    }
  }
}
