/* eslint-disable */

// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { SynapseWikiMarkdown } from '@sagebionetworks/explorers/models';
import { synapseWikiMock } from '.';

// -------------------------------------------------------------------------- //
// Stub
// -------------------------------------------------------------------------- //
@Injectable()
export class SynapseApiServiceStub {
  constructor() {}

  getWikiMarkdown(ownerId: string, wikiId: string): Observable<SynapseWikiMarkdown> {
    return of(synapseWikiMock);
  }

  renderHtml(html: string) {
    return '';
  }
}
