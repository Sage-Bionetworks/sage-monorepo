import { Component } from '@angular/core';
import { Registry, RegistryService } from '@sagebionetworks/api-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'challenge-registry-statistics-viewer',
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent {
  registry$!: Observable<Registry>;

  constructor(private registryService: RegistryService) {
    this.registry$ = this.registryService.getRegistry();
  }
}
