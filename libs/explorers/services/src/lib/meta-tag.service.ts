import { DestroyRef, inject, Injectable } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute, ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

interface RouteMetaData {
  title?: string | ((snapshot: ActivatedRouteSnapshot) => string);
  description?: string | ((snapshot: ActivatedRouteSnapshot) => string);
}

@Injectable({
  providedIn: 'root',
})
export class MetaTagService {
  private titleService = inject(Title);
  private metaService = inject(Meta);
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  initialize(defaultTitle: string) {
    this.router.events
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        filter((event) => event instanceof NavigationEnd),
        map(() => this.activatedRoute),
        map(this.getDeepestRoute),
        filter((route) => route.outlet === 'primary'),
        map((route) => ({
          data: route.snapshot.data as RouteMetaData,
          snapshot: route.snapshot,
        })),
      )
      .subscribe(({ data, snapshot }) => {
        this.setPageTitle(data, snapshot, defaultTitle);
        this.setPageDescription(data, snapshot);
      });
  }

  private getDeepestRoute(route: ActivatedRoute): ActivatedRoute {
    while (route.firstChild) {
      route = route.firstChild;
    }
    return route;
  }

  private setPageTitle(
    data: RouteMetaData,
    currentSnapshot: ActivatedRouteSnapshot,
    defaultTitle: string,
  ) {
    // Title: if data.title is a function, execute it to get the title
    const pageTitle =
      typeof data.title === 'function'
        ? data.title(currentSnapshot) || defaultTitle
        : data.title || defaultTitle;
    this.titleService.setTitle(pageTitle);
  }

  private setPageDescription(data: RouteMetaData, currentSnapshot: ActivatedRouteSnapshot) {
    // Description: if data.description is a function, execute it to get the description
    let description = data.description;
    if (typeof description === 'function') {
      description = description(currentSnapshot);
    }
    if (description) {
      this.metaService.updateTag({ name: 'description', content: description });
    } else {
      this.metaService.removeTag("name='description'");
    }
  }
}
