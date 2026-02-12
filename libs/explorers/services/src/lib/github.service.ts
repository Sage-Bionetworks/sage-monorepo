import { inject, Injectable } from '@angular/core';
import { Octokit } from '@octokit/rest';
import { catchError, from, Observable, of } from 'rxjs';
import { LoggerService } from './logger.service';

@Injectable({
  providedIn: 'root',
})
export class GitHubService {
  private readonly octokit = new Octokit();
  private readonly logger = inject(LoggerService);

  getCommitSHA(tagName: string): Observable<string> {
    return from(this.fetchCommitSHA(tagName)).pipe(
      catchError((error) => {
        this.logger.error('Error fetching tags', error);
        return of('');
      }),
    );
  }

  private async fetchCommitSHA(tagName: string): Promise<string> {
    const iterator = this.octokit.paginate.iterator(this.octokit.rest.repos.listTags, {
      owner: 'Sage-Bionetworks',
      repo: 'sage-monorepo',
      per_page: 100,
    });

    for await (const { data: tags } of iterator) {
      const tag = tags.find((t) => t.name === tagName);
      if (tag) {
        return this.getShortSHA(tag.commit.sha);
      }
    }

    return '';
  }

  getShortSHA(fullSHA: string) {
    if (!fullSHA || fullSHA.length !== 40) return '';

    return fullSHA.slice(0, 7);
  }
}
