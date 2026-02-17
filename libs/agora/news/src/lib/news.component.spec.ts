import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NewsComponent } from './news.component';
import { provideHttpClient } from '@angular/common/http';

describe('NewsComponent', () => {
  let component: NewsComponent;
  let fixture: ComponentFixture<NewsComponent>;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(NewsComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have wiki component', () => {
    expect(element.querySelector('explorers-wiki')).toBeTruthy();
  });
});
