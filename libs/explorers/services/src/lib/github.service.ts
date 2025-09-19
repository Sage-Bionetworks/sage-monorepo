import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface GitHubTag {
  name: string;
  commit: {
    sha: string;
  };
}

@Injectable({
  providedIn: 'root',
})
export class GitHubService {
  http = inject(HttpClient);

  private readonly apiUrl = 'https://api.github.com/repos/Sage-Bionetworks/sage-monorepo/tags';

  getCommitSHA(tagName: string): Observable<string> {
    return this.http.get<GitHubTag[]>(this.apiUrl).pipe(
      map((tags) => {
        const tag = tags.find((t) => t.name === tagName);
        return tag ? this.getShortSHA(tag.commit.sha) : '';
      }),
    );
  }

  getShortSHA(fullSHA: string) {
    if (!fullSHA || fullSHA.length !== 40) return '';

    return fullSHA.slice(0, 7);
  }
}
