import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class GitHubService {
  http = inject(HttpClient);

  private apiUrl = 'https://api.github.com/repos/Sage-Bionetworks/sage-monorepo/tags';
  // private token = 'your_github_token'; // Optional for private repos or higher rate limit

  getCommitSHA(tagName: string): Observable<string> {
    // const headers = new HttpHeaders({
    //   Authorization: `Bearer ${this.token}`,
    // });

    // return this.http.get<any[]>(this.apiUrl, { headers }).pipe(
    return this.http.get<any[]>(this.apiUrl).pipe(
      map((tags) => {
        const tag = tags.find((t) => t.name === tagName);
        return tag ? tag.commit.sha : 'Tag not found';
      }),
    );
  }
}
