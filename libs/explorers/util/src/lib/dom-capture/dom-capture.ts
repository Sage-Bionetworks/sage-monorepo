import { toBlob } from 'html-to-image';

/** Captured at module load so concurrent calls always restore to the real native function. */
let nativeGetComputedStyle: Window['getComputedStyle'];
if (typeof window !== 'undefined') {
  nativeGetComputedStyle = window.getComputedStyle.bind(window);
}

/**
 * Captures a DOM element as a Blob image using html-to-image,
 * with optimizations to skip fonts and CSS custom properties.
 */
export async function captureDomToBlob(target: HTMLElement, paddingPx = 0): Promise<Blob | null> {
  paddingPx = Math.max(0, paddingPx);
  const restore = patchGetComputedStyleToSkipCSSVars();

  try {
    return await toBlob(target, {
      backgroundColor: '#fff',
      width: target.offsetWidth + paddingPx * 2,
      height: target.offsetHeight + paddingPx * 2,
      skipFonts: true,
      style: paddingPx > 0 ? { padding: `${paddingPx}px` } : undefined,
    });
  } finally {
    restore();
  }
}

/**
 * Patches getComputedStyle to hide CSS custom properties (--vars) from html-to-image,
 * reducing the number of properties copied per node during serialization.
 * Returns a restore function to undo the patch after capture.
 */
function patchGetComputedStyleToSkipCSSVars(): () => void {
  window.getComputedStyle = (elt: Element, pseudo?: string | null): CSSStyleDeclaration => {
    const style = nativeGetComputedStyle(elt, pseudo);
    return new Proxy(style, {
      get(target, prop) {
        const allProps = target as CSSStyleDeclaration;

        if (prop === 'length') {
          let count = 0;
          for (let i = 0; i < allProps.length; i++) {
            if (!allProps[i].startsWith('--')) count++;
          }
          return count;
        }

        if (typeof prop === 'string' && !isNaN(Number(prop))) {
          const index = Number(prop);
          let count = 0;
          for (let i = 0; i < allProps.length; i++) {
            if (!allProps[i].startsWith('--')) {
              if (count === index) return allProps[i];
              count++;
            }
          }
          return undefined;
        }

        const value = (target as unknown as Record<string | symbol, unknown>)[prop];
        return typeof value === 'function' ? value.bind(target) : value;
      },
    });
  };

  return () => {
    window.getComputedStyle = nativeGetComputedStyle;
  };
}
