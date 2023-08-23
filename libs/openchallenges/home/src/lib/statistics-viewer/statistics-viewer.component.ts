import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  Component,
  Inject,
  // OnInit,
  NgZone,
  PLATFORM_ID,
  OnDestroy,
} from '@angular/core';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { HomeDataService } from '../home-data-service';

// amCharts imports
import * as am5 from '@amcharts/amcharts5';
import * as am5xy from '@amcharts/amcharts5/xy';
import am5ThemesAnimated from '@amcharts/amcharts5/themes/Animated';

@Component({
  selector: 'openchallenges-statistics-viewer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent implements OnDestroy {
  constructor(
    private challengeService: ChallengeService,
    private homeDataService: HomeDataService,
    @Inject(PLATFORM_ID) private platformId: any,
    private zone: NgZone
  ) {}

  // ngOnInit() {
  //   this.homeDataService
  //     .getAllChallenges()
  //     .subscribe((challenges) => console.log(challenges));
  // }

  private root!: am5.Root;

  // Run the function only in the browser
  browserOnly(f: () => void) {
    if (isPlatformBrowser(this.platformId)) {
      this.zone.runOutsideAngular(() => {
        f();
      });
    }
  }

  ngAfterViewInit() {
    // Chart code goes in here
    this.browserOnly(() => {
      const root = am5.Root.new('chartdiv');

      root.setThemes([am5ThemesAnimated.new(root)]);

      const chart = root.container.children.push(
        am5xy.XYChart.new(root, {
          panY: false,
          layout: root.verticalLayout,
        })
      );

      // Define data
      const data = [
        {
          category: 'Research',
          value1: 1000,
          value2: 588,
        },
        {
          category: 'Marketing',
          value1: 1200,
          value2: 1800,
        },
        {
          category: 'Sales',
          value1: 850,
          value2: 1230,
        },
      ];

      // Create Y-axis
      const yAxis = chart.yAxes.push(
        am5xy.ValueAxis.new(root, {
          renderer: am5xy.AxisRendererY.new(root, {}),
        })
      );

      // Create X-Axis
      const xAxis = chart.xAxes.push(
        am5xy.CategoryAxis.new(root, {
          renderer: am5xy.AxisRendererX.new(root, {}),
          categoryField: 'category',
        })
      );
      xAxis.data.setAll(data);

      // Create series
      const series1 = chart.series.push(
        am5xy.ColumnSeries.new(root, {
          name: 'Series',
          xAxis,
          yAxis,
          valueYField: 'value1',
          categoryXField: 'category',
        })
      );
      series1.data.setAll(data);

      const series2 = chart.series.push(
        am5xy.ColumnSeries.new(root, {
          name: 'Series',
          xAxis,
          yAxis,
          valueYField: 'value2',
          categoryXField: 'category',
        })
      );
      series2.data.setAll(data);

      // Add legend
      const legend = chart.children.push(am5.Legend.new(root, {}));
      legend.data.setAll(chart.series.values);

      // Add cursor
      chart.set('cursor', am5xy.XYCursor.new(root, {}));

      this.root = root;
    });
  }

  ngOnDestroy() {
    // Clean up chart when the component is removed
    this.browserOnly(() => {
      if (this.root) {
        this.root.dispose();
      }
    });
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
