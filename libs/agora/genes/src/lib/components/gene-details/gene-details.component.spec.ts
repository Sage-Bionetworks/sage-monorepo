import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { GeneService } from '@sagebionetworks/agora/api-client';
import { HelperService, SvgIconService } from '@sagebionetworks/agora/services';
import {
  ActivatedRouteStub,
  geneMock1,
  GenesServiceStub,
  SvgIconServiceStub,
} from '@sagebionetworks/agora/testing';
import { GeneDetailsComponent } from './gene-details.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Details', () => {
  let fixture: ComponentFixture<GeneDetailsComponent>;
  let component: GeneDetailsComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(),
        },
        provideHttpClient(),
        Location,
        HelperService,
        {
          provide: GeneService,
          useValue: new GenesServiceStub(),
        },
        { provide: SvgIconService, useClass: SvgIconServiceStub },
      ],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have data', () => {
    const noiSpy = jest.spyOn(component, 'ngOnInit');

    component.ngOnInit();
    fixture.detectChanges();
    expect(noiSpy).toHaveBeenCalled();
    expect(component.gene).toEqual(geneMock1);
  });
});
