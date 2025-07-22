import { inject } from '@angular/core';
import { DefaultUrlSerializer, UrlSerializer, UrlTree } from '@angular/router';
import { HelperService } from '@sagebionetworks/explorers/services';

// CustomUrlSerializer encodes special characters (forward slash in parentheses, parentheses)
// so they are treated as data rather than as part of the route
export class CustomUrlSerializer implements UrlSerializer {
  private readonly defaultSerializer = new DefaultUrlSerializer();
  helperService = inject(HelperService);

  parse(url: string): UrlTree {
    url = this.helperService.encodeParenthesesForwardSlashes(url);
    return this.defaultSerializer.parse(url);
  }

  serialize(tree: UrlTree): string {
    return this.defaultSerializer.serialize(tree);
  }
}
