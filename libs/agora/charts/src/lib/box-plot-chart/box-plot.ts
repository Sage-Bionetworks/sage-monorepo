/* eslint-disable @typescript-eslint/no-this-alias */
/* eslint-disable @typescript-eslint/ban-ts-comment */
// @ts-nocheck

import { scaleBand, scaleLinear } from 'd3';
import { select } from 'd3';
import { min, max, ascending, quantile, range } from 'd3';
import { timerFlush } from 'd3';

import { CoordinateGridMixin } from 'dc';
import { transition } from 'dc';
import { units } from 'dc';
import { utils } from 'dc';
import { d3compat } from 'dc/src/core/config';

export const d3Box = function () {
  let width = 1;
  let height = 1;
  let duration = 0;
  const delay = 0;
  let domain = null;
  let value = Number;
  let whiskers = boxWhiskers;
  let quartiles = boxQuartiles;
  let tickFormat = null;

  // Enhanced attributes
  let renderDataPoints = false;
  const dataRadius = 3;
  let dataOpacity = 0.3;
  let dataWidthPortion = 0.8;
  let renderTitle = false;
  let showOutliers = true;
  let boldOutlier = false;

  // For each small multiple…
  function box(g) {
    g.each(function (_data, index) {
      const data = _data.map(value).sort(ascending);
      const _g = select(this);
      const n = data.length;
      let min;
      let max;

      // Leave if there are no items.
      if (data.length === 0) {
        return;
      }

      // Compute quartiles. Must return exactly 3 elements.
      // const quartileData = (data.quartiles = quartiles(data));
      // ** Agora custom code
      data.quartiles = quartiles(data);
      const quartileData = _data.quartiles ? _data.quartiles : data.quartiles;
      // Agora custom code **

      // Compute whiskers. Must return exactly 2 elements, or null.
      const whiskerIndices = whiskers && whiskers.call(this, data, index),
        whiskerData = whiskerIndices && whiskerIndices.map((_i) => data[_i]);

      // Compute outliers. If no whiskers are specified, all data are 'outliers'.
      // We compute the outliers as indices, so that we can join across transitions!
      const outlierIndices = whiskerIndices
        ? range(0, whiskerIndices[0]).concat(range(whiskerIndices[1] + 1, n))
        : range(n);

      // Determine the maximum value based on if outliers are shown
      if (showOutliers) {
        min = data[0];
        max = data[n - 1];
      } else {
        min = data[whiskerIndices[0]];
        max = data[whiskerIndices[1]];
      }
      const pointIndices = range(whiskerIndices[0], whiskerIndices[1] + 1);

      // Compute the new x-scale.
      const x1 = scaleLinear()
        .domain((domain && domain.call(this, data, index)) || [min, max])
        .range([height, 0]);

      // Retrieve the old x-scale, if this is an update.
      const x0 = this.__chart__ || scaleLinear().domain([0, Infinity]).range(x1.range());

      // Stash the new scale.
      this.__chart__ = x1;

      // Note: the box, median, and box tick elements are fixed in number,
      // so we only have to handle enter and update. In contrast, the outliers
      // and other elements are variable, so we need to exit them! Variable
      // elements also fade in and out.

      // Update center line: the vertical line spanning the whiskers.
      const center = _g.selectAll('line.center').data(whiskerData ? [whiskerData] : []);

      center
        .enter()
        .insert('line', 'rect')
        .attr('class', 'center')
        .attr('x1', width / 2)
        .attr('y1', (d) => x0(d[0]))
        .attr('x2', width / 2)
        .attr('y2', (d) => x0(d[1]))
        .style('opacity', 1e-6)
        .transition()
        .duration(duration)
        .delay(delay)
        .style('opacity', 1)
        .attr('y1', (d) => x1(d[0]))
        .attr('y2', (d) => x1(d[1]));

      center
        .transition()
        .duration(duration)
        .delay(delay)
        .style('opacity', 1)
        .attr('x1', width / 2)
        .attr('x2', width / 2)
        .attr('y1', (d) => x1(d[0]))
        .attr('y2', (d) => x1(d[1]));

      center
        .exit()
        .transition()
        .duration(duration)
        .delay(delay)
        .style('opacity', 1e-6)
        .attr('y1', (d) => x1(d[0]))
        .attr('y2', (d) => x1(d[1]))
        .remove();

      // Update innerquartile box.
      const _box = _g.selectAll('rect.box').data([quartileData]);

      _box
        .enter()
        .append('rect')
        .attr('class', 'box')
        .attr('x', 0)
        .attr('y', (d) => x0(d[2]))
        .attr('width', width)
        .attr('height', (d) => x0(d[0]) - x0(d[2]))
        // ** Agora custom code
        .attr('rx', 8)
        .style('fill-opacity', renderDataPoints ? 0.1 : 1)
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('y', (d) => x1(d[2]))
        .attr('height', (d) => x1(d[0]) - x1(d[2]));

      _box
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('width', width)
        .attr('y', (d) => x1(d[2]))
        .attr('height', (d) => x1(d[0]) - x1(d[2]));

      // Update median line.
      const medianLine = _g.selectAll('line.median').data([quartileData[1]]);

      medianLine
        .enter()
        .append('line')
        .attr('class', 'median')
        .attr('x1', 0)
        .attr('y1', x0)
        .attr('x2', width)
        .attr('y2', x0)
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('y1', x1)
        .attr('y2', x1);

      medianLine
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('x1', 0)
        .attr('x2', width)
        .attr('y1', x1)
        .attr('y2', x1);

      // Update whiskers.
      const whisker = _g.selectAll('line.whisker').data(whiskerData || []);

      whisker
        .enter()
        .insert('line', 'circle, text')
        .attr('class', 'whisker')
        .attr('x1', 0)
        .attr('y1', x0)
        .attr('x2', width)
        .attr('y2', x0)
        .style('opacity', 1e-6)
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('y1', x1)
        .attr('y2', x1)
        .style('opacity', 1);

      whisker
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('x1', 0)
        .attr('x2', width)
        .attr('y1', x1)
        .attr('y2', x1)
        .style('opacity', 1);

      whisker
        .exit()
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('y1', x1)
        .attr('y2', x1)
        .style('opacity', 1e-6)
        .remove();

      // Update outliers.
      if (showOutliers) {
        const outlierClass = boldOutlier ? 'outlierBold' : 'outlier';
        const outlierSize = boldOutlier ? 3 : 5;

        let outlierX;
        if (boldOutlier) {
          outlierX = () => {
            return Math.floor(
              Math.random() * (width * dataWidthPortion) +
                1 +
                (width - width * dataWidthPortion) / 2,
            );
          };
        } else {
          outlierX = () => {
            return width / 2;
          };
        }

        const outlier = _g.selectAll(`circle.${outlierClass}`).data(outlierIndices, Number);

        outlier
          .enter()
          .insert('circle', 'text')
          .attr('class', outlierClass)
          .attr('r', outlierSize)
          .attr('cx', outlierX)
          .attr('cy', (i) => x0(data[i]))
          .style('opacity', 1e-6)
          .transition()
          .duration(duration)
          .delay(delay)
          .attr('cy', (i) => x1(data[i]))
          .style('opacity', 0.6);

        if (renderTitle) {
          outlier.selectAll('title').remove();
          outlier.append('title').text((i) => data[i]);
        }

        outlier
          .transition()
          .duration(duration)
          .delay(delay)
          .attr('cx', outlierX)
          .attr('cy', (i) => x1(data[i]))
          .style('opacity', 0.6);

        outlier
          .exit()
          .transition()
          .duration(duration)
          .delay(delay)
          .attr('cy', 0) //function (i) { return x1(d[i]); })
          .style('opacity', 1e-6)
          .remove();
      }

      // Update Values
      if (renderDataPoints) {
        const point = _g.selectAll('circle.data').data(pointIndices);

        point
          .enter()
          .insert('circle', 'text')
          .attr('class', 'data')
          .attr('r', dataRadius)
          .attr('cx', () =>
            Math.floor(
              Math.random() * (width * dataWidthPortion) +
                1 +
                (width - width * dataWidthPortion) / 2,
            ),
          )
          .attr('cy', (i) => x0(data[i]))
          .style('opacity', 1e-6)
          .transition()
          .duration(duration)
          .delay(delay)
          .attr('cy', (i) => x1(data[i]))
          .style('opacity', dataOpacity);

        if (renderTitle) {
          point.selectAll('title').remove();
          point.append('title').text((i) => data[i]);
        }

        point
          .transition()
          .duration(duration)
          .delay(delay)
          .attr('cx', () =>
            Math.floor(
              Math.random() * (width * dataWidthPortion) +
                1 +
                (width - width * dataWidthPortion) / 2,
            ),
          )
          .attr('cy', (i) => x1(data[i]))
          .style('opacity', dataOpacity);

        point
          .exit()
          .transition()
          .duration(duration)
          .delay(delay)
          .attr('cy', 0)
          .style('opacity', 1e-6)
          .remove();
      }

      // Compute the tick format.
      const format = tickFormat || x1.tickFormat(8);

      // Update box ticks.
      const boxTick = _g.selectAll('text.box').data(quartileData);

      boxTick
        .enter()
        .append('text')
        .attr('class', 'box')
        .attr('dy', '.3em')
        .attr('dx', (d, i) => (i & 1 ? 6 : -6))
        .attr('x', (d, i) => (i & 1 ? width : 0))
        .attr('y', x0)
        .attr('text-anchor', (d, i) => (i & 1 ? 'start' : 'end'))
        .text(format)
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('y', x1);

      boxTick
        .transition()
        .duration(duration)
        .delay(delay)
        .text(format)
        .attr('x', (d, i) => (i & 1 ? width : 0))
        .attr('y', x1);

      // Update whisker ticks. These are handled separately from the box
      // ticks because they may or may not exist, and we want don't want
      // to join box ticks pre-transition with whisker ticks post-.
      const whiskerTick = _g.selectAll('text.whisker').data(whiskerData || []);

      whiskerTick
        .enter()
        .append('text')
        .attr('class', 'whisker')
        .attr('dy', '.3em')
        .attr('dx', 6)
        .attr('x', width)
        .attr('y', x0)
        .text(format)
        .style('opacity', 1e-6)
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('y', x1)
        .style('opacity', 1);

      whiskerTick
        .transition()
        .duration(duration)
        .delay(delay)
        .text(format)
        .attr('x', width)
        .attr('y', x1)
        .style('opacity', 1);

      whiskerTick
        .exit()
        .transition()
        .duration(duration)
        .delay(delay)
        .attr('y', x1)
        .style('opacity', 1e-6)
        .remove();

      // Remove temporary quartiles element from within data array.
      delete data.quartiles;
    });
    timerFlush();
  }

  box.width = function (x) {
    if (!arguments.length) {
      return width;
    }
    width = x;
    return box;
  };

  box.height = function (x) {
    if (!arguments.length) {
      return height;
    }
    height = x;
    return box;
  };

  box.tickFormat = function (x) {
    if (!arguments.length) {
      return tickFormat;
    }
    tickFormat = x;
    return box;
  };

  box.showOutliers = function (x) {
    if (!arguments.length) {
      return showOutliers;
    }
    showOutliers = x;
    return box;
  };

  box.boldOutlier = function (x) {
    if (!arguments.length) {
      return boldOutlier;
    }
    boldOutlier = x;
    return box;
  };

  box.renderDataPoints = function (x) {
    if (!arguments.length) {
      return renderDataPoints;
    }
    renderDataPoints = x;
    return box;
  };

  box.renderTitle = function (x) {
    if (!arguments.length) {
      return renderTitle;
    }
    renderTitle = x;
    return box;
  };

  box.dataOpacity = function (x) {
    if (!arguments.length) {
      return dataOpacity;
    }
    dataOpacity = x;
    return box;
  };

  box.dataWidthPortion = function (x) {
    if (!arguments.length) {
      return dataWidthPortion;
    }
    dataWidthPortion = x;
    return box;
  };

  box.duration = function (x) {
    if (!arguments.length) {
      return duration;
    }
    duration = x;
    return box;
  };

  box.domain = function (x) {
    if (!arguments.length) {
      return domain;
    }
    domain = x === null ? x : typeof x === 'function' ? x : utils.constant(x);
    return box;
  };

  box.value = function (x) {
    if (!arguments.length) {
      return value;
    }
    value = x;
    return box;
  };

  box.whiskers = function (x) {
    if (!arguments.length) {
      return whiskers;
    }
    whiskers = x;
    return box;
  };

  box.quartiles = function (x) {
    if (!arguments.length) {
      return quartiles;
    }
    quartiles = x;
    return box;
  };

  return box;
};

