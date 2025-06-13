import { render } from '@testing-library/angular';
import { ContainerSize, SectionComponent } from './section.component';

async function setup(containerSize: ContainerSize = 'md') {
  await render(SectionComponent, {
    providers: [],
    componentInputs: {
      containerSize: containerSize,
    },
  });
}

describe('SectionComponent', () => {
  it('should apply default container size', async () => {
    await setup();
    expect(document.querySelector('.container-md')).toBeTruthy();
  });

  it('should apply user specified container size', async () => {
    await setup('sm-md');
    expect(document.querySelector('.container-sm-md')).toBeTruthy();
  });
});
