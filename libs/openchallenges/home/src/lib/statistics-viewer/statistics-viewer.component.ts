import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { HomeDataService } from '../home-data-service';
import { NgxEchartsModule, NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { EChartsOption } from 'echarts';
import { CountUpModule } from 'ngx-countup';
import { Subscription } from 'rxjs';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'openchallenges-statistics-viewer',
  standalone: true,
  imports: [CommonModule, RouterModule, CountUpModule, NgxEchartsModule],
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
  constructor(private homeDataService: HomeDataService) {}

  private chartDataSubscription: Subscription | undefined;

  chartOptions!: EChartsOption;
  undatedChallengeCount = 0;

  ngOnInit() {
    // update plot's data
    this.chartDataSubscription = this.homeDataService
      .getChallengesPerYear()
      .subscribe(
        (res) =>
          (this.chartOptions = {
            textStyle: {
              fontWeight: 'normal',
              fontFamily: 'Lato, sans-serif',
              color: '#000',
            },
            // title: {
            //   text: 'The Rise of Challenges',
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
              data: res.years,
              axisLabel: { fontSize: '1em' },
            },
            yAxis: [
              {
                type: 'value',
                name: '',
                axisLabel: { fontSize: '1em' },
                nameTextStyle: {
                  fontSize: '1.1em',
                  lineHeight: 56,
                },
              },
            ],
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
            graphic: res.undatedChallengeCount
              ? {
                  elements: [
                    {
                      type: 'text',
                      style: {
                        text:
                          `*An additional ${res.undatedChallengeCount} challenges ` +
                          `without known start dates are not displayed.`,
                        fill: '#888',
                        fontSize: '1em',
                      },
                      left: '25%',
                      bottom: 5,
                    },
                  ],
                }
              : undefined,
          })
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
