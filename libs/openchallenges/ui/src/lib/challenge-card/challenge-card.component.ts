import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import {
  Challenge,
  ChallengePlatformService,
  SimpleChallengePlatform,
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Challenge as DeprecatedChallenge } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
// import { startCase } from 'lodash';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-challenge-card',
  standalone: true,
  imports: [CommonModule, MatIconModule, RouterModule],
  templateUrl: './challenge-card.component.html',
  styleUrls: ['./challenge-card.component.scss'],
})
export class ChallengeCardComponent implements OnInit {
  @Input() challenge!: Challenge;
  // TODO: remove the deprecatedChallenge when real Challenge has all required properties
  @Input() deprecatedChallenge!: DeprecatedChallenge;
  banner$: Observable<Image> | undefined;
  platform!: SimpleChallengePlatform;
  status!: string | undefined;
  desc!: string;
  incentives!: string;
  statusClass!: string;
  // difficulty!: string | undefined;

  constructor(
    private challengePlatformService: ChallengePlatformService,
    private imageService: ImageService
  ) {}

  ngOnInit(): void {
    if (this.challenge) {
      this.status = this.challenge.status ? this.challenge.status : 'No Status';
      this.statusClass = this.challenge.status || '';
      // this.difficulty = this.challenge.difficulty
      //   ? startCase(this.challenge.difficulty.replace('-', ''))
      //   : undefined;
      this.platform = this.challenge.platform;
      this.desc = this.challenge.headline
        ? this.challenge.headline
        : this.challenge.description;
      this.incentives =
        this.challenge.incentives.length === 0
          ? 'No incentives listed'
          : this.challenge.incentives
              .map(function (s) {
                return (
                  s.charAt(0).toUpperCase() +
                  s.slice(1).replace(/_/g, ' ').toLowerCase()
                );
              })
              .join(', ');
      this.banner$ = this.challenge.avatarUrl
        ? this.imageService.getImage({
            objectKey: this.challenge.avatarUrl,
          })
        : this.imageService.getImage({
            objectKey: 'banner-default.svg',
          });
    }
  }

  truncate(str: string, nchar: number) {
    if (str.length > nchar) {
      const words = str.split(' ');
      let truncated = '';

      for (const word of words) {
        if ((truncated + ' ' + word).length <= nchar - 3) {
          truncated += ' ' + word;
        } else {
          break;
        }
      }

      return truncated + '...';
    } else {
      return str;
    }
  }
}
