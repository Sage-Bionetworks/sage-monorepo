import { AsyncPipe, NgClass, TitleCasePipe } from '@angular/common';
import { Component, inject, Input, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { Challenge, Image, ImageService } from '@sagebionetworks/openchallenges/api-client-angular';
import { ChallengeIncentiveLabelPipe } from '@sagebionetworks/openchallenges/util';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-challenge-card',
  imports: [ChallengeIncentiveLabelPipe, NgClass, MatIconModule, RouterModule, TitleCasePipe],
  templateUrl: './challenge-card.component.html',
  styleUrls: ['./challenge-card.component.scss'],
})
export class ChallengeCardComponent implements OnInit {
  private readonly imageService = inject(ImageService);

  @Input({ required: true }) challenge!: Challenge;
  banner$: Observable<Image> | undefined;
  status!: string | undefined;
  desc!: string;
  statusClass!: string;
  time_info!: string | number;

  ngOnInit(): void {
    if (this.challenge) {
      this.status = this.challenge.status ? this.challenge.status : 'No Status';
      this.statusClass = this.challenge.status || '';
      this.desc = this.challenge.headline ? this.challenge.headline : this.challenge.description;
      this.banner$ = this.challenge.avatarUrl
        ? this.imageService.getImage({
            objectKey: this.challenge.avatarUrl,
          })
        : this.imageService.getImage({
            objectKey: 'banner-default.svg',
          });
      try {
        if (this.challenge.endDate && this.status === 'completed') {
          const timeSince = this.calcTimeDiff(this.challenge.endDate, true);
          if (timeSince) {
            this.time_info = `Ended ${timeSince} ago`;
          }
        } else if (this.challenge.endDate && this.status === 'active') {
          this.time_info = `Ends in ${this.calcTimeDiff(this.challenge.endDate)}`;
        } else if (this.challenge.startDate && this.status === 'upcoming') {
          this.time_info = `Starts in ${this.calcTimeDiff(this.challenge.startDate)}`;
        }
      } catch (error: unknown) {
        console.log(error);
      }
    }
  }

  calcTimeDiff(date: string, hideFarDates = false): string | never {
    const pattern = /\d{4}-\d{2}-\d{2}/;
    if (!pattern.test(date)) {
      throw Error(`${date} does not match the schema: ${pattern}`);
    }
    const refDate: any = new Date(date + ' 00:00:00');
    const now: any = new Date();
    const diffMs = Math.abs(refDate - now);

    // Calculate the time difference in years, months, weeks, days, and hours.
    const timeDiff = {
      month: Math.floor(diffMs / 2_629_746_000),
      week: Math.floor(diffMs / 604_800_000),
      day: Math.floor(diffMs / 86_400_000),
      hour: Math.floor(diffMs / 3_600_000),
    };

    // Find the largest unit of time and return in human-readable format.
    let timeDiffString = '';
    for (const [unit, value] of Object.entries(timeDiff)) {
      if (hideFarDates && unit === 'month' && value > 3) {
        break;
      } else if (value > 0) {
        timeDiffString = `${value} ${unit}` + (value > 1 ? 's' : '');
        break;
      }
    }
    return timeDiffString;
  }
}
