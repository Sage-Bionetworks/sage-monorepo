/* eslint-disable */

// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { SynapseWiki } from '@sagebionetworks/explorers/models';
import { synapseWikiMock } from '.';

// -------------------------------------------------------------------------- //
// Stub
// -------------------------------------------------------------------------- //
@Injectable()
export class SynapseApiServiceStub {
  constructor() {}

  getWiki(ownerId: string, wikiId: string): Observable<SynapseWiki> {
    return of(synapseWikiMock);
  }

  renderHtml(html: string) {
    return '';
  }
}
