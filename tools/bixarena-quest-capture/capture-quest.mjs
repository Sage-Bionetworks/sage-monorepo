// capture-quest.mjs
// Captures the BioArena Community Quest section from localhost:8100
//
// Usage:
//   node capture-quest.mjs
//   node capture-quest.mjs --gif
//   node capture-quest.mjs --mp4
//   node capture-quest.mjs --mp4 --portrait --record-ms=30000
//   node capture-quest.mjs --mp4 --fps=15 --record-ms=30000 --trim-start-ms=3000
//   node capture-quest.mjs --url=http://localhost:7860
//
// Notes:
// - Captures just the quest section from the BioArena app
// - --gif creates a GIF (default: WebM is kept).
// - --mp4 creates an MP4 video (RECOMMENDED for LinkedIn - much smaller than GIF).
// - --portrait creates 0.8 aspect ratio portrait video (1080x1350) showing carousel, progress, and contributors.
// - --fps controls output framerate (default: 15).
// - --record-ms controls how long to record (default: 12000 ms = 2 carousel rotations at 6s each).
// - --trim-start-ms removes N milliseconds from start to eliminate loading artifacts (default: 2000).
// - --scale controls output size as percentage (default: 100, use 75-90 for smaller files).
// - --url specifies the app URL (default: https://bioarena.io).
// - Quest carousel rotates every 6 seconds by default (5 images = 30 seconds total).
// - For LinkedIn (< 5 MB): use --mp4 (produces files ~500KB-2MB for 30s videos).
// - Portrait mode hides: CTA buttons, tier legend, and credits section.
//
// Requirements:
// - Playwright (Chromium) installed: `pnpm dlx playwright install chromium --with-deps`
// - ffmpeg available on PATH for MP4/GIF conversion

import { chromium } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';
import { exec } from 'child_process';
import { promisify } from 'util';
import fs from 'fs/promises';

const execAsync = promisify(exec);

// ---------- Helpers ----------
function parseFlagNum(args, name, def) {
  const raw = args.find((a) => a.startsWith(`--${name}=`));
  if (!raw) return def;
  const v = Number(raw.split('=')[1]);
  return Number.isFinite(v) ? v : def;
}

function parseFlagString(args, name, def) {
  const raw = args.find((a) => a.startsWith(`--${name}=`));
  if (!raw) return def;
  return raw.split('=')[1];
}

function hasFlag(args, name) {
  return args.some((a) => a === `--${name}`);
}

async function ensureFfmpeg() {
  try {
    await execAsync('ffmpeg -version');
  } catch {
    console.error('Error: ffmpeg is not available on PATH. Please install ffmpeg.');
    process.exit(1);
  }
}

// ---------- CLI parsing ----------
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const args = process.argv.slice(2);

const wantGif = hasFlag(args, 'gif');
const wantMp4 = hasFlag(args, 'mp4');
const wantPortrait = hasFlag(args, 'portrait');
const format = wantGif ? 'gif' : wantMp4 ? 'mp4' : 'webm';
const GIF_FPS = parseFlagNum(args, 'fps', 15);
// Default: 12 seconds = 2 full carousel rotations (6s each)
const RECORD_MS = parseFlagNum(args, 'record-ms', 12000);
const APP_URL = parseFlagString(args, 'url', 'https://bioarena.io');
// Wait 3 seconds for initial render and potential redirects
const PRE_WAIT_MS = 3000;
// Trim first N milliseconds from video to remove loading artifacts
const TRIM_START_MS = parseFlagNum(args, 'trim-start-ms', 2000);
// Scale output (percentage, 100 = original size, 75 = 75% of original)
const SCALE_PERCENT = parseFlagNum(args, 'scale', 100);

// Output files
const outputDir = __dirname;
const webmFile = path.join(outputDir, `bixarena-quest.webm`);
const outputFile = path.join(outputDir, `bixarena-quest.${format}`);

