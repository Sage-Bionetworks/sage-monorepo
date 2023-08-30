import { CommonModule } from '@angular/common';
import {
  Component,
  OnInit,
  // OnDestroy,
  // AfterViewInit,
} from '@angular/core';
import {
  Challenge,
  // ChallengeService,
  // ChallengePlatform,
  // ChallengePlatformService,
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
    // private challengeService: ChallengeService,
    // private challengePlatformService: ChallengePlatformService,
    private homeDataService: HomeDataService
  ) {}

  chartOptions!: EChartsOption;
  // challenges!: Challenge[];
  // challengePlatforms!: ChallengePlatform[];
  // platformOptions: string[] = [];

  ngOnInit() {
    // this.challengePlatformService
    //   .listChallengePlatforms({})
    //   .subscribe(
    //     (platforms) =>
    //       (this.platformOptions = platforms.challengePlatforms.map(
    //         (platform) => platform.name
    //       ))
    //   );

    this.homeDataService.getAllChallenges().subscribe((challenges) => {
      // this.challenges = challenges;
      const dataByYear = this.processData(challenges);
      // const challengeStatusData = this.calculateChallengeStatusCounts(
      //   challenges,
      //   dataByYear.years
      // );

      this.chartOptions = {
        title: {
          text: 'Growth of Challenges',
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            axis: 'x',
          },
        },
        // legend: {},
        xAxis: {
          type: 'category',
          data: dataByYear.years,
        },
        yAxis: [
          {
            type: 'value',
            name: 'Challenges Count',
          },
        ],
        series: [
          // {
          //   name: 'Active',
          //   type: 'bar',
          //   data: challengeStatusData.activeCounts,
          //   itemStyle: {
          //     color: '#71c663',
          //   },
          // },
          // {
          //   name: 'Completed',
          //   type: 'bar',
          //   data: challengeStatusData.completedCounts,
          //   itemStyle: {
          //     color: '#ffb6c1',
          //   },
          // },
          // {
          //   name: 'Upcoming',
          //   type: 'bar',
          //   data: challengeStatusData.upcomingCounts,
          //   itemStyle: {
          //     color: '#ffc56d',
          //   },
          // },
          {
            name: 'Total',
            data: dataByYear.cumulativeChallengeCounts,
            type: 'bar',
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
      console.log(
        challenge.name + '     ' + new Date(challenge.startDate as string)
      );
      console.log(challenge.startDate);
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

  // private calculateChallengeStatusCounts(
  //   challenges: Challenge[],
  //   years: string[]
  // ): {
  //   activeCounts: number[];
  //   completedCounts: number[];
  //   upcomingCounts: number[];
  // } {
  //   const activeCounts: number[] = [];
  //   const completedCounts: number[] = [];
  //   const upcomingCounts: number[] = [];

  //   years.forEach((year) => {
  //     const yearNumber = parseInt(year, 10);
  //     const activeChallenges = challenges.filter(
  //       (challenge) =>
  //         new Date(challenge.startDate as string).getFullYear() <= yearNumber &&
  //         new Date(challenge.endDate as string).getFullYear() > yearNumber
  //     ).length;

  //     const completedChallenges = challenges.filter(
  //       (challenge) =>
  //         new Date(challenge.endDate as string).getFullYear() === yearNumber
  //     ).length;

  //     const upcomingChallenges = challenges.filter(
  //       (challenge) =>
  //         new Date(challenge.startDate as string).getFullYear() > yearNumber &&
  //         new Date(challenge.startDate as string).getFullYear() <=
  //           yearNumber + 1
  //     ).length;

  //     activeCounts.push(activeChallenges);
  //     completedCounts.push(completedChallenges);
  //     upcomingCounts.push(upcomingChallenges);
  //   });

  //   return {
  //     activeCounts,
  //     completedCounts,
  //     upcomingCounts,
  //   };
  // }

  // onLegndChange(event: any) {
  //   console.log(event.name);
  // }
}
