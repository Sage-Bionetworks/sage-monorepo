import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  ChallengeService,
  Image,
  ImageHeight,
  ImageService,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { HomeDataService } from '../home-data-service';
import { NgxEchartsModule, NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { EChartsOption } from 'echarts';
import { CountUpModule } from 'ngx-countup';
import { Observable, map } from 'rxjs';
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
export class StatisticsViewerComponent implements OnInit {
  constructor(
    private homeDataService: HomeDataService,
    private imageService: ImageService,
    private challengeService: ChallengeService,
    private organizationService: OrganizationService
  ) {}

  chartOptions!: EChartsOption;
  reanimateOnClick = false;
  challengeImg$: Observable<Image> | undefined;
  orgImg$: Observable<Image> | undefined;
  userImg$: Observable<Image> | undefined;

  private imgHeight = ImageHeight._140px;

  challengeCount$: Observable<number> | undefined;
  orgCount$: Observable<number> | undefined;
  ngOnInit() {
    this.challengeImg$ = this.imageService.getImage({
      objectKey: 'home-challenges.svg',
      height: this.imgHeight,
    });
    this.orgImg$ = this.imageService.getImage({
      objectKey: 'home-hosts.svg',
      height: this.imgHeight,
    });
    this.userImg$ = this.imageService.getImage({
      objectKey: 'home-users.svg',
      height: this.imgHeight,
    });

    this.challengeCount$ = this.challengeService
      .listChallenges({
        pageNumber: 1,
        pageSize: 1,
      })
      .pipe(map((page) => page.totalElements));

    this.orgCount$ = this.organizationService
      .listOrganizations({
        pageNumber: 1,
        pageSize: 1,
      })
      .pipe(map((page) => page.totalElements));

    // update plot's data
    this.homeDataService.getChallengesPerYear().subscribe(
      (res) =>
        (this.chartOptions = {
          title: {
            text: 'The Rise of Challenges',
            left: 'center',
            textStyle: {
              fontWeight: 'normal',
              fontFamily: 'Lato, sans-serif',
            },
          },
          // tooltip: {
          //   trigger: 'axis',
          //   axisPointer: {
          //     type: 'cross',
          //   },
          // },
          xAxis: {
            type: 'category',
            data: res.years,
          },
          yAxis: [
            {
              type: 'value',
              name: 'Total Challenges Tracked',
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
              // make bar plot rise from left to right instead of rising all together in the same time
              animationDelay: (dataIndex: number) => dataIndex * 100,
            },
          ],
        })
    );
  }
}
