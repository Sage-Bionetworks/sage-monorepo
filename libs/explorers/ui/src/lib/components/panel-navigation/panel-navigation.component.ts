import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  HostListener,
  inject,
  input,
  output,
  PLATFORM_ID,
} from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faAngleLeft, faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { Panel } from '@sagebionetworks/explorers/models';
import { HelperService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-panel-navigation',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './panel-navigation.component.html',
  styleUrls: ['./panel-navigation.component.scss'],
})
export class PanelNavigationComponent implements AfterViewInit, AfterViewChecked {
  helperService = inject(HelperService);
  private readonly platformId: Record<string, any> = inject(PLATFORM_ID);

  panels = input.required<Panel[]>();
  activePanel = input.required<string>();
  activeParent = input<string>('');
  panelChange = output<Panel>();

  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;

  private readonly DEFAULT_NAV_SLIDE_INDEX = 0;
  navSlideIndex = this.DEFAULT_NAV_SLIDE_INDEX;

  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    if (isPlatformBrowser(this.platformId)) {
      const nav = document.querySelector<HTMLElement>('.panel-navigation');
      const rect = nav?.getBoundingClientRect();

      if (rect && rect.y <= 0) {
        nav?.classList.add('sticky');
      } else {
        nav?.classList.remove('sticky');
      }
    }
  }

  @HostListener('window:resize', ['$event'])
  onWindowResize() {
    if (isPlatformBrowser(this.platformId)) {
      const nav = document.querySelector<HTMLElement>('.panel-navigation');
      const navContainer = nav?.querySelector<HTMLElement>('.panel-navigation-container');
      const navList = nav?.querySelector<HTMLElement>('.panel-navigation-container > ul');
      const navItems = nav?.querySelectorAll<HTMLElement>('.panel-navigation-container > ul > li');
      let navItemsWidth = 0;
      if (navItems) {
        for (const element of Array.from(navItems)) {
          navItemsWidth += element.offsetWidth;
        }
      }

      if (navContainer && navList && navItemsWidth) {
        if (navItemsWidth > navContainer.offsetWidth) {
          nav?.classList.add('scrollable');
        } else {
          nav?.classList.remove('scrollable');
          this.navSlideIndex = 0;
          navList.style.marginLeft = '0px';
        }
      }
    }
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.onWindowResize();
    }, 100);
  }

  ngAfterViewChecked() {
    this.onWindowResize();
  }

  activatePanel(panel: Panel) {
    if (isPlatformBrowser(this.platformId)) {
      const nav = document.querySelector('.panel-navigation');
      if (nav) {
        window.scrollTo(0, this.helperService.getOffset(nav).top);
      }

      this.panelChange.emit(panel);
    }
  }

  getPanelCount() {
    return this.panels().filter((p: Panel) => !p.disabled).length;
  }

  onNavigationItemClick(panel: Panel) {
    this.activatePanel(panel);
  }

  slideNavigation(direction: number) {
    this.navSlideIndex += direction;

    if (this.navSlideIndex < 0) {
      this.navSlideIndex = 0;
    } else if (this.navSlideIndex > this.getPanelCount() - 1) {
      this.navSlideIndex = this.getPanelCount() - 1;
    }

    const nav = document.querySelector<HTMLElement>('.panel-navigation');
    const navList = nav?.querySelector<HTMLElement>('.panel-navigation-container > ul');
    const navItems = nav?.querySelectorAll<HTMLElement>('.panel-navigation-container > ul > li');

    if (navList && navItems) {
      let navItemsWidth = 0;

      for (let i = 0; i < this.navSlideIndex; ++i) {
        navItemsWidth += navItems[i].offsetWidth;
      }

      if (this.navSlideIndex > 0) {
        navItemsWidth += 20;
      }

      navList.style.marginLeft = navItemsWidth * -1 + 'px';
    }
  }

  isPanelActive(panel: Panel): boolean {
    return this.activePanel() === panel.name || this.activeParent() === panel.name;
  }
}
