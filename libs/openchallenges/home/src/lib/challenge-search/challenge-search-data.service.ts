import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ChallengeSearchDataService {
  private searchTerm: BehaviorSubject<string | undefined> = new BehaviorSubject<
    string | undefined
  >(undefined);

  setSearchTerm(text: string | undefined): void {
    this.searchTerm.next(text);
  }

  getSearchTerm(): Observable<string | undefined> {
    return this.searchTerm.asObservable();
  }
}
