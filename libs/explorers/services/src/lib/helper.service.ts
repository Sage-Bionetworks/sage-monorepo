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

  getUrlParam(name: string) {
    if (typeof window === 'undefined') return null;

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    return urlParams.get(name);
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
}