console.log(`App URL: ${APP_URL}`);
console.log(`Output format: ${format.toUpperCase()}`);
console.log(`Mode: ${wantPortrait ? 'Portrait (0.8 aspect ratio)' : 'Landscape'}`);
console.log(`Record duration: ${RECORD_MS}ms (${RECORD_MS / 1000}s)`);
console.log(`Trim first: ${TRIM_START_MS}ms (to remove loading artifacts)`);
if (wantGif || wantMp4) {
  console.log(`Output framerate: ${GIF_FPS} fps`);
  console.log(`Scale: ${SCALE_PERCENT}%`);
}

// ---------- Pass 1: measure quest section dimensions ----------
const browser = await chromium.launch({ headless: true });
const context = await browser.newContext({
  viewport: { width: 1920, height: 1080 },
  colorScheme: 'dark', // Force dark mode
});
const page = await context.newPage();

console.log('Loading BioArena home page...');
try {
  await page.goto(APP_URL, { waitUntil: 'domcontentloaded', timeout: 30000 });
} catch (error) {
  console.error(`Error: Could not connect to ${APP_URL}`);
  console.error('Make sure the BioArena app is running (e.g., nx serve bixarena-app)');
  console.error('Error details:', error.message);
  await browser.close();
  process.exit(1);
}

console.log('Waiting for quest section to render...');
await page.waitForTimeout(PRE_WAIT_MS);

// Wait for the quest section to be visible
try {
  await page.waitForSelector('#quest-section-wrapper', { timeout: 5000 });
} catch (error) {
  console.error('Error: Quest section not found on page.');
  console.error('Make sure APP_COMMUNITY_QUEST_ENABLED=true is set.');
  await browser.close();
  process.exit(1);
}

// Switch to dark mode
console.log('Switching to dark mode...');
await page.evaluate(() => {
  // Gradio's theme toggle button
  const themeToggle = document.querySelector('button[id*="theme"]') ||
                      document.querySelector('button[aria-label*="theme"]') ||
                      document.querySelector('button[aria-label*="Theme"]');

  if (themeToggle) {
    // Check if we're in light mode and need to toggle
    const html = document.documentElement;
    const currentTheme = html.classList.contains('dark') ? 'dark' : 'light';
    if (currentTheme === 'light') {
      themeToggle.click();
    }
  }
});
// Wait for theme transition
await page.waitForTimeout(500);

