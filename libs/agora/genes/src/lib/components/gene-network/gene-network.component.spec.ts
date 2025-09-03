// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { GeneService } from '@sagebionetworks/agora/api-client-angular';
import { GeneNetworkComponent } from './gene-network.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Network', () => {
  let fixture: ComponentFixture<GeneNetworkComponent>;
  let component: GeneNetworkComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [GeneService, provideHttpClient()],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneNetworkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