function boxWhiskers(d) {
  return [0, d.length - 1];
}

function boxQuartiles(d) {
  return [quantile(d, 0.25), quantile(d, 0.5), quantile(d, 0.75)];
}

// Returns a function to compute the interquartile range.
function defaultWhiskersIQR(k) {
  return (d) => {
    const q1 = d.quartiles[0];
    const q3 = d.quartiles[2];
    const iqr = (q3 - q1) * k;

    let i = -1;
    let j = d.length;

    do {
      ++i;
    } while (d[i] < q1 - iqr);

    do {
      --j;
    } while (d[j] > q3 + iqr);

    return [i, j];
  };
}

/**
 * A box plot is a chart that depicts numerical data via their quartile ranges.
 *
 * Examples:
 * - {@link http://dc-js.github.io/dc.js/examples/boxplot-basic.html Boxplot Basic example}
 * - {@link http://dc-js.github.io/dc.js/examples/boxplot-enhanced.html Boxplot Enhanced example}
 * - {@link http://dc-js.github.io/dc.js/examples/boxplot-render-data.html Boxplot Render Data example}
 * - {@link http://dc-js.github.io/dc.js/examples/boxplot-time.html Boxplot time example}
 * @mixes CoordinateGridMixin
 */
export class AgoraBoxPlot extends CoordinateGridMixin {
  /**
   * Create a Box Plot.
   *
   * @example
   * // create a box plot under #chart-container1 element using the default global chart group
   * var boxPlot1 = new BoxPlot('#chart-container1');
   * // create a box plot under #chart-container2 element using chart group A
   * var boxPlot2 = new BoxPlot('#chart-container2', 'chartGroupA');
   * @param {String|node|d3.selection} parent - Any valid
   * {@link https://github.com/d3/d3-selection/blob/master/README.md#select d3 single selector} specifying
   * a dom block element such as a div; or a dom element or d3 selection.
   * @param {String} [chartGroup] - The name of the chart group this chart instance should be placed in.
   * Interaction with a chart will only trigger events and redraws within the chart's group.
   */
  constructor(parent, chartGroup?, options?) {
    super();

    this._whiskerIqrFactor = 1.5;
    this._whiskersIqr = defaultWhiskersIQR;
    this._whiskers = this._whiskersIqr(this._whiskerIqrFactor);

    this._box = d3Box();
    this._tickFormat = null;
    this._renderDataPoints = false;
    this._dataOpacity = 0.3;
    this._dataWidthPortion = 0.8;
    this._showOutliers = true;
    this._boldOutlier = false;

    // Used in yAxisMin and yAxisMax to add padding in pixel coordinates
    // so the min and max data points/whiskers are within the chart
    this._yRangePadding = 8;

    this._yAxisMin = options?.yAxisMin || undefined;
    this._yAxisMax = options?.yAxisMax || undefined;

    this._boxWidth = (innerChartWidth, xUnits) => {
      if (this.isOrdinal()) {
        return this.x().bandwidth();
      } else {
        return innerChartWidth / (1 + this.boxPadding()) / xUnits;
      }
    };

    // default to ordinal
    this.x(scaleBand());
    this.xUnits(units.ordinal);

    // valueAccessor should return an array of values that can be coerced into numbers
    // or if data is overloaded for a static array of arrays, it should be `Number`.
    // Empty arrays are not included.
    this.data((group) =>
      group
        .all()
        .map((d) => {
          d.map = (accessor) => accessor.call(d, d);
          return d;
        })
        .filter((d) => {
          const values = this.valueAccessor()(d);
          return values.length !== 0;
        }),
    );

    this.boxPadding(0.8);
    this.outerPadding(0.5);

    this.anchor(parent, chartGroup);
  }