console.log('Measuring quest section dimensions...');
const dimensions = await page.evaluate((portraitMode) => {
  const questSection = document.querySelector('#quest-section-wrapper');
  if (!questSection) {
    return { width: 1920, height: 1080, top: 0, left: 0 };
  }

  const bbox = questSection.getBoundingClientRect();

  // For portrait mode, measure the actual content after hiding elements
  if (portraitMode) {
    // Hide unwanted elements for portrait mode
    const elementsToHide = [
      '.quest-cta-btn',  // CTA buttons
      '#quest-cta-btn-authenticated',
      '#quest-cta-btn-login'
    ];

    elementsToHide.forEach(selector => {
      const elements = document.querySelectorAll(selector);
      elements.forEach(el => el.style.display = 'none');
    });

    // Apply portrait layout CSS first so we can measure correctly
    const style = document.createElement('style');
    style.textContent = `
      #quest-section-grid {
        display: flex !important;
        flex-direction: column !important;
        gap: 1.5rem !important;
      }
    `;
    document.head.appendChild(style);

    // Force layout recalculation
    questSection.offsetHeight;

    // Now measure the actual content in portrait layout
    const updatedBox = questSection.getBoundingClientRect();

    // Use actual content dimensions but ensure they fit portrait aspect ratio
    const contentWidth = Math.ceil(updatedBox.width);
    const contentHeight = Math.ceil(updatedBox.height);

    // Calculate dimensions for 0.8 aspect ratio
    // We want width/height = 0.8, so height = width/0.8
    let portraitWidth = contentWidth;
    let portraitHeight = Math.ceil(portraitWidth / 0.8);

    // If content is too tall, adjust based on height instead
    if (contentHeight > portraitHeight) {
      portraitHeight = contentHeight;
      portraitWidth = Math.ceil(portraitHeight * 0.8);
    }

    // Ensure even numbers
    portraitWidth = Math.floor(portraitWidth / 2) * 2;
    portraitHeight = Math.floor(portraitHeight / 2) * 2;

    console.log('Portrait mode dimensions:', {
      contentWidth,
      contentHeight,
      portraitWidth,
      portraitHeight,
      aspectRatio: (portraitWidth / portraitHeight).toFixed(2)
    });

    return {
      width: portraitWidth,
      height: portraitHeight,
      top: Math.max(0, Math.floor(updatedBox.top)),
      left: Math.max(0, Math.floor(updatedBox.left)),
      portraitMode: true
    };
  }

  // Standard landscape mode
  // Use scrollHeight/scrollWidth which includes overflow content
  const scrollHeight = questSection.scrollHeight;
  const scrollWidth = questSection.scrollWidth;

  // Use the larger of bbox or scroll dimensions
  const actualWidth = Math.max(bbox.width, scrollWidth);
  const actualHeight = Math.max(bbox.height, scrollHeight);

  // Debug: log the computed bounds
  console.log('Computed bounds:', {
    bboxTop: bbox.top,
    bboxLeft: bbox.left,
    bboxWidth: bbox.width,
    bboxHeight: bbox.height,
    scrollWidth,
    scrollHeight,
    actualWidth,
    actualHeight
  });

  // Manual adjustments based on visual inspection
  const cropTop = 0;       // No crop from top
  const cropLeft = 0;      // No crop from left
  const cropRight = 4;     // Trim 4px from right (remove white line)
  const cropBottom = -50;  // Add 50px to bottom (capture more content)

  const finalWidth = actualWidth - cropLeft - cropRight;
  const finalHeight = actualHeight - cropTop - cropBottom;
  const finalTop = bbox.top + cropTop;
  const finalLeft = bbox.left + cropLeft;

  // Round dimensions to even numbers for video encoding compatibility
  const evenWidth = Math.floor(finalWidth / 2) * 2;
  const evenHeight = Math.floor(finalHeight / 2) * 2;

  console.log('Final dimensions:', {
    finalWidth,
    finalHeight,
    evenWidth,
    evenHeight,
    finalTop,
    finalLeft
  });

  return {
    width: evenWidth,
    height: evenHeight,
    top: Math.max(0, Math.floor(finalTop)),
    left: Math.max(0, Math.floor(finalLeft)),
    portraitMode: false
  };
}, wantPortrait);

console.log(
  `Quest section: ${dimensions.width}x${dimensions.height} at (${dimensions.left}, ${dimensions.top})`,
);
await browser.close();

// ---------- Pass 2: Pre-position page BEFORE recording ----------
console.log('Pre-positioning page before recording...');
const recordingBrowser = await chromium.launch({ headless: true });

// Create context WITHOUT recording to position the page first
// For portrait mode, use a larger initial viewport to ensure content loads properly
const prepViewport = wantPortrait
  ? { width: 1920, height: 1080 }  // Start with full viewport for portrait
  : { width: dimensions.width, height: dimensions.height };

const prepContext = await recordingBrowser.newContext({
  viewport: prepViewport,
  colorScheme: 'dark',
  deviceScaleFactor: 2,
});

const prepPage = await prepContext.newPage();

console.log('Loading and positioning page...');
await prepPage.goto(APP_URL, { waitUntil: 'domcontentloaded', timeout: 30000 });
await prepPage.waitForTimeout(PRE_WAIT_MS);
await prepPage.waitForSelector('#quest-section-wrapper');

