import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/agora/testing';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { MessageService } from 'primeng/api';
import { GeneComparisonToolComponent } from './gene-comparison-tool.component';
import { AGORA_LOADING_ICON_COLORS } from '@sagebionetworks/agora/config';

describe('GeneComparisonToolComponent', () => {
  let component: GeneComparisonToolComponent;
  let fixture: ComponentFixture<GeneComparisonToolComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        MessageService,
        { provide: SvgIconService, useClass: SvgIconServiceStub },
        ...provideLoadingIconColors(AGORA_LOADING_ICON_COLORS),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(GeneComparisonToolComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
