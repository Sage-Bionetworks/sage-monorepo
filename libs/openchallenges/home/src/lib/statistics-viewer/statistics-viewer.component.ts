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
import * as echarts from 'echarts';
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

      this.chartOptions = {
        title: {
          text: 'Growth of Challenges',
          left: 'center',
        },
        xAxis: {
          type: 'category',
          data: dataByYear.years,
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            data: dataByYear.cumulativeChallengeCounts,
            type: 'line',
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
}
