import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  Account,
  Challenge,
} from '@sagebionetworks/api-client-angular-deprecated';
import { map, Observable, of, Subscription } from 'rxjs';
import { Tab } from './tab.model';
import { CHALLENGE_TABS } from './challenge-tabs';
import {
  MOCK_CHALLENGES,
  Avatar,
} from '@sagebionetworks/challenge-registry/ui';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-challenge',
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.scss'],
})
export class ChallengeComponent implements OnInit {
  public appVersion: string;
  account$!: Observable<Account | undefined>;
  challenge$: Observable<Challenge> = of(MOCK_CHALLENGES[0]);
  loggedIn = false;
  challengeAvatar!: Avatar;
  tabs = CHALLENGE_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab: Tab = this.tabs['overview'];
  private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private route: ActivatedRoute,
    private readonly configService: ConfigService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit(): void {
    this.route.params.subscribe((param) => console.log(param['orgLogin']));

    const activeTab$ = this.activatedRoute.queryParamMap.pipe(
      map((params: ParamMap) => params.get('tab')),
      map((key) => (key === null ? 'overview' : key))
    );

    this.challenge$.subscribe(
      (challenge) =>
      (this.challengeAvatar = {
        name: challenge.displayName
          ? (challenge.displayName as string)
          : challenge.name.replace(/-/g, ' '),
        src: '', // TODO: Replace with avatarUrl once implemented in Challenge Object
        size: 320,
      })
    );

    const activeTabSub = activeTab$.subscribe((key) => {
      if (!this.tabKeys.includes(key)) {
        this.router.navigate([]);
      } else {
        this.activeTab = this.tabs[key];
      }
    });

    this.subscriptions.push(activeTabSub);
  }
}
