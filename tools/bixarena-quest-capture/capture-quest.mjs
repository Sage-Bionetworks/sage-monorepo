// capture-quest.mjs
// Captures the BioArena Community Quest section from localhost:8100
//
// Usage:
//   node capture-quest.mjs
//   node capture-quest.mjs --gif
//   node capture-quest.mjs --mp4
//   node capture-quest.mjs --mp4 --portrait --record-ms=30000
//   node capture-quest.mjs --mp4 --fps=15 --record-ms=30000 --trim-start=6
//   node capture-quest.mjs --url=http://localhost:7860
//
// Notes:
// - Captures just the quest section from the BioArena app
// - --gif creates a GIF (default: WebM is kept).
// - --mp4 creates an MP4 video (RECOMMENDED for LinkedIn - much smaller than GIF).
// - --portrait creates 0.8 aspect ratio portrait video (1080x1350) showing carousel, progress, and contributors.
// - --fps controls output framerate (default: 15).
// - --record-ms controls how long to record (default: 12000 ms = 2 carousel rotations at 6s each).
// - --trim-start removes N seconds from start to eliminate loading artifacts (default: 2).
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
// Trim first N seconds from video to remove loading artifacts
const TRIM_START_SECONDS = parseFlagNum(args, 'trim-start', 2);
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
console.log(`Trim first: ${TRIM_START_SECONDS}s (to remove loading artifacts)`);
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
// For portrait mode, record at full size and crop later with FFmpeg
const recordVideoSize = wantPortrait
  ? { width: 1920, height: 1080 }  // Record full size for portrait
  : { width: dimensions.width, height: dimensions.height };

