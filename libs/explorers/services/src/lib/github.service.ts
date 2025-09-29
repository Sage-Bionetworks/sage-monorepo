import { Injectable } from '@angular/core';
import { Octokit } from '@octokit/rest';

@Injectable({
  providedIn: 'root',
})
export class GitHubService {
  private readonly octokit = new Octokit();

  async getCommitSHA(tagName: string): Promise<string> {
    try {
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
    } catch (error) {
      console.error('Error fetching tags:', error);
      return '';
    }
  }

  getShortSHA(fullSHA: string) {
    if (!fullSHA || fullSHA.length !== 40) return '';

    return fullSHA.slice(0, 7);
  }
}
