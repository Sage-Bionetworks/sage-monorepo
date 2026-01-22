import { Sex } from '@sagebionetworks/model-ad/api-client';
import { render } from '@testing-library/angular';
import { BoxplotData } from '../boxplot/boxplot.component';
import { BoxplotsGridComponent } from './boxplots-grid.component';

const mockBoxplotDataList: BoxplotData[] = [
  {
    age: '6 months',
    units: 'ng/ml',
    y_axis_max: 10.0,
    data: [
      { genotype: '3xTg-AD', sex: 'Male', individual_id: '1001', value: 5.5 },
      { genotype: '3xTg-AD', sex: 'Female', individual_id: '1003', value: 7.2 },
    ],
  },
  {
    age: '12 months',
    units: 'ng/ml',
    y_axis_max: 15.0,
    data: [
      { genotype: '5xFAD', sex: 'Male', individual_id: '2001', value: 8.5 },
      { genotype: '5xFAD', sex: 'Female', individual_id: '2003', value: 9.2 },
    ],
  },
];

async function setup(boxplotDataList = mockBoxplotDataList, sexFilter?: Sex[]) {
  return render(BoxplotsGridComponent, {
    imports: [],
    componentInputs: {
      boxplotDataList,
      sexFilter,
    },
  });
}

describe('BoxplotsGridComponent', () => {
  it('should render the correct number of boxplot components', async () => {
    const { container } = await setup();
    const boxplotComponents = container.querySelectorAll('model-ad-boxplot');
    expect(boxplotComponents).toHaveLength(mockBoxplotDataList.length);
  });

  it('should render legend with all point styles when no sex filter provided', async () => {
    const { container } = await setup();
    const legend = container.querySelector('.boxplots-grid-legend');
    expect(legend).toBeTruthy();
  });

  it('should filter point styles in legend when sex filter is provided', async () => {
    const { fixture } = await setup(mockBoxplotDataList, ['Male']);
    const component = fixture.componentInstance;
    const pointStyles = component.pointStyles();
    expect(pointStyles).toHaveLength(1);
    expect(pointStyles[0].label).toBe('Male');
  });

  it('should show all point styles when both sexes are in filter', async () => {
    const { fixture } = await setup(mockBoxplotDataList, ['Female', 'Male']);
    const component = fixture.componentInstance;
    const pointStyles = component.pointStyles();
    expect(pointStyles).toHaveLength(2);
  });
});