// Switch to dark mode
await prepPage.evaluate((portraitMode) => {
  const themeToggle = document.querySelector('button[id*="theme"]') ||
                      document.querySelector('button[aria-label*="theme"]') ||
                      document.querySelector('button[aria-label*="Theme"]');
  if (themeToggle) {
    const html = document.documentElement;
    const currentTheme = html.classList.contains('dark') ? 'dark' : 'light';
    if (currentTheme === 'light') {
      themeToggle.click();
    }
  }

  // Inject CSS and hide elements for portrait mode layout
  if (portraitMode) {
    // Find and hide specific elements by content
    const allDivs = document.querySelectorAll('div, h4, h3');
    allDivs.forEach(el => {
      const text = el.textContent;
      // Hide Contributor Tiers legend
      if (text.includes('Contributor Tiers') || text.includes('Champion') && text.includes('Knight')) {
        let parent = el.parentElement;
        // Hide the containing section
        if (parent) parent.style.display = 'none';
      }
      // Hide Credits section
      if (text.trim() === 'Credits' || (text.includes('Credits') && text.includes('Quest'))) {
        let parent = el.parentElement;
        if (parent) parent.style.display = 'none';
      }
    });

    // Hide CTA buttons
    const buttons = document.querySelectorAll('button');
    buttons.forEach(btn => {
      if (btn.textContent.includes('Contribute a Block')) {
        btn.style.display = 'none';
      }
    });

    // Inject CSS for portrait mode layout
    const style = document.createElement('style');
    style.textContent = `
      /* Portrait mode: stack elements vertically */
      #quest-section-grid {
        display: flex !important;
        flex-direction: column !important;
        gap: 1.5rem !important;
        max-width: 100% !important;
        margin: 0 !important;
        padding: 0 1rem !important;
      }

      /* Make carousel take full width */
      #quest-section-grid > div:first-child {
        width: 100% !important;
        max-width: 100% !important;
      }

      /* Progress section full width */
      #quest-section-grid > div:last-child {
        width: 100% !important;
        max-width: 100% !important;
      }

      /* Hide quest section wrapper padding for tighter framing */
      #quest-section-wrapper {
        padding: 1rem !important;
      }

      #quest-content-box {
        margin: 0 !important;
        padding: 1.5rem !important;
      }
    `;
    document.head.appendChild(style);
  }
}, wantPortrait);
await prepPage.waitForTimeout(500);

// Position to quest section and resize viewport for portrait
if (wantPortrait) {
  // Resize viewport to match recording dimensions
  await prepPage.setViewportSize({
    width: dimensions.width,
    height: dimensions.height
  });

  // Scroll to quest section after resize
  await prepPage.evaluate(({ top, left }) => {
    window.scrollTo({ top, left, behavior: 'instant' });
  }, { top: dimensions.top, left: dimensions.left });
} else {
  // Standard landscape positioning
  await prepPage.evaluate(({ top, left }) => {
    window.scrollTo({ top, left, behavior: 'instant' });
  }, { top: dimensions.top, left: dimensions.left });
}

// Wait for everything to stabilize
await prepPage.waitForTimeout(1000);

console.log('Page ready. Starting recording context...');

// Get the current page state/cookies to reuse
const cookies = await prepContext.cookies();
const storageState = await prepContext.storageState();

// Close prep context
await prepPage.close();
await prepContext.close();

// ---------- Pass 3: Record from pre-positioned state ----------
// Create NEW context WITH recording
// For portrait mode, record at 1080x1350 and inject CSS to trigger mobile layout
const portraitWidth = 1080;
const portraitHeight = 1350;

const recordViewport = wantPortrait
  ? { width: portraitWidth, height: portraitHeight }  // Record at target resolution
  : { width: 1920, height: 1080 };

const recordVideoSize = wantPortrait
  ? { width: portraitWidth, height: portraitHeight }  // Record at 1080x1350
  : { width: dimensions.width, height: dimensions.height };

const recordingContext = await recordingBrowser.newContext({
  viewport: recordViewport,
  colorScheme: 'dark',
  deviceScaleFactor: 2,
  storageState, // Restore state from prep
  recordVideo: {
    dir: outputDir,
    size: recordVideoSize,
  },
});

await recordingContext.addCookies(cookies);
const recordingPage = await recordingContext.newPage();

// Quickly navigate to same position
console.log('Recording starting...');
await recordingPage.goto(APP_URL, { waitUntil: 'domcontentloaded', timeout: 30000 });

