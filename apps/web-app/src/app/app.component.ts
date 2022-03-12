import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { PageTitleService } from '@challenge-registry/web/data-access';
import { NavbarSection } from '@challenge-registry/web/ui';
import { Registry, RegistryService } from '@challenge-registry/api-angular';
import { APP_SECTIONS } from './app-sections';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'web-app';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  registry$!: Observable<Registry>;

  constructor(
    private pageTitleService: PageTitleService,
    private registryService: RegistryService
  ) {}

  ngOnInit() {
    this.pageTitleService.setTitle('Challenge Registry');
    this.registry$ = this.registryService.getRegistry();
  }
}