  /**
   * Get or set the spacing between boxes as a fraction of box size. Valid values are within 0-1.
   * See the {@link https://github.com/d3/d3-scale/blob/master/README.md#scaleBand d3 docs}
   * for a visual description of how the padding is applied.
   * @see {@link https://github.com/d3/d3-scale/blob/master/README.md#scaleBand d3.scaleBand}
   * @param {Number} [padding=0.8]
   * @returns {Number|BoxPlot}
   */
  boxPadding(padding) {
    if (!arguments.length) {
      return this._rangeBandPadding();
    }
    return this._rangeBandPadding(padding);
  }

  /**
   * Get or set the outer padding on an ordinal box chart. This setting has no effect on non-ordinal charts
   * or on charts with a custom {@link BoxPlot#boxWidth .boxWidth}. Will pad the width by
   * `padding * barWidth` on each side of the chart.
   * @param {Number} [padding=0.5]
   * @returns {Number|BoxPlot}
   */
  outerPadding(padding) {
    if (!arguments.length) {
      return this._outerRangeBandPadding();
    }
    return this._outerRangeBandPadding(padding);
  }

  /**
   * Get or set the numerical width of the boxplot box. The width may also be a function taking as
   * parameters the chart width excluding the right and left margins, as well as the number of x
   * units.
   * @example
   * // Using numerical parameter
   * chart.boxWidth(10);
   * // Using function
   * chart.boxWidth((innerChartWidth, xUnits) { ... });
   * @param {Number|Function} [boxWidth=0.5]
   * @returns {Number|Function|BoxPlot}
   */
  boxWidth(boxWidth) {
    if (!arguments.length) {
      return this._boxWidth;
    }
    this._boxWidth = typeof boxWidth === 'function' ? boxWidth : utils.constant(boxWidth);
    return this;
  }

