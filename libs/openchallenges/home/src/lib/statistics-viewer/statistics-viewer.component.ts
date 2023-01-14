import { Component } from '@angular/core';
import {
  Registry,
  RegistryService,
} from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-statistics-viewer',
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent {
  registry$!: Observable<Registry>;
  constructor(private registryService: RegistryService) {
    this.registry$ = this.registryService.getRegistry();
  }
}
