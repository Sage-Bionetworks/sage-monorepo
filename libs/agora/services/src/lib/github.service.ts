import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class GitHubService {
  http = inject(HttpClient);

  private apiUrl = 'https://api.github.com/repos/Sage-Bionetworks/sage-monorepo/tags';

  getCommitSHA(tagName: string): Observable<string> {
    return this.http.get<any[]>(this.apiUrl).pipe(
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
