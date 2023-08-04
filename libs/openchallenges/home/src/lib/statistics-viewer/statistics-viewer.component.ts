import { Component, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
// import {
//   ChallengeService,
//   Image,
//   ImageHeight,
//   ImageService,
//   OrganizationService,
// } from '@sagebionetworks/openchallenges/api-client-angular';
// import { Observable, map } from 'rxjs';
import { EChartsOption } from 'echarts';
import * as echarts from 'echarts';

@Component({
  selector: 'openchallenges-statistics-viewer',
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
    private challengeService: ChallengeService
  ) {}

  option!: EChartsOption;
  ngOnInit() {
    const chartDom = document.getElementById('statistics')!;
    const myChart = echarts.init(chartDom);

    this.challengeService
      .listChallenges({ pageSize: 1000 })
      .subscribe((page) => {
        const dataByYear = this.processData(page.challenges);

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
