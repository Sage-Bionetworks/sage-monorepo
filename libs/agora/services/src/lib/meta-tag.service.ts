import { Injectable } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, map, mergeMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class MetaTagService {
  constructor(
    private titleService: Title,
    private metaService: Meta,
    private router: Router,
    private activatedRoute: ActivatedRoute,
  ) {}

  updateMetaTags() {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => this.activatedRoute),
        map((route) => {
          while (route.firstChild) route = route.firstChild;
          return route;
        }),
        filter((route) => route.outlet === 'primary'),
        mergeMap((route) => route.data),
      )
      .subscribe((data) => {
        const pageTitle = data['title'] || 'Agora';
        this.titleService.setTitle(pageTitle);

        if (data['description']) {
          this.metaService.updateTag({ name: 'description', content: data['description'] });
        } else {
          this.metaService.removeTag("name='description'");
        }
      });
  }
}
