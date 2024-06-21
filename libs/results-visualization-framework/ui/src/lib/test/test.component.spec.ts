import { render, screen } from '@testing-library/angular';
import { TestComponent } from './test.component';

const setUp = async (inputText: string) => {
  await render(TestComponent, {
    componentProperties: { inputText },
  });
};

describe('TestComponent', () => {
  it('should display inputText', async () => {
    const text = 'Hello World!';
    await setUp(text);
    expect(screen.getByRole('heading', { name: text })).toBeVisible();
  });
});
