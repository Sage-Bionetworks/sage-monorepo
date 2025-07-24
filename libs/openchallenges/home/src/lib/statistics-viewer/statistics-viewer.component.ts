import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { HomeDataService } from '../home-data-service';
import { NgxEchartsModule, NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { EChartsOption } from 'echarts';
import { CountUpModule } from 'ngx-countup';
import { Subscription } from 'rxjs';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'openchallenges-statistics-viewer',
  imports: [RouterModule, CountUpModule, NgxEchartsModule],
  providers: [
    {
      provide: NGX_ECHARTS_CONFIG,
      useFactory: () => ({ echarts: () => import('echarts') }),
    },
  ],
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent implements OnInit, OnDestroy {
  private readonly homeDataService = inject(HomeDataService);

  private chartDataSubscription: Subscription | undefined;

  chartOptions!: EChartsOption;

  ngOnInit() {
    // update plot's data
    this.chartDataSubscription = this.homeDataService.getChallengesPerYear().subscribe(
      (res) =>
        (this.chartOptions = {
          textStyle: {
            fontWeight: 'normal',
            fontFamily: 'Lato, sans-serif',
            color: '#000',
          },
          xAxis: {
            type: 'category',
            data: res.years,
            axisLabel: { rotate: 45, fontSize: '15px' },
          },
          yAxis: [
            {
              type: 'value',
              name: '',
              nameTextStyle: {
                lineHeight: 56,
              },
            },
          ],
          grid: {
            containLabel: true,
          },
          series: [
            {
              name: 'Total challenges',
              data: res.challengeCounts,
              type: 'bar',
              itemStyle: {
                color: '#afa0fe',
              },
              // disable default clicking
              silent: true,
              // make bar plot rise from left to right
              // instead of rising all together in the same time
              animationDelay: (dataIndex: number) => dataIndex * 100,
            },
          ],
        }),
    );
  }

  // this.challengeService
  // .listChallenges({ pageSize: 1000 })
  // .subscribe((page) => console.log(this.processData(page.challenges)));

  // private processData(challenges: Challenge[]): {
  //   years: string[];
  //   cumulativeChallengeCounts: number[];
  // } {
  //   const dataByYear: { [year: string]: number } = {};
  //   const cumulativeChallengeCounts: number[] = [];
  //   const filteredChallenges = challenges.filter(
  //     (challenge) => challenge.startDate !== null
  //   );
  //   filteredChallenges.forEach((challenge) => {
  //     const startYear = new Date(challenge.startDate as string)
  //       .getFullYear()
  //       .toString();
  //     dataByYear[startYear] = (dataByYear[startYear] || 0) + 1;
  //   });

  //   const years = Object.keys(dataByYear);
  //   years.sort(); // Sort years in ascending order
  //   let cumulativeSum = 0;
  //   years.forEach((year) => {
  //     cumulativeSum += dataByYear[year];
  //     cumulativeChallengeCounts.push(cumulativeSum);
  //   });
  //   return {
  //     years,
  //     cumulativeChallengeCounts,
  //   };
  // }

  ngOnDestroy() {
    if (this.chartDataSubscription) {
      this.chartDataSubscription.unsubscribe();
    }
  }
}