// Speed up carousel rotation from 6 seconds to 4 seconds (must be done before carousel initializes)
if (wantPortrait) {
  await recordingPage.evaluate(() => {
    // Override setInterval to change carousel timing from 6000ms to 5000ms
    const originalSetInterval = window.setInterval;
    window.setInterval = function(callback, delay, ...args) {
      // If this looks like a carousel interval (6000ms), change to 5000ms
      if (delay === 6000) {
        console.log('Overriding carousel interval from 6000ms to 5000ms');
        return originalSetInterval(callback, 5000, ...args);
      }
      return originalSetInterval(callback, delay, ...args);
    };
  });
}

await recordingPage.waitForTimeout(1000); // Brief wait for page load
await recordingPage.waitForSelector('#quest-section-wrapper', { timeout: 5000 });

// For portrait mode, hide specific elements and compact spacing
if (wantPortrait) {
  console.log('Applying portrait mode element hiding and spacing adjustments...');

  await recordingPage.evaluate(() => {
    // Inject CSS to override responsive breakpoint and increase font sizes
    // The actual breakpoint is @media (max-width: 1000px) targeting #quest-section-grid
    const style = document.createElement('style');
    style.textContent = `
      /* Force mobile grid layout at 1080px (override max-width: 1000px breakpoint) */
      #quest-section-grid {
        grid-template-columns: 1fr !important;
      }

      /* Scale the entire quest section content by 1.15x */
      #quest-section-wrapper {
        transform: scale(1.15) !important;
        transform-origin: top center !important;
      }
    `;
    document.head.appendChild(style);

    // Hide text updates section
    const updatesContainer = document.querySelector('.quest-updates-container');
    if (updatesContainer) {
      updatesContainer.style.display = 'none';
      console.log('Hidden: quest-updates-container');
    }

    // Remove horizontal dividers to save vertical space and show more builders
    let questSection = document.querySelector('#quest-section-wrapper');
    let removedCount = 0;
    if (questSection) {
      // Get all divs in quest section
      const allDivs = questSection.querySelectorAll('div');

      Array.from(allDivs).forEach(div => {
        const inlineStyle = div.getAttribute('style') || '';

        // Look for the exact pattern: height: 1px with background variable
        if (inlineStyle.includes('height: 1px') || inlineStyle.includes('height:1px')) {
          if (inlineStyle.includes('--border-color') || inlineStyle.includes('background:')) {
            div.parentNode.removeChild(div);
            removedCount++;
          }
        }
      });
    }

    // Hide Contributor Tiers and Credits sections, but keep Builders section visible
    const allHeadings = document.querySelectorAll('h2, h3, .text-xl, .font-semibold');
    allHeadings.forEach(heading => {
      const text = heading.textContent.trim();

      // Hide "Contributor Tiers" legend
      if (text === 'Contributor Tiers') {
        let container = heading.parentElement;
        while (container && container.tagName !== 'SECTION' && !container.classList.contains('quest-section')) {
          if (container.classList.length > 0 || container.id) {
            container.style.display = 'none';
            console.log('Hidden: Contributor Tiers section');
            break;
          }
          container = container.parentElement;
        }
      }

      // Hide "Credits" section
      if (text === 'Minecraft Arena Designer' || text.includes('Quest Architect')) {
        let container = heading.parentElement;
        while (container && container.tagName !== 'SECTION' && !container.classList.contains('quest-section')) {
          if (container.classList.length > 0 || container.id) {
            container.style.display = 'none';
            console.log('Hidden: Credits section');
            break;
          }
          container = container.parentElement;
        }
      }
    });

    // Aggressively reduce spacing throughout the quest section for portrait mode
    if (questSection) {
      // Reduce gap on the main quest section if it's a flex container
      const sectionStyle = window.getComputedStyle(questSection);
      if (sectionStyle.display === 'flex') {
        questSection.style.gap = '16px';
        console.log('Reduced quest section flex gap to 16px');
      }

      // Find ALL divs in the quest section and reduce excessive spacing
      const allDivs = questSection.querySelectorAll('div');
      allDivs.forEach(div => {
        const children = Array.from(div.children);
        const style = window.getComputedStyle(div);

        // Check for pagination dots container
        const hasMultipleDots = children.length >= 3 && children.length <= 10 &&
          children.every(child => child.tagName === 'BUTTON' || child.tagName === 'SPAN');

        if (hasMultipleDots) {
          // Pagination dots - minimize spacing
          div.style.margin = '4px 0';
          div.style.padding = '0px';
          div.style.gap = '6px';
          console.log('Reduced pagination dots spacing');
          return; // Don't apply other spacing rules to pagination dots
        }

        // Skip if this looks like it contains images/carousel content
        const hasImage = div.querySelector('img') !== null;
        const hasVideo = div.querySelector('video') !== null;
        if (hasImage || hasVideo) {
          // Only reduce very large spacing on image containers
          if (parseInt(style.marginTop) > 20) {
            div.style.marginTop = '16px';
          }
          if (parseInt(style.marginBottom) > 20) {
            div.style.marginBottom = '16px';
          }
          return; // Don't aggressively reduce padding on image containers
        }

        // Reduce all margins/paddings greater than 12px for non-image elements
        if (parseInt(style.marginTop) > 12) {
          div.style.marginTop = '12px';
        }
        if (parseInt(style.marginBottom) > 12) {
          div.style.marginBottom = '12px';
        }
        if (parseInt(style.paddingTop) > 12) {
          div.style.paddingTop = '12px';
        }
        if (parseInt(style.paddingBottom) > 12) {
          div.style.paddingBottom = '12px';
        }

        // Reduce flex gaps greater than 16px
        if (style.display === 'flex' && parseInt(style.gap) > 16) {
          div.style.gap = '16px';
        }
      });

      // Compact the header/title area at the top
      const titleElements = questSection.querySelectorAll('h1, h2, .text-2xl, .text-3xl');
      titleElements.forEach(title => {
        title.style.marginTop = '8px';
        title.style.marginBottom = '8px';
        console.log('Reduced title spacing');
      });

      // Compact description/paragraph text
      const paragraphs = questSection.querySelectorAll('p');
      paragraphs.forEach(p => {
        p.style.marginTop = '6px';
        p.style.marginBottom = '6px';
      });

      console.log('Applied comprehensive spacing reduction for portrait mode');
    }
  });

  // Wait for layout to stabilize after hiding elements
  await recordingPage.waitForTimeout(500);

  // Second pass: Remove dividers again after layout stabilizes (they might be added dynamically)
  await recordingPage.evaluate(() => {
    const questSection = document.querySelector('#quest-section-wrapper');
    if (questSection) {
      const allDivs = questSection.querySelectorAll('div');
      Array.from(allDivs).forEach(div => {
        const inlineStyle = div.getAttribute('style') || '';
        // Match divs with height: 1px and background color variable
        if ((inlineStyle.includes('height: 1px') || inlineStyle.includes('height:1px')) &&
            (inlineStyle.includes('--border-color') || inlineStyle.includes('background'))) {
          div.remove();
        }
      });
    }
  });

  await recordingPage.waitForTimeout(100);
}

