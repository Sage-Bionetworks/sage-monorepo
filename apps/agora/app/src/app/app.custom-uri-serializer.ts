import { DefaultUrlSerializer, UrlSerializer, UrlTree } from '@angular/router';

// CustomUrlSerializer strips parentheses from legacy Agora URLs
// before parsing; this prevents legacy path parts (e.g. (genes-router:genes-list)
// from being stripped before Angular route handling.
export class CustomUrlSerializer implements UrlSerializer {
  private readonly defaultSerializer = new DefaultUrlSerializer();

  parse(url: string): UrlTree {
    url = url.replace('(', '').replace(')', '');
    return this.defaultSerializer.parse(url);
  }

  serialize(tree: UrlTree): string {
    return this.defaultSerializer.serialize(tree);
  }
}
