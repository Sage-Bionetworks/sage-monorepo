import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import {
  Challenge,
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
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
  @Input({ required: true }) challenge!: Challenge;
  banner$: Observable<Image> | undefined;
  dynamicStatus!: string;
  desc!: string;
  incentives!: string;
  statusClass!: string;
  time_info!: string | number;

  constructor(private imageService: ImageService) {}

  ngOnInit(): void {
    if (this.challenge) {
      this.dynamicStatus = this.getChallengeStatus(this.challenge);
      this.statusClass = this.dynamicStatus || '';
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

      this.time_info = this.getTimeInfo(this.challenge);
    }
  }

  private getChallengeStatus(challenge: Challenge): string {
    // 1. Both startDate and endDate are missing -> No Status
    // 2. Both startDate and endDate present
    //    2.1.  now --- startDate -> upcoming
    //    2.2.  startDate --- now --- endDate -> active
    //    2.3.  endDate --- now -> completed
    // 3. Either of startDate or endDate is missing
    //    3.1. provide startDate, but no endDate
    //         startDate --- ? --- now --- ? -> cannot decide if active/completed --> No Status
    //    3.2. provided endDate, but no startDate
    //         ? --- now --- ? --- endDate -> cannot decide if active/upcoming -> No Status

    const now = new Date();
    const startDate = challenge.startDate;
    const endDate = challenge.endDate;

    switch (true) {
      case startDate && now < new Date(startDate):
        return 'upcoming';
      case endDate && now > new Date(endDate):
        return 'completed';
      case !!startDate && !!endDate:
        return 'active';
      default:
        return 'No Status';
    }
  }

  private getTimeInfo(challenge: Challenge): string {
    if (challenge.endDate && this.dynamicStatus === 'completed') {
      return `Ended ${this.calcTimeDiff(challenge.endDate, true)} ago`;
    } else if (challenge.endDate && this.dynamicStatus === 'active') {
      return `Ends in ${this.calcTimeDiff(challenge.endDate)}`;
    } else if (challenge.startDate && this.dynamicStatus === 'upcoming') {
      return `Starts in ${this.calcTimeDiff(challenge.startDate)}`;
    } else {
      return '';
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
