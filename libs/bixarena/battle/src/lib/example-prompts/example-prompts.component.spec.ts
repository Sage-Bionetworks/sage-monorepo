import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import {
  BiomedicalCategory,
  ExamplePrompt,
  ExamplePromptPage,
  ExamplePromptSearchQuery,
  ExamplePromptService,
  ExamplePromptSort,
} from '@sagebionetworks/bixarena/api-client';
import { ExamplePromptsComponent } from './example-prompts.component';

describe('ExamplePromptsComponent', () => {
  let component: ExamplePromptsComponent;
  let fixture: ComponentFixture<ExamplePromptsComponent>;
  let listSpy: jest.Mock;

  const makePrompts = (ids: string[]): ExamplePrompt[] =>
    ids.map((id) => ({
      id,
      question: `Question ${id}?`,
      source: 'bixarena',
      active: true,
      categories: [BiomedicalCategory.Genetics],
      createdAt: '2026-04-20T00:00:00Z',
    })) as ExamplePrompt[];

  const pageOf = (ids: string[]): Partial<ExamplePromptPage> => ({
    examplePrompts: makePrompts(ids),
  });

  async function setup(initialIds = ['p1', 'p2', 'p3']) {
    listSpy = jest.fn(() => of(pageOf(initialIds) as ExamplePromptPage));

    await TestBed.configureTestingModule({
      imports: [ExamplePromptsComponent],
      providers: [{ provide: ExamplePromptService, useValue: { listExamplePrompts: listSpy } }],
    }).compileComponents();

    fixture = TestBed.createComponent(ExamplePromptsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('creates and fetches initial prompts', async () => {
    await setup();
    expect(component).toBeTruthy();
    expect(listSpy).toHaveBeenCalledTimes(1);
    const query = listSpy.mock.calls[0][0] as ExamplePromptSearchQuery;
    expect(query.sort).toBe(ExamplePromptSort.Random);
    expect(query.pageSize).toBe(3);
    expect(query.active).toBe(true);
    expect(query.categories).toBeUndefined();
    expect(component.prompts()).toHaveLength(3);
  });

  it('emits promptSelect with the question text on card click', async () => {
    await setup(['pX']);
    const emitted: string[] = [];
    component.promptSelect.subscribe((q) => emitted.push(q));
    component.onCardClick(component.prompts()[0]);
    expect(emitted).toEqual(['Question pX?']);
  });

  it('categoryLabel formats multi-word slugs with preserved "and"', async () => {
    await setup();
    const prompt = {
      id: 'p',
      question: 'q?',
      source: 'bixarena',
      active: true,
      categories: [BiomedicalCategory.PharmacologyAndToxicology],
      createdAt: '2026-04-20T00:00:00Z',
    } as ExamplePrompt;
    expect(component.categoryLabel(prompt)).toBe('Pharmacology and Toxicology');
  });

  it('refresh fetches a fresh set', async () => {
    await setup();
    listSpy.mockClear();
    component.refresh();
    jest.advanceTimersByTime(500);
    expect(listSpy).toHaveBeenCalledTimes(1);
  });

  it('refresh is debounced while a fetch is in-flight', async () => {
    await setup();
    listSpy.mockImplementation(() => of(pageOf(['qA']) as ExamplePromptPage));
    component.loading.set(true);
    component.refresh();
    jest.advanceTimersByTime(500);
    const baseline = listSpy.mock.calls.length;
    component.refresh();
    jest.advanceTimersByTime(500);
    expect(listSpy.mock.calls.length).toBe(baseline);
  });

  it('renders empty state when the API returns no prompts', async () => {
    listSpy = jest.fn(() => of({ examplePrompts: [] } as unknown as ExamplePromptPage));
    await TestBed.configureTestingModule({
      imports: [ExamplePromptsComponent],
      providers: [{ provide: ExamplePromptService, useValue: { listExamplePrompts: listSpy } }],
    }).compileComponents();
    fixture = TestBed.createComponent(ExamplePromptsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component.prompts()).toEqual([]);
    expect(component.error()).toBe(false);
  });

  it('sets the error flag when the API call fails', async () => {
    listSpy = jest.fn(() => throwError(() => new Error('boom')));
    await TestBed.configureTestingModule({
      imports: [ExamplePromptsComponent],
      providers: [{ provide: ExamplePromptService, useValue: { listExamplePrompts: listSpy } }],
    }).compileComponents();
    fixture = TestBed.createComponent(ExamplePromptsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component.error()).toBe(true);
    expect(component.prompts()).toEqual([]);
  });

  it('exposes static helix geometry for the template', async () => {
    await setup();
    expect(component.frontPath.startsWith('M')).toBe(true);
    expect(component.backPath.startsWith('M')).toBe(true);
    expect(component.rungs.length).toBeGreaterThan(0);
    for (const r of component.rungs) {
      expect(typeof r.x).toBe('number');
      expect(r.y1).not.toBe(r.y2);
    }
  });
});
