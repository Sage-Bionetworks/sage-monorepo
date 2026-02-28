// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { HttpClient, HttpContext } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { SUPPRESS_ERROR_OVERLAY } from '@sagebionetworks/explorers/constants';
import sanitizeHtml from 'sanitize-html';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { SynapseWikiMarkdown, TermsOfUseInfo } from '@sagebionetworks/explorers/models';

// -------------------------------------------------------------------------- //
// Service
// -------------------------------------------------------------------------- //
@Injectable({
  providedIn: 'root',
})
export class SynapseApiService {
  http = inject(HttpClient);

  private cache: { [key: string]: any } = {
    wikis: {},
    terms: null,
  };

  getWikiMarkdown(
    ownerId: string,
    wikiId: string,
    suppressErrorOverlay = false,
  ): Observable<SynapseWikiMarkdown> {
    const key = ownerId + wikiId;
    if (this.cache['wikis'][key]) {
      return of(this.cache['wikis'][key]);
    } else {
      const context = suppressErrorOverlay
        ? new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true)
        : undefined;
      return this.http
        .get('https://repo-prod.prod.sagebase.org/repo/v1/entity/' + ownerId + '/wiki/' + wikiId, {
          context,
        })
        .pipe(
          tap((wiki: any) => {
            this.cache['wikis'][key] = wiki;
          }),
        );
    }
  }

  getTermsOfService(): Observable<string> {
    if (this.cache['terms']) {
      return of(this.cache['terms']);
    }

    return this.http
      .get<TermsOfUseInfo>('https://repo-prod.prod.sagebase.org/auth/v1/termsOfUse2/info')
      .pipe(
        switchMap((info) =>
          this.http.get(info.termsOfServiceUrl, { responseType: 'text' }).pipe(
            map((markdown) => {
              this.cache['terms'] = markdown;
              return markdown;
            }),
          ),
        ),
      );
  }

  renderHtml(html: string) {
    // Sanitize
    let sanitized = sanitizeHtml(html);

    // Add bold tags
    sanitized = sanitized.replace(/\*\*(.*?)\*\*/g, this.replaceBold);

    // Add syn links
    sanitized = sanitized.replace(/\[(.+?)\]\((.+?)\)/g, this.replaceLinks);

    // Add emails
    sanitized = sanitized.replace(
      /([a-zA-Z0-9.*_-]+@[a-zA-Z0-9 .*_-]+\.[a-zA-Z0-9*_-]+)/gi,
      this.replaceEmail,
    );

    // Add variables
    sanitized = sanitized.replace(/\${(.*?)}/g, this.replaceVariable);

    return sanitized;
  }

  replaceLinks(match: string, text: string, url: string) {
    const target = '_blank';

    if (url.startsWith('syn')) {
      url = 'https://synapse.org/#!Synapse:' + url;
    }

    return (
      '<a href="' + url + '"' + (target ? ' target="' + target + '"' : '') + '>' + text + '</a>'
    );
  }

  replaceBold(match: string, content: string) {
    return '<b>' + content + '</b>';
  }

  replaceVariable(match: string, content: string) {
    let params: any = null;

    try {
      const contentArr = content.split('?');
      params = new URLSearchParams(contentArr.length > 1 ? contentArr[1] : contentArr[0]);
    } catch (err) {
      console.error(err);
    }

    if (params) {
      if (params.has('fileName')) {
        return (
          '<img src="' +
          params.get('fileName') +
          '" alt="' +
          (params.get('amp;altText') || '') +
          '" />'
        );
      } else if (params.has('vimeoId')) {
        return (
          '<iframe src="https://player.vimeo.com/video/' +
          params.get('vimeoId') +
          '?autoplay=0&speed=1" frameborder="0" allow="autoplay; encrypted-media" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>'
        );
      }
    }

    return '';
  }

  replaceEmail(match: string, content: string) {
    // Remove all spaces and *
    content = content.replace(/(\s|\*)/g, '');

    if (content) {
      return '<a class="link email-link" href="mailto:' + content + '">' + content + '</a>';
    }

    return content;
  }
}