  _boxTransform(d, i) {
    const xOffset = this.x()(this.keyAccessor()(d, i));
    return `translate(${xOffset}, 0)`;
  }

  _preprocessData() {
    if (this.elasticX()) {
      this.x().domain([]);
    }
  }

  plotData() {
    this._calculatedBoxWidth = this._boxWidth(this.effectiveWidth(), this.xUnitCount());

    this._box
      .whiskers(this._whiskers)
      .width(this._calculatedBoxWidth)
      .height(this.effectiveHeight())
      .value(this.valueAccessor())
      .domain(this.y().domain())
      .duration(this.transitionDuration())
      .tickFormat(this._tickFormat)
      .renderDataPoints(this._renderDataPoints)
      .dataOpacity(this._dataOpacity)
      .dataWidthPortion(this._dataWidthPortion)
      .renderTitle(this.renderTitle())
      .showOutliers(this._showOutliers)
      .boldOutlier(this._boldOutlier);

    const boxesG = this.chartBodyG().selectAll('g.box').data(this.data(), this.keyAccessor());

    const boxesGEnterUpdate = this._renderBoxes(boxesG);
    this._updateBoxes(boxesGEnterUpdate);
    this._removeBoxes(boxesG);

    this.fadeDeselectedArea(this.filter());
  }

