import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { EChartsOption } from 'echarts';
import * as echarts from 'echarts';
import { CountUpModule } from 'ngx-countup';
import { HomeDataService } from '../home-data-service';
// import { Observable, map } from 'rxjs';

@Component({
  selector: 'openchallenges-statistics-viewer',
  standalone: true,
  imports: [CommonModule, CountUpModule],
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent implements OnInit {
  // challengeImg$: Observable<Image> | undefined;
  // orgImg$: Observable<Image> | undefined;
  // userImg$: Observable<Image> | undefined;

  // private imgHeight = ImageHeight._140px;

  // challengeCount$: Observable<number> | undefined;
  // orgCount$: Observable<number> | undefined;

  constructor(
    // private organizationService: OrganizationService,
    // private imageService: ImageService
    private challengeService: ChallengeService,
    private homeDataService: HomeDataService
  ) {}

  option!: EChartsOption;
  ngOnInit() {
    const chartDom = document.getElementById('statistics')!;
    const myChart = echarts.init(chartDom);

    this.homeDataService.getAllChallenges().subscribe((challenges) => {
      const dataByYear = this.processData(challenges);

      console.log(dataByYear);
      this.option = {
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

      this.option && myChart.setOption(this.option);
    });

    // this.challengeImg$ = this.imageService.getImage({
    //   objectKey: 'home-challenges.svg',
    //   height: this.imgHeight,
    // });
    // this.orgImg$ = this.imageService.getImage({
    //   objectKey: 'home-hosts.svg',
    //   height: this.imgHeight,
    // });
    // this.userImg$ = this.imageService.getImage({
    //   objectKey: 'home-users.svg',
    //   height: this.imgHeight,
    // });
    // this.challengeCount$ = this.challengeService
    //   .listChallenges({
    //     pageNumber: 1,
    //     pageSize: 1,
    //   })
    //   .pipe(map((page) => page.totalElements));
    // this.orgCount$ = this.organizationService
    //   .listOrganizations({
    //     pageNumber: 1,
    //     pageSize: 1,
    //   })
    //   .pipe(map((page) => page.totalElements));
  }

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
