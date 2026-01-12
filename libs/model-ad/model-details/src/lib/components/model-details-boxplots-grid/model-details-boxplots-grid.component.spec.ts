import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render } from '@testing-library/angular';
import { ModelDetailsBoxplotsGridComponent } from './model-details-boxplots-grid.component';

const mockModelDataList = modelMock.biomarkers.slice(1, 4);

async function setup(modelDataList = mockModelDataList) {
  return render(ModelDetailsBoxplotsGridComponent, {
    imports: [],
    componentInputs: {
      modelDataList: modelDataList,
      sexes: ['Female', 'Male'],
    },
  });
}

describe('ModelDetailsBoxplotsGridComponent', () => {
  it('should render the correct number of boxplot components', async () => {
    const { container } = await setup();
    const boxplotComponents = container.querySelectorAll('model-ad-boxplot');
    expect(boxplotComponents).toHaveLength(mockModelDataList.length);
  });
});
