import { Injectable } from '@angular/core';
import { Panel } from '@sagebionetworks/explorers/models';

@Injectable({
  providedIn: 'root',
})
export class HelperService {
  getScrollTop() {
    if (typeof window === 'undefined' || typeof document == 'undefined') {
      return { x: 0, y: 0 };
    }

    const supportPageOffset = window.pageXOffset !== undefined;
    const isCSS1Compat = (document.compatMode || '') === 'CSS1Compat';

    if (supportPageOffset) {
      return {
        x: window.pageXOffset,
        y: window.pageYOffset,
      };
    } else if (isCSS1Compat) {
      return {
        x: document.documentElement.scrollLeft,
        y: document.documentElement.scrollTop,
      };
    } else {
      return {
        x: document.body.scrollLeft,
        y: document.body.scrollTop,
      };
    }
  }

  getOffset(el: any) {
    if (typeof window === 'undefined' || typeof document === 'undefined' || !el) {
      return { top: 0, left: 0 };
    }

    const rect = el.getBoundingClientRect();
    const scroll = this.getScrollTop();
    return {
      top: rect.top + scroll.y,
      left: rect.left + scroll.x,
    };
  }

  getPanelUrl(basePath: string, activePanel: string, activeParent: string) {
    const panelPath = activeParent === '' ? activePanel : `${activeParent}/${activePanel}`;
    return `${basePath}/${panelPath}`;
  }

  getActivePanelAndParent(
    panels: Panel[],
    panel: Panel,
  ): { activePanel: string; activeParent: string } {
    if (panel.children) {
      return {
        activePanel: panel.children[0].name,
        activeParent: panel.name,
      };
    } else if (!panels.find((p: Panel) => p.name === panel.name)) {
      const parent = panels.find((p: Panel) =>
        p.children?.find((c: Panel) => c.name === panel.name),
      );
      return {
        activePanel: panel.name,
        activeParent: parent?.name ?? '',
      };
    } else {
      return {
        activePanel: panel.name,
        activeParent: '',
      };
    }
  }

  findPanelByName(panels: Panel[], panelName: string): Panel | undefined {
    const topLevelPanel = panels.find((p) => p.name === panelName);
    if (topLevelPanel) {
      return topLevelPanel;
    }

    for (const panel of panels) {
      if (panel.children) {
        const childPanel = panel.children.find((child) => child.name === panelName);
        if (childPanel) {
          return childPanel;
        }
      }
    }

    return undefined;
  }

  getUrlParam(name: string) {
    if (typeof window === 'undefined') return null;

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    return urlParams.get(name);
  }

  getHashFragment() {
    // Extract hash fragment from URL (e.g. "nfl" from "#nfl")
    return window.location.hash.slice(1);
  }

  encodeParenthesesForwardSlashes(uri: string) {
    return (
      uri
        // forward slash within parentheses
        .replace(/\(([^)]*)\)/g, (match, content) => `(${content.replace(/\//g, '%2F')})`)
        // parentheses
        .replace(/\(/g, '%28')
        .replace(/\)/g, '%29')
    );
  }

  getNumberFromCSSValue(cssValue: string) {
    return parseFloat(cssValue);
  }

  getSignificantFigures(n: number, sig = 2) {
    let sign = 1;
    if (n === 0) {
      return 0;
    }
    if (n < 0) {
      n *= -1;
      sign = -1;
    }

    const mult = Math.pow(10, sig - Math.floor(Math.log(n) / Math.LN10) - 1);
    return (Math.round(n * mult) / mult) * sign;
  }

  cleanFilename(filename: string) {
    const invalidFilenameCharsRegex = /[<>:"\\/|?*]/g;
    return filename.replaceAll(invalidFilenameCharsRegex, '_').replaceAll(' ', '_');
  }
}
