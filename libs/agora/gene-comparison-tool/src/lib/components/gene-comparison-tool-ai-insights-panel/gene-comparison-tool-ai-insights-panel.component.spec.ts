import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideMarkdown } from 'ngx-markdown';
import { GeneComparisonToolAiInsightsPanelComponent } from './gene-comparison-tool-ai-insights-panel.component';

describe('GeneComparisonToolAiInsightsPanelComponent', () => {
  let component: GeneComparisonToolAiInsightsPanelComponent;
  let fixture: ComponentFixture<GeneComparisonToolAiInsightsPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneComparisonToolAiInsightsPanelComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideMarkdown()],
    }).compileComponents();

    fixture = TestBed.createComponent(GeneComparisonToolAiInsightsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle panel visibility', () => {
    expect(component.isOpen).toBe(false);
    component.toggle();
    expect(component.isOpen).toBe(true);
    component.toggle();
    expect(component.isOpen).toBe(false);
  });

  it('should clear conversation', () => {
    component.messages = [
      { role: 'user', content: 'test' },
      { role: 'assistant', content: 'response' },
    ];
    component.errorMessage = 'some error';
    component.clearConversation();
    expect(component.messages.length).toBe(0);
    expect(component.errorMessage).toBe('');
  });
});
