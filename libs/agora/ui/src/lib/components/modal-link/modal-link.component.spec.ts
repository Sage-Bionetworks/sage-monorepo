import { TestBed, ComponentFixture } from '@angular/core/testing';
import { ModalLinkComponent } from './modal-link.component';
import { DialogModule } from 'primeng/dialog';
import { LoadingIconComponent, SvgIconComponent } from '@sagebionetworks/agora/ui';
import { WikiComponent } from 'libs/agora/wiki/src/lib/wiki.component';
import { SynapseApiService } from '@sagebionetworks/agora/services';
import { provideHttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

describe('ModalLinkComponent', () => {
  let fixture: ComponentFixture<ModalLinkComponent>;
  let component: ModalLinkComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ModalLinkComponent,
        CommonModule,
        DialogModule,
        SvgIconComponent,
        WikiComponent,
        LoadingIconComponent,
      ],
      providers: [SynapseApiService, provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalLinkComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
