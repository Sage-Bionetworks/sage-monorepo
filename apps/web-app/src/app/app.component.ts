import { Component, OnInit } from '@angular/core';
// import { Challenge } from '@challenge-registry/api-angular';
import { PageTitleService } from '@challenge-registry/web/data-access';
// import { FooterComponent } from '@challenge-registry/web/ui';
import '@challenge-registry/shared/web-components';
import { Observable } from 'rxjs';
import { Registry, RegistryService } from '@challenge-registry/api-angular';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'web-app';
  registry$!: Observable<Registry>;

  constructor(
    private pageTitleService: PageTitleService,
    private registryService: RegistryService
  ) {}

  ngOnInit() {
    this.pageTitleService.setTitle('Awesome web-app');
    this.registry$ = this.registryService.getRegistry();
  }
}
