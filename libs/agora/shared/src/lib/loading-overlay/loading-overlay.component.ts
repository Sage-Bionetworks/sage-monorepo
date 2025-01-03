import { Component, inject, Input, OnInit } from '@angular/core';
import { HelperService } from '@sagebionetworks/agora/services';
import { CommonModule } from '@angular/common';
import { LoadingIconComponent } from '../loading-icon/loading-icon.component';

@Component({
  selector: 'agora-loading-overlay',
  standalone: true,
  imports: [CommonModule, LoadingIconComponent],
  providers: [HelperService],
  templateUrl: './loading-overlay.component.html',
  styleUrls: ['./loading-overlay.component.scss'],
})
export class LoadingOverlayComponent implements OnInit {
  helperService = inject(HelperService);

  @Input() isGlobal = false;
  @Input() isActive = false;

  ngOnInit() {
    console.log('global', this.isGlobal);
    if (this.isGlobal) {
      this.helperService.loadingChange.subscribe(() => {
        this.isActive = this.helperService.getLoading();
        console.log('active', this.isActive);
      });
    }
  }
}
