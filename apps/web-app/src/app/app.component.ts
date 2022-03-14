import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { PageTitleService } from '@challenge-registry/web/data-access';
import { NavbarSection } from '@challenge-registry/web/ui';
import { Registry, RegistryService } from '@challenge-registry/api-angular';
import { APP_SECTIONS } from './app-sections';
import { AuthService } from '@challenge-registry/web/auth';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'web-app';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  registry$!: Observable<Registry>;
  loggedIn = false;

  private subscriptions: Subscription[] = [];

  constructor(
    private pageTitleService: PageTitleService,
    private registryService: RegistryService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const loggedInSub = this.authService
      .isLoggedIn()
      .subscribe((loggedIn) => (this.loggedIn = loggedIn));
    this.subscriptions.push(loggedInSub);

    this.pageTitleService.setTitle('Challenge Registry');
    this.registry$ = this.registryService.getRegistry();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