const recordingContext = await recordingBrowser.newContext({
  viewport: { width: 1920, height: 1080 },  // Always use full viewport for recording
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
await recordingPage.waitForTimeout(1000); // Brief wait for page load
await recordingPage.waitForSelector('#quest-section-wrapper', { timeout: 5000 });

// For portrait mode, we'll hide elements but skip the layout CSS to avoid breaking the page
// We'll crop to portrait dimensions in post-processing instead
if (wantPortrait) {
  await recordingPage.evaluate(() => {
    // Hide CTA buttons
    const buttons = document.querySelectorAll('button');
    buttons.forEach(btn => {
      if (btn.textContent.includes('Contribute a Block')) {
        btn.style.display = 'none';
      }
    });

    // Find and hide tier legend and credits by content - be more specific to avoid hiding quest section
    const questSection = document.querySelector('#quest-section-wrapper');
    if (!questSection) return;

    // Only search within the quest section
    const allElements = questSection.querySelectorAll('div, h4, h3');
    allElements.forEach(el => {
      const text = el.textContent.trim();

      // Hide Contributor Tiers legend - only if it's a heading
      if ((el.tagName === 'H4' || el.tagName === 'H3') &&
          (text === 'Contributor Tiers' || text.includes('Contributor Tiers'))) {
        // Hide the parent container
        let parent = el.parentElement;
        while (parent && parent !== questSection) {
          // Look for the tier legend container (usually has class or specific structure)
          if (parent.querySelector('h4, h3') === el) {
            parent.style.display = 'none';
            break;
          }
          parent = parent.parentElement;
        }
      }

      // Hide Credits section - only exact match on heading
      if ((el.tagName === 'H4' || el.tagName === 'H3') && text === 'Credits') {
        let parent = el.parentElement;
        if (parent && parent !== questSection) {
          parent.style.display = 'none';
        }
      }
    });
  });
  await recordingPage.waitForTimeout(1000);
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
      if (!questSection) return { top: 0, left: 0 };
      const bbox = questSection.getBoundingClientRect();
      return {
        top: Math.floor(bbox.top),
        left: Math.floor(bbox.left),
        scrollY: window.scrollY
      };
    });

    dimensions.top = newPosition.top;
    dimensions.left = newPosition.left;
    console.log(`After scroll - Quest section at: (${dimensions.left}, ${dimensions.top}), window.scrollY: ${newPosition.scrollY}`);
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
console.log(`Trimming first ${TRIM_START_SECONDS}s from video...`);
// Note: -ss AFTER -i for frame-accurate seeking (vs keyframe seeking before -i)
// Using -c copy is fast but may be slightly imprecise at trim point
const trimCmd =
  `ffmpeg -hide_banner -loglevel error -i "${rawWebmFile}" -ss ${TRIM_START_SECONDS} ` +
  `-c copy "${webmFile}" -y`;

try {
  await execAsync(trimCmd);
  console.log('Trimming complete. Cleaning up raw WebM...');
  await fs.unlink(rawWebmFile);
} catch (error) {
  console.error('Error trimming video:', error?.stderr || error);
  console.log(`Raw WebM kept at: ${rawWebmFile}`);
  process.exit(1);
}

// ---------- Crop for portrait mode if needed ----------
if (wantPortrait) {
  const uncropWebm = path.join(outputDir, `bixarena-quest-uncropped.webm`);
  await fs.rename(webmFile, uncropWebm);

  // The recorded video is 1920x1080
  const recordedWidth = 1920;
  const recordedHeight = 1080;

  // For portrait mode with 0.8 aspect ratio (width/height = 0.8)
  // We're limited by the recording height of 1080
  // So: width = height * 0.8 = 1080 * 0.8 = 864
  const portraitWidth = Math.floor((recordedHeight * 0.8) / 2) * 2; // 864, ensure even
  const portraitHeight = recordedHeight; // Use full height

  // Center the crop horizontally in the quest section
  // The quest section is at dimensions.left, we want to center our portrait crop within it
  const cropX = Math.max(0, Math.floor(dimensions.left + (dimensions.width - portraitWidth) / 2));

  // Crop from the top of the quest section
  const cropY = Math.max(0, dimensions.top);

  console.log(`Cropping to portrait: ${portraitWidth}x${portraitHeight} at (${cropX},${cropY}) from ${recordedWidth}x${recordedHeight}...`);
  console.log(`Portrait aspect ratio: ${(portraitWidth / portraitHeight).toFixed(2)} (target: 0.80)`);

  // Crop the video to portrait dimensions
  const cropCmd =
    `ffmpeg -hide_banner -loglevel error -i "${uncropWebm}" ` +
    `-vf "crop=${portraitWidth}:${portraitHeight}:${cropX}:${cropY}" ` +
    `-c:v libvpx-vp9 -crf 30 -b:v 0 "${webmFile}" -y`;

  try {
    await execAsync(cropCmd);
    console.log('Portrait crop complete. Cleaning up uncropped WebM...');
    await fs.unlink(uncropWebm);

    // Update dimensions to reflect actual crop
    dimensions.width = portraitWidth;
    dimensions.height = portraitHeight;
  } catch (error) {
    console.error('Error cropping video:', error?.stderr || error);
    console.log(`Uncropped WebM kept at: ${uncropWebm}`);
    process.exit(1);
  }
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
  // Calculate scaled dimensions
  const scaledWidth = Math.round(dimensions.width * (SCALE_PERCENT / 100));

  // Crop 4 pixels from the right to remove white line artifact
  const cropWidth = Math.floor(scaledWidth / 2) * 2 - 4;  // Ensure even number

  // H.264 encoding with high quality settings for better blacks and text readability
  // -crf 20 = high quality (lower = better quality, 18-23 is very good range)
  // -preset slow = better compression at same quality
  // -pix_fmt yuv420p = compatibility with all players
  // -colorspace bt709 = modern color space for better color accuracy
  // -color_primaries bt709 = standard HD primaries
  // -color_trc bt709 = standard HD transfer characteristics
  // -movflags +faststart = progressive download
  const ffmpegCmd =
    `ffmpeg -hide_banner -loglevel error -i "${webmFile}" ` +
    `-vf "fps=${GIF_FPS},scale=${scaledWidth}:-1:flags=lanczos,crop=${cropWidth}:ih:0:0" ` +
    `-c:v libx264 -crf 20 -preset slow ` +
    `-pix_fmt yuv420p -colorspace bt709 -color_primaries bt709 -color_trc bt709 ` +
    `-movflags +faststart "${outputFile}" -y`;

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
