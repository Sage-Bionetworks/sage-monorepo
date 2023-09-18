import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  ChallengeService,
  Image,
  ImageHeight,
  ImageService,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { HomeDataService } from '../home-data-service';
import { Observable, Subscription, map } from 'rxjs';
import { Router } from '@angular/router';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxTypedJsModule } from 'ngx-typed-js';
import { InputTextModule } from 'primeng/inputtext';
import { CountUpModule } from 'ngx-countup';
import { NgxEchartsModule, NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { EChartsOption } from 'echarts';

@Component({
  selector: 'openchallenges-challenge-search',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NgxTypedJsModule,
    InputTextModule,
    CountUpModule,
    NgxEchartsModule,
  ],
  providers: [
    {
      provide: NGX_ECHARTS_CONFIG,
      useFactory: () => ({ echarts: () => import('echarts') }),
    },
  ],
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent implements OnInit, OnDestroy {
  private chartDataSubscription: Subscription | undefined;
  private imgHeight = ImageHeight._140px;
  public isPlatformServer = false;
  public searchOC$: Observable<Image> | undefined;
  searchTerms!: string | undefined;
  challengeCount$: Observable<number> | undefined;
  orgCount$: Observable<number> | undefined;
  chartOptions!: EChartsOption;
  reanimateOnClick = false;
  challengeImg$: Observable<Image> | undefined;
  orgImg$: Observable<Image> | undefined;
  userImg$: Observable<Image> | undefined;

  constructor(
    private readonly configService: ConfigService,
    private router: Router,
    private homeDataService: HomeDataService,
    private imageService: ImageService,
    private challengeService: ChallengeService,
    private organizationService: OrganizationService
  ) {
    this.isPlatformServer = this.configService.config.isPlatformServer;
  }

  ngOnInit() {
    this.searchOC$ = this.imageService.getImage({
      objectKey: 'home-search.svg',
    });
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

  onSearch(): void {
    this.router.navigateByUrl('/challenge?searchTerms=' + this.searchTerms);
  }
}