// Find where the quest section actually is in the viewport for cropping
console.log(`Finding quest section position in recording viewport...`);

const actualQuestPosition = await recordingPage.evaluate(() => {
  const questSection = document.querySelector('#quest-section-wrapper');
  if (!questSection) {
    // Try alternate selectors
    const altSelectors = ['[id*="quest"]', '.quest-section', '#quest-content-box'];
    for (const sel of altSelectors) {
      const elem = document.querySelector(sel);
      if (elem) {
        return { found: false, error: `#quest-section-wrapper not found, but found: ${sel}` };
      }
    }
    return { found: false, error: '#quest-section-wrapper not found, no alternates found either' };
  }

  const bbox = questSection.getBoundingClientRect();
  const styles = window.getComputedStyle(questSection);

  return {
    found: true,
    top: Math.floor(bbox.top),
    left: Math.floor(bbox.left),
    width: Math.floor(bbox.width),
    height: Math.floor(bbox.height),
    viewportHeight: window.innerHeight,
    viewportWidth: window.innerWidth,
    documentHeight: document.documentElement.scrollHeight,
    display: styles.display,
    visibility: styles.visibility,
    opacity: styles.opacity
  };
});

console.log(`Quest section actual position:`, actualQuestPosition);

// Update dimensions for cropping based on actual position in the recording
if (actualQuestPosition.found && actualQuestPosition.width > 0 && actualQuestPosition.height > 0) {
  // Quest section found with valid dimensions
  dimensions.top = actualQuestPosition.top;
  dimensions.left = actualQuestPosition.left;
  console.log(`Quest section position: (${dimensions.left}, ${dimensions.top})`);

  // If quest section is below the viewport, scroll it into view
  if (dimensions.top > 100) { // Leave some margin at top
    console.log(`Scrolling page to bring quest section into view...`);

    await recordingPage.evaluate(() => {
      const questSection = document.querySelector('#quest-section-wrapper');
      if (questSection) {
        questSection.scrollIntoView({ block: 'start', behavior: 'instant' });
      }
    });

    await recordingPage.waitForTimeout(500);

    // Re-measure position after scroll
    const newPosition = await recordingPage.evaluate(() => {
      const questSection = document.querySelector('#quest-section-wrapper');
      if (!questSection) return { top: 0, left: 0, scrollY: 0, found: false };
      const bbox = questSection.getBoundingClientRect();

      // Get the element's text content to verify it's the quest section
      const titleEl = questSection.querySelector('h2, h3');
      const title = titleEl ? titleEl.textContent.trim().substring(0, 30) : 'no title';

      return {
        top: Math.floor(bbox.top),
        left: Math.floor(bbox.left),
        width: Math.floor(bbox.width),
        height: Math.floor(bbox.height),
        scrollY: window.scrollY,
        scrollX: window.scrollX,
        found: true,
        title
      };
    });

    dimensions.top = newPosition.top;
    dimensions.left = newPosition.left;
    console.log(`After scroll - Quest section "${newPosition.title}" at: (${dimensions.left}, ${dimensions.top}), scroll: (${newPosition.scrollX}, ${newPosition.scrollY}), size: ${newPosition.width}x${newPosition.height}`);

    // Take a debug screenshot
    await recordingPage.screenshot({ path: path.join(outputDir, 'debug-after-scroll.png') });
    console.log('Debug screenshot saved: debug-after-scroll.png');
  }
} else if (!actualQuestPosition.found) {
  console.error(`Error: ${actualQuestPosition.error}`);
  process.exit(1);
} else {
  console.error(`Quest section has 0 dimensions - display: ${actualQuestPosition.display}, visibility: ${actualQuestPosition.visibility}, opacity: ${actualQuestPosition.opacity}`);
  process.exit(1);
}

