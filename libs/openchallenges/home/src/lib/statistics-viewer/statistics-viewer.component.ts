import { CommonModule } from '@angular/common';
import {
  Component,
  OnInit,
  // OnDestroy,
  // AfterViewInit,
} from '@angular/core';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { HomeDataService } from '../home-data-service';
import { NgxEchartsModule, NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { EChartsOption } from 'echarts';

@Component({
  selector: 'openchallenges-statistics-viewer',
  standalone: true,
  imports: [CommonModule, NgxEchartsModule],
  providers: [
    {
      provide: NGX_ECHARTS_CONFIG,
      useFactory: () => ({ echarts: () => import('echarts') }),
    },
  ],
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent implements OnInit {
  constructor(
    private challengeService: ChallengeService,
    private homeDataService: HomeDataService
  ) {}

  chartOptions!: EChartsOption;

  ngOnInit() {
    this.homeDataService.getAllChallenges().subscribe((challenges) => {
      const dataByYear = this.processData(challenges);
      const challengeStatusData = this.calculateChallengeStatusCounts(
        challenges,
        dataByYear.years
      );

      console.log(challengeStatusData);
      this.chartOptions = {
        title: {
          text: 'Growth of Challenges',
          left: 'center',
        },
        legend: {
          data: [
            'Active Challenges',
            'Completed Challenges',
            'Upcoming Challenges',
          ],
        },
        xAxis: {
          type: 'category',
          data: dataByYear.years,
        },
        yAxis: [
          {
            type: 'value',
            name: 'Cumulative Count',
          },
          {
            type: 'value',
            name: 'Active Count',
          },
        ],
        series: [
          {
            name: 'Active Challenges',
            type: 'bar',
            data: challengeStatusData.activeCounts,
          },
          {
            name: 'Completed Challenges',
            type: 'bar',
            data: challengeStatusData.completedCounts,
          },
          {
            name: 'Upcoming Challenges',
            type: 'bar',
            data: challengeStatusData.upcomingCounts,
          },
          {
            data: dataByYear.cumulativeChallengeCounts,
            type: 'line',
            yAxisIndex: 0,
          },
        ],
      };
    });
  }

  // ngOnDestroy() {
  // }

  private processData(challenges: Challenge[]): {
    years: string[];
    cumulativeChallengeCounts: number[];
  } {
    const dataByYear: { [year: string]: number } = {};
    const cumulativeChallengeCounts: number[] = [];

    challenges.forEach((challenge) => {
      const startYear = new Date(challenge.startDate as string)
        .getFullYear()
        .toString();
      dataByYear[startYear] = (dataByYear[startYear] || 0) + 1;
    });

    const years = Object.keys(dataByYear);
    years.sort(); // Sort years in ascending order

    let cumulativeSum = 0;
    years.forEach((year) => {
      cumulativeSum += dataByYear[year];
      cumulativeChallengeCounts.push(cumulativeSum);
    });

    return {
      years,
      cumulativeChallengeCounts,
    };
  }

  private calculateChallengeStatusCounts(
    challenges: Challenge[],
    years: string[]
  ): {
    activeCounts: number[];
    completedCounts: number[];
    upcomingCounts: number[];
  } {
    const activeCounts: number[] = [];
    const completedCounts: number[] = [];
    const upcomingCounts: number[] = [];

    years.forEach((year) => {
      const yearNumber = parseInt(year, 10);
      const activeChallenges = challenges.filter(
        (challenge) =>
          new Date(challenge.startDate as string).getFullYear() <= yearNumber &&
          new Date(challenge.endDate as string).getFullYear() > yearNumber
      ).length;

      const completedChallenges = challenges.filter(
        (challenge) =>
          new Date(challenge.endDate as string).getFullYear() === yearNumber
      ).length;

      const upcomingChallenges = challenges.filter(
        (challenge) =>
          new Date(challenge.startDate as string).getFullYear() > yearNumber &&
          new Date(challenge.startDate as string).getFullYear() <=
            yearNumber + 1
      ).length;

      activeCounts.push(activeChallenges);
      completedCounts.push(completedChallenges);
      upcomingCounts.push(upcomingChallenges);
    });

    return {
      activeCounts,
      completedCounts,
      upcomingCounts,
    };
  }
}
