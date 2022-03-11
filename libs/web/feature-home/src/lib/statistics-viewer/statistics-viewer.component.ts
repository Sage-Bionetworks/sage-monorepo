import { Component, Inject } from '@angular/core';
import {
  Configuration,
  Registry,
  RegistryService,
} from '@challenge-registry/api-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'challenge-registry-statistics-viewer',
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent {
  registry$!: Observable<Registry>;

  constructor(
    private registryService: RegistryService,
    @Inject(Configuration) private apiConfig: Configuration
  ) {
    this.registry$ = this.registryService.getRegistry();
  }
}
