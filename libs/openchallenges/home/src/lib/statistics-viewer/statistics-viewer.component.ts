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
      /* Chart code */
      // Create root element
      // https://www.amcharts.com/docs/v5/getting-started/#Root_element
      const root = am5.Root.new('chartdiv');

      // Set themes
      // https://www.amcharts.com/docs/v5/concepts/themes/
      root.setThemes([am5ThemesAnimated.new(root)]);

      // Create chart
      // https://www.amcharts.com/docs/v5/charts/xy-chart/
      const chart = root.container.children.push(
        am5xy.XYChart.new(root, {
          panX: false,
          panY: false,
          wheelX: 'panX',
          wheelY: 'zoomX',
          layout: root.verticalLayout,
        })
      );

      // Add scrollbar
      // https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
      chart.set(
        'scrollbarX',
        am5.Scrollbar.new(root, {
          orientation: 'horizontal',
        })
      );

      const data = [
        {
          year: '2016',
          income: 23.5,
          expenses: 21.1,
        },
        {
          year: '2017',
          income: 26.2,
          expenses: 30.5,
        },
        {
          year: '2018',
          income: 30.1,
          expenses: 34.9,
        },
        {
          year: '2019',
          income: 29.5,
          expenses: 31.1,
        },
        {
          year: '2020',
          income: 30.6,
          expenses: 28.2,
          strokeSettings: {
            stroke: chart.get('colors')?.getIndex(1),
            strokeWidth: 3,
            strokeDasharray: [5, 5],
          },
        },
        {
          year: '2021',
          income: 34.1,
          expenses: 32.9,
          columnSettings: {
            strokeWidth: 1,
            strokeDasharray: [5],
            fillOpacity: 0.2,
          },
          info: '(projection)',
        },
      ];

      // Create axes
      // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
      const xRenderer = am5xy.AxisRendererX.new(root, {});
      const xAxis = chart.xAxes.push(
        am5xy.CategoryAxis.new(root, {
          categoryField: 'year',
          renderer: xRenderer,
          tooltip: am5.Tooltip.new(root, {}),
        })
      );
      xRenderer.grid.template.setAll({
        location: 1,
      });

      xAxis.data.setAll(data);

      const yAxis = chart.yAxes.push(
        am5xy.ValueAxis.new(root, {
          min: 0,
          extraMax: 0.1,
          renderer: am5xy.AxisRendererY.new(root, {
            strokeOpacity: 0.1,
          }),
        })
      );

      // Add series
      // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
      const series1 = chart.series.push(
        am5xy.ColumnSeries.new(root, {
          name: 'Income',
          xAxis,
          yAxis,
          valueYField: 'income',
          categoryXField: 'year',
          tooltip: am5.Tooltip.new(root, {
            pointerOrientation: 'horizontal',
            labelText: '{name} in {categoryX}: {valueY} {info}',
          }),
        })
      );

      series1.columns.template.setAll({
        tooltipY: am5.percent(10),
        templateField: 'columnSettings',
      });

      series1.data.setAll(data);

      const series2 = chart.series.push(
        am5xy.LineSeries.new(root, {
          name: 'Expenses',
          xAxis,
          yAxis,
          valueYField: 'expenses',
          categoryXField: 'year',
          tooltip: am5.Tooltip.new(root, {
            pointerOrientation: 'horizontal',
            labelText: '{name} in {categoryX}: {valueY} {info}',
          }),
        })
      );

      series2.strokes.template.setAll({
        strokeWidth: 3,
        templateField: 'strokeSettings',
      });

      series2.data.setAll(data);

      series2.bullets.push(function () {
        return am5.Bullet.new(root, {
          sprite: am5.Circle.new(root, {
            strokeWidth: 3,
            stroke: series2.get('stroke'),
            radius: 5,
            fill: root.interfaceColors.get('background'),
          }),
        });
      });

      chart.set('cursor', am5xy.XYCursor.new(root, {}));

      // Add legend
      // https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
      const legend = chart.children.push(
        am5.Legend.new(root, {
          centerX: am5.p50,
          x: am5.p50,
        })
      );
      legend.data.setAll(chart.series.values);

      // Make stuff animate on load
      // https://www.amcharts.com/docs/v5/concepts/animations/
      chart.appear(1000, 100);
      series1.appear();
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
