import { render, screen } from '@testing-library/angular';
import { ModelDetailsComponent } from './model-details.component';

async function setup() {
  const result = await render(ModelDetailsComponent, {
    imports: [],
    providers: [],
  });

  const fixture = result.fixture;
  const component = fixture.componentInstance;
  return { fixture, component };
}

describe('ModelDetailsComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});
