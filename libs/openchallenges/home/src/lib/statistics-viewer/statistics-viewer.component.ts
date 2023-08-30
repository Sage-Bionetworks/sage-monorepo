import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular';
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
  constructor(private homeDataService: HomeDataService) {}

  chartOptions!: EChartsOption;

  ngOnInit() {
    this.homeDataService.getAllChallenges().subscribe((challenges) => {
      const dataByYear = this.processData(challenges);

      this.chartOptions = {
        // title: {
        //   text: 'Growth of Challenges',
        //   left: 'center',
        // },
        // tooltip: {
        //   trigger: 'axis',
        //   axisPointer: {
        //     type: 'cross',
        //   },
        // },
        xAxis: {
          type: 'category',
          data: dataByYear.years,
        },
        yAxis: [
          {
            type: 'value',
            name: 'Count',
          },
        ],
        series: [
          {
            name: 'Total challenges',
            data: dataByYear.challengeCountsPerYear,
            type: 'bar',
            silent: true, // disable default clicking
          },
        ],
      };
    });
  }

  private processData(challenges: Challenge[]): {
    years: string[];
    challengeCountsPerYear: number[];
  } {
    const dataByYear: { [year: string]: number } = {};
    // const cumulativeChallengeCounts: number[] = [];
    const filteredChallenges = challenges.filter(
      (challenge) => challenge.startDate !== null
    );
    filteredChallenges.forEach((challenge) => {
      const startYear = new Date(challenge.startDate as string)
        .getFullYear()
        .toString();
      dataByYear[startYear] = (dataByYear[startYear] || 0) + 1;
    });

    const years = Object.keys(dataByYear);
    years.sort(); // Sort years in ascending order

    // let cumulativeSum = 0;
    // years.forEach((year) => {
    //   cumulativeSum += dataByYear[year];
    //   cumulativeChallengeCounts.push(cumulativeSum);
    // });
    const challengeCountsPerYear = years.map((year) => dataByYear[year]);
    return {
      years,
      challengeCountsPerYear,
    };
  }
}
