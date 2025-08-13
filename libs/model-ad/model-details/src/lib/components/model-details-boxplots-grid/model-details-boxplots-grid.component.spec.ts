import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsBoxplotsGridComponent } from './model-details-boxplots-grid.component';

const mockTitle = 'My Title';
const mockModelDataList = modelMock.biomarkers.slice(1, 4);

async function setup(modelDataList = mockModelDataList) {
  return render(ModelDetailsBoxplotsGridComponent, {
    imports: [],
    componentInputs: {
      modelDataList: modelDataList,
      sexes: ['Female', 'Male'],
      title: mockTitle,
    },
  });
}

describe('ModelDetailsBoxplotsGridComponent', () => {
  it('should render title', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2, name: mockTitle })).toBeVisible();
  });

  it('should render the correct number of boxplot components', async () => {
    const { container } = await setup();
    const boxplotComponents = container.querySelectorAll('model-ad-model-details-boxplot');
    expect(boxplotComponents).toHaveLength(mockModelDataList.length);
  });
});