  _renderBoxes(boxesG) {
    const boxesGEnter = boxesG.enter().append('g');

    boxesGEnter
      .attr('class', 'box')
      .classed('dc-tabbable', this._keyboardAccessible)
      .attr('transform', (d, i) => this._boxTransform(d, i))
      .call(this._box)
      .on(
        'click',
        d3compat.eventHandler((d) => {
          this.filter(this.keyAccessor()(d));
          this.redrawGroup();
        }),
      )
      .selectAll('circle')
      .classed('dc-tabbable', this._keyboardAccessible);

    if (this._keyboardAccessible) {
      this._makeKeyboardAccessible(this.onClick);
    }

    return boxesGEnter.merge(boxesG);
  }

  _updateBoxes(boxesG) {
    const chart = this;
    transition(boxesG, this.transitionDuration(), this.transitionDelay())
      .attr('transform', (d, i) => this._boxTransform(d, i))
      .call(this._box)
      .each(function (d) {
        const color = chart.getColor(d, 0);
        select(this).select('rect.box').attr('fill', color);
        select(this).selectAll('circle.data').attr('fill', color);
      });
  }

  _removeBoxes(boxesG) {
    boxesG.exit().remove().call(this._box);
  }

  _minDataValue() {
    return min(this.data(), (e) => min(this.valueAccessor()(e)));
  }

  _maxDataValue() {
    return max(this.data(), (e) => max(this.valueAccessor()(e)));
  }

  _yAxisRangeRatio() {
    return (this._maxDataValue() - this._minDataValue()) / this.effectiveHeight();
  }

  onClick(d) {
    this.filter(this.keyAccessor()(d));
    this.redrawGroup();
  }

