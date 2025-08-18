import { IndividualData, ModelData } from '@sagebionetworks/model-ad/api-client-angular';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsBoxplotComponent } from './model-details-boxplot.component';

const mockModelData: ModelData = {
  name: '3xTg-AD',
  evidence_type: 'A&beta;42',
  tissue: 'Cerebral Cortex',
  age: '6 months',
  units: 'ng/ml',
  data: [
    { genotype: '3xTg-AD', sex: 'Male', individual_id: '1001', value: 5.5 },
    { genotype: '3xTg-AD', sex: 'Male', individual_id: '1002', value: 6.2 },
    { genotype: '3xTg-AD', sex: 'Female', individual_id: '1003', value: 7.2 },
    { genotype: '3xTg-AD', sex: 'Female', individual_id: '1004', value: 4.4 },
  ],
};

async function setup(
  modelData = mockModelData,
  sexes: IndividualData.SexEnum[] = ['Female', 'Male'],
) {
  const { fixture } = await render(ModelDetailsBoxplotComponent, {
    imports: [],
    componentInputs: {
      modelData,
      sexes,
    },
  });
  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('ModelDetailsBoxplotComponent', () => {
  it('should render chart title with age', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 3, name: '6 months' })).toBeVisible();
  });

  it('should filter data by selected sexes', async () => {
    const { component } = await setup(mockModelData, ['Male']);
    const points = component.points();
    expect(points).toHaveLength(2);
    expect(points.every((point) => point.pointCategory === 'Male')).toBe(true);
  });

  it('should include both sexes when both are selected', async () => {
    const { component } = await setup(mockModelData, ['Female', 'Male']);

    const points = component.points();
    expect(points).toHaveLength(4);

    const malePoints = points.filter((point) => point.pointCategory === 'Male');
    const femalePoints = points.filter((point) => point.pointCategory === 'Female');
    expect(malePoints).toHaveLength(2);
    expect(femalePoints).toHaveLength(2);
  });

  it('should handle empty sexes filter', async () => {
    const { component } = await setup(mockModelData, []);
    const points = component.points();
    expect(points).toHaveLength(0);
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
