import { DefaultUrlSerializer, UrlSerializer, UrlTree } from '@angular/router';

const LEGACY_GENES_ROUTER_OUTLET = /^\/genes\/\((genes-router:[^)]*)\)/;

// Agora 2.x placed gene pages in a named outlet, e.g. /genes/(genes-router:genes-list).
// Unwrapping that outlet before parsing lets the legacy redirects in app.routes.ts match
// instead of Angular reading the parentheses as an outlet group.
export class CustomUrlSerializer implements UrlSerializer {
  private readonly defaultSerializer = new DefaultUrlSerializer();

  parse(url: string): UrlTree {
    return this.defaultSerializer.parse(url.replace(LEGACY_GENES_ROUTER_OUTLET, '/genes/$1'));
  }

  serialize(tree: UrlTree): string {
    return this.defaultSerializer.serialize(tree);
  }
}