// Start recording the carousel immediately
console.log(`Recording for ${RECORD_MS / 1000}s...`);
await recordingPage.waitForTimeout(RECORD_MS);

// Close page/context to finalize the video
const videoObj = await recordingPage.video();
await recordingPage.close();
await recordingContext.close();
await recordingBrowser.close();

if (!videoObj) {
  console.error(
    'Error: Playwright did not produce a video. Is recordVideo enabled on the context?',
  );
  process.exit(1);
}

// After page is closed, path() becomes available
const recordedPath = await videoObj.path();

// Temporary files
const rawWebmFile = path.join(outputDir, `bixarena-quest-raw.webm`);
await fs.rename(recordedPath, rawWebmFile);
console.log(`Recorded raw WebM: ${rawWebmFile}`);

// ---------- Trim video to remove loading artifacts ----------
await ensureFfmpeg();
const trimSeconds = TRIM_START_MS / 1000; // Convert ms to seconds for FFmpeg
console.log(`Trimming first ${TRIM_START_MS}ms (${trimSeconds}s) from video...`);
// Note: For accurate trimming, we re-encode instead of using -c copy
// -ss AFTER -i for frame-accurate seeking
const trimCmd =
  `ffmpeg -hide_banner -loglevel error -i "${rawWebmFile}" -ss ${trimSeconds} ` +
  `-c:v libvpx-vp9 -crf 30 -b:v 0 "${webmFile}" -y`;

