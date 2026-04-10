import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExamplePromptsComponent } from './example-prompts.component';

describe('ExamplePromptsComponent', () => {
  let component: ExamplePromptsComponent;
  let fixture: ComponentFixture<ExamplePromptsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamplePromptsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ExamplePromptsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have 5 helix nodes', () => {
    expect(component.nodes).toHaveLength(5);
  });

  it('should select a prompt on node click', () => {
    component.onNodeClick(component.nodes[0]);
    expect(component.selectedPrompt()).toBeTruthy();
    expect(component.selectedCategory()).toBe('Cardiology');
  });

  it('should emit prompt on usePrompt', () => {
    const spy = jest.fn();
    component.promptSelect.subscribe(spy);
    component.onNodeClick(component.nodes[0]);
    component.usePrompt();
    expect(spy).toHaveBeenCalled();
    expect(component.selectedPrompt()).toBeNull();
  });
});
