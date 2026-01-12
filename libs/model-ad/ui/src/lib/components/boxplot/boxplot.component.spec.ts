import { Sex } from '@sagebionetworks/model-ad/api-client';
import { render, screen } from '@testing-library/angular';
import { BoxplotComponent, BoxplotData } from './boxplot.component';

const mockBoxplotData: BoxplotData = {
  age: '6 months',
  units: 'ng/ml',
  y_axis_max: 10.0,
  data: [
    { genotype: '3xTg-AD', sex: 'Male', individual_id: '1001', value: 5.5 },
    { genotype: '3xTg-AD', sex: 'Male', individual_id: '1002', value: 6.2 },
    { genotype: '3xTg-AD', sex: 'Female', individual_id: '1003', value: 7.2 },
    { genotype: '3xTg-AD', sex: 'Female', individual_id: '1004', value: 4.4 },
  ],
};

async function setup(boxplotData = mockBoxplotData, sexFilter?: Sex[]) {
  const { fixture } = await render(BoxplotComponent, {
    imports: [],
    componentInputs: {
      boxplotData,
      sexFilter,
    },
  });
  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('BoxplotComponent', () => {
  it('should render chart title with age', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 3, name: '6 months' })).toBeVisible();
  });

  it('should include all data when no sex filter is provided', async () => {
    const { component } = await setup(mockBoxplotData, undefined);
    const points = component.points();
    expect(points).toHaveLength(4);
  });

  it('should filter data by selected sexes when filter is provided', async () => {
    const { component } = await setup(mockBoxplotData, ['Male']);
    const points = component.points();
    expect(points).toHaveLength(2);
    expect(points.every((point) => point.pointCategory === 'Male')).toBe(true);
  });

  it('should include both sexes when both are in the filter', async () => {
    const { component } = await setup(mockBoxplotData, ['Female', 'Male']);

    const points = component.points();
    expect(points).toHaveLength(4);

    const malePoints = points.filter((point) => point.pointCategory === 'Male');
    const femalePoints = points.filter((point) => point.pointCategory === 'Female');
    expect(malePoints).toHaveLength(2);
    expect(femalePoints).toHaveLength(2);
  });

  it('should handle empty sexes filter', async () => {
    const { component } = await setup(mockBoxplotData, []);
    const points = component.points();
    expect(points).toHaveLength(0);
  });

  it('should compute yAxisMax from data when present', async () => {
    const { component } = await setup(mockBoxplotData);
    expect(component.yAxisMax()).toBe(10.0);
  });

  it('should return undefined yAxisMax when not present in data', async () => {
    const dataWithoutMax: BoxplotData = {
      age: '6 months',
      units: 'ng/ml',
      data: mockBoxplotData.data,
    };
    const { component } = await setup(dataWithoutMax);
    expect(component.yAxisMax()).toBeUndefined();
  });

  it('should format x-axis labels', async () => {
    const { component } = await setup();

    const labelToExpectedFormattedLabel = {
      'short-dash': 'short-dash',
      'short*star': 'short*star',
      'long-onedash': 'long-\nonedash',
      'long*onestar': 'long*\nonestar',
      'long-multi-dash': 'long-\nmulti-dash',
      'long*multi*star': 'long*\nmulti*star',
    };

    for (const [label, expectedFormattedLabel] of Object.entries(labelToExpectedFormattedLabel)) {
      const formattedLabel = component.xAxisLabelFormatter(label);
      expect(formattedLabel).toBe(expectedFormattedLabel);
    }
  });
});
