import { Component, OnInit } from '@angular/core';
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

  // constructor(
  //   private organizationService: OrganizationService,
  //   private challengeService: ChallengeService,
  //   private imageService: ImageService
  // ) {}

  option!: EChartsOption;

  ngOnInit() {
    const chartDom = document.getElementById('statistics')!;
    const myChart = echarts.init(chartDom);
    this.option = {
      xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          data: [150, 230, 224, 218, 135, 147, 260],
          type: 'line',
        },
      ],
    };

    this.option && myChart.setOption(this.option);
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
}