try {
  await execAsync(trimCmd);
  console.log('Trimming complete. Cleaning up raw WebM...');
  await fs.unlink(rawWebmFile);
} catch (error) {
  console.error('Error trimming video:', error?.stderr || error);
  console.log(`Raw WebM kept at: ${rawWebmFile}`);
  process.exit(1);
}

// ---------- Portrait mode recorded at native 1080x1350 ----------
if (wantPortrait) {
  // Portrait mode: video recorded at 1080x1350 with CSS zoom to trigger mobile layout
  console.log(`Portrait video recorded at 1080x1350 - no scaling needed.`);
  dimensions.width = 1080;
  dimensions.height = 1350;
}

// ---------- Convert to GIF or MP4 (if requested) ----------
if (wantGif) {
  console.log('Converting WebM to GIF...');
  // Calculate scaled dimensions
  const scaledWidth = Math.round(dimensions.width * (SCALE_PERCENT / 100));

  // Palettegen/paletteuse chain for quality; lanczos scaling; user FPS
  const ffmpegCmd =
    `ffmpeg -hide_banner -loglevel error -i "${webmFile}" ` +
    `-vf "fps=${GIF_FPS},scale=${scaledWidth}:-1:flags=lanczos,` +
    `split[s0][s1];[s0]palettegen=max_colors=256:stats_mode=diff[p];` +
    `[s1][p]paletteuse=dither=bayer:bayer_scale=5:diff_mode=rectangle" ` +
    `-loop 0 "${outputFile}" -y`;

  try {
    await execAsync(ffmpegCmd);
    console.log('GIF conversion complete. Cleaning up WebM...');
    await fs.unlink(webmFile);
  } catch (error) {
    console.error('Error converting to GIF:', error?.stderr || error);
    console.log(`WebM kept at: ${webmFile}`);
    process.exit(1);
  }
  console.log(`GIF saved to: ${outputFile}`);
} else if (wantMp4) {
  console.log('Converting WebM to MP4...');

  let ffmpegCmd;
  if (wantPortrait) {
    // Portrait mode: WebM is already at correct size (1080x1350), just convert codec
    ffmpegCmd =
      `ffmpeg -hide_banner -loglevel error -i "${webmFile}" ` +
      `-vf "fps=${GIF_FPS}" ` +
      `-c:v libx264 -crf 20 -preset slow ` +
      `-pix_fmt yuv420p -colorspace bt709 -color_primaries bt709 -color_trc bt709 ` +
      `-movflags +faststart "${outputFile}" -y`;
  } else {
    // Landscape mode: Calculate scaled dimensions and crop
    const scaledWidth = Math.round(dimensions.width * (SCALE_PERCENT / 100));
    const cropWidth = Math.floor(scaledWidth / 2) * 2 - 4;  // Ensure even number, crop 4px to remove artifact

    ffmpegCmd =
      `ffmpeg -hide_banner -loglevel error -i "${webmFile}" ` +
      `-vf "fps=${GIF_FPS},scale=${scaledWidth}:-1:flags=lanczos,crop=${cropWidth}:ih:0:0" ` +
      `-c:v libx264 -crf 20 -preset slow ` +
      `-pix_fmt yuv420p -colorspace bt709 -color_primaries bt709 -color_trc bt709 ` +
      `-movflags +faststart "${outputFile}" -y`;
  }

  try {
    await execAsync(ffmpegCmd);
    console.log('MP4 conversion complete.');
    console.log(`WebM kept at: ${webmFile} (for debugging)`);
    // await fs.unlink(webmFile); // Keep for debugging
  } catch (error) {
    console.error('Error converting to MP4:', error?.stderr || error);
    console.log(`WebM kept at: ${webmFile}`);
    process.exit(1);
  }
  console.log(`MP4 saved to: ${outputFile}`);
} else {
  console.log(`WEBM saved to: ${webmFile}`);
}
