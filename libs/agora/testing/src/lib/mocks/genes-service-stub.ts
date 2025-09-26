import { Injectable } from '@angular/core';
import { Distribution, Gene } from '@sagebionetworks/agora/api-client';
import { Observable } from 'rxjs';
import { ApiServiceStub } from '.';

@Injectable({ providedIn: 'root' })
export class GenesServiceStub {
  private readonly apiServiceStub: ApiServiceStub;

  constructor() {
    this.apiServiceStub = new ApiServiceStub();
  }

  getGene(id: string): Observable<Gene | null> {
    return this.apiServiceStub.getGene(id);
  }

  getDistribution(): Observable<Distribution> {
    return this.apiServiceStub.getDistribution();
  }
}
