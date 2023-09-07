import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
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
import { Observable, Subscription, map } from 'rxjs';
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
  constructor(
    private homeDataService: HomeDataService,
    private imageService: ImageService,
    private challengeService: ChallengeService,
    private organizationService: OrganizationService
  ) {}

  private chartDataSubscription: Subscription | undefined;

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
                name: 'Total Challenges Tracked',
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
                // make bar plot rise from left to right instead of rising all together in the same time
                animationDelay: (dataIndex: number) => dataIndex * 100,
              },
            ],
          })
      );
  }

  ngOnDestroy() {
    if (this.chartDataSubscription) {
      this.chartDataSubscription.unsubscribe();
    }
  }
}