  fadeDeselectedArea(brushSelection) {
    const chart = this;
    if (this.hasFilter()) {
      if (this.isOrdinal()) {
        this.g()
          .selectAll('g.box')
          .each(function (d) {
            if (chart.isSelectedNode(d)) {
              chart.highlightSelected(this);
            } else {
              chart.fadeDeselected(this);
            }
          });
      } else {
        if (!(this.brushOn() || this.parentBrushOn())) {
          return;
        }
        const start = brushSelection[0];
        const end = brushSelection[1];
        this.g()
          .selectAll('g.box')
          .each(function (d) {
            const key = chart.keyAccessor()(d);
            if (key < start || key >= end) {
              chart.fadeDeselected(this);
            } else {
              chart.highlightSelected(this);
            }
          });
      }
    } else {
      this.g()
        .selectAll('g.box')
        .each(function () {
          chart.resetHighlight(this);
        });
    }
  }

  isSelectedNode(d) {
    return this.hasFilter(this.keyAccessor()(d));
  }

  yAxisMin() {
    const padding = this._yRangePadding * this._yAxisRangeRatio();
    let min = this._minDataValue();
    min = this._yAxisMin && this._yAxisMin < min ? this._yAxisMin : min - 0.2;
    return utils.subtract(min - padding, this.yAxisPadding());
  }

  yAxisMax() {
    const padding = this._yRangePadding * this._yAxisRangeRatio();
    let max = this._maxDataValue();
    max = this._yAxisMax && this._yAxisMax > max ? this._yAxisMax : max + 0.2;
    return utils.add(max + padding, this.yAxisPadding());
  }

  /**
   * Get or set the numerical format of the boxplot median, whiskers and quartile labels. Defaults
   * to integer formatting.
   * @example
   * // format ticks to 2 decimal places
   * chart.tickFormat(d3.format('.2f'));
   * @param {Function} [tickFormat]
   * @returns {Number|Function|BoxPlot}
   */
  tickFormat(tickFormat) {
    if (!arguments.length) {
      return this._tickFormat;
    }
    this._tickFormat = tickFormat;
    return this;
  }

  /**
   * Get or set the amount of padding to add, in pixel coordinates, to the top and
   * bottom of the chart to accommodate box/whisker labels.
   * @example
   * // allow more space for a bigger whisker font
   * chart.yRangePadding(12);
   * @param {Function} [yRangePadding = 8]
   * @returns {Number|Function|BoxPlot}
   */
  yRangePadding(yRangePadding) {
    if (!arguments.length) {
      return this._yRangePadding;
    }
    this._yRangePadding = yRangePadding;
    return this;
  }

  /**
   * Get or set whether individual data points will be rendered.
   * @example
   * // Enable rendering of individual data points
   * chart.renderDataPoints(true);
   * @param {Boolean} [show=false]
   * @returns {Boolean|BoxPlot}
   */
  renderDataPoints(show) {
    if (!arguments.length) {
      return this._renderDataPoints;
    }
    this._renderDataPoints = show;
    return this;
  }

  /**
   * Get or set the opacity when rendering data.
   * @example
   * // If individual data points are rendered increase the opacity.
   * chart.dataOpacity(70%);
   * @param {Number} [opacity=0.3]
   * @returns {Number|BoxPlot}
   */
  dataOpacity(opacity) {
    if (!arguments.length) {
      return this._dataOpacity;
    }
    this._dataOpacity = opacity;
    return this;
  }

  /**
   * Get or set the portion of the width of the box to show data points.
   * @example
   * // If individual data points are rendered increase the data box.
   * chart.dataWidthPortion(0.9);
   * @param {Number} [percentage=0.8]
   * @returns {Number|BoxPlot}
   */
  dataWidthPortion(percentage) {
    if (!arguments.length) {
      return this._dataWidthPortion;
    }
    this._dataWidthPortion = percentage;
    return this;
  }

  /**
   * Get or set whether outliers will be rendered.
   * @example
   * // Disable rendering of outliers
   * chart.showOutliers(false);
   * @param {Boolean} [show=true]
   * @returns {Boolean|BoxPlot}
   */
  showOutliers(show) {
    if (!arguments.length) {
      return this._showOutliers;
    }
    this._showOutliers = show;
    return this;
  }

  /**
   * Get or set whether outliers will be drawn bold.
   * @example
   * // If outliers are rendered display as bold
   * chart.boldOutlier(true);
   * @param {Boolean} [show=false]
   * @returns {Boolean|BoxPlot}
   */
  boldOutlier(show) {
    if (!arguments.length) {
      return this._boldOutlier;
    }
    this._boldOutlier = show;
    return this;
  }
}

export const agoraBoxPlot = (parent, chartGroup?, options?) =>
  new AgoraBoxPlot(parent, chartGroup, options);
