import { CommonModule } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SynapseApiService } from '@sagebionetworks/agora/services';
import { DialogModule } from 'primeng/dialog';
import { LoadingIconComponent } from '../loading-icon/loading-icon.component';
import { SvgIconComponent } from '../svg-icon/svg-icon.component';
import { WikiComponent } from '../wiki/wiki.component';
import { ModalLinkComponent } from './modal-link.component';

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
