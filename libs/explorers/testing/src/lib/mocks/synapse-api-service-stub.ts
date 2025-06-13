import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { SynapseWikiMarkdown } from '@sagebionetworks/explorers/models';

@Injectable()
export class SynapseApiServiceStub {
  private readonly baseUrl = 'https://repo-prod.prod.sagebase.org/repo/v1';

  constructor(private http: HttpClient) {}

  getWikiMarkdown(ownerId: string, wikiId: string): Observable<SynapseWikiMarkdown> {
    return this.http.get<SynapseWikiMarkdown>(`${this.baseUrl}/entity/${ownerId}/wiki/${wikiId}`);
  }

  renderHtml(markdown: string): string {
    // Simple markdown to HTML conversion for testing
    return markdown
      .replace(/^# (.*$)/gim, '<h1>$1</h1>')
      .replace(/^## (.*$)/gim, '<h2>$1</h2>')
      .replace(/\*\*(.*)\*\*/gim, '<strong>$1</strong>')
      .replace(/\*(.*)\*/gim, '<em>$1</em>');
  }
}
