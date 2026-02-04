// capture-quest.mjs
// Captures the BioArena Community Quest section from localhost:8100
//
// Usage:
//   node capture-quest.mjs
//   node capture-quest.mjs --gif
//   node capture-quest.mjs --gif --fps=15 --record-ms=12000
//   node capture-quest.mjs --url=http://localhost:7860
//
// Notes:
// - Captures just the quest section from the running BioArena app
// - --gif creates a GIF (otherwise WebM is kept).
// - --fps controls GIF framerate (default: 15).
// - --record-ms controls how long to record (default: 12000 ms = 2 carousel rotations at 6s each).
// - --url specifies the app URL (default: http://localhost:8100).
// - Quest carousel rotates every 6 seconds by default.
//
// Requirements:
// - BioArena app running on localhost:8100
// - Playwright (Chromium) installed: `pnpm dlx playwright install chromium --with-deps`
// - ffmpeg available on PATH for GIF conversion

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
const format = wantGif ? 'gif' : 'webm';
const GIF_FPS = parseFlagNum(args, 'fps', 15);
// Default: 12 seconds = 2 full carousel rotations (6s each)
const RECORD_MS = parseFlagNum(args, 'record-ms', 12000);
const APP_URL = parseFlagString(args, 'url', 'http://localhost:8100');
// Wait 3 seconds for initial render and potential redirects
const PRE_WAIT_MS = 3000;

// Output files
const outputDir = __dirname;
const webmFile = path.join(outputDir, `bixarena-quest.webm`);
const outputFile = path.join(outputDir, `bixarena-quest.${format}`);

console.log(`App URL: ${APP_URL}`);
console.log(`Output format: ${format.toUpperCase()}`);
console.log(`Record duration: ${RECORD_MS}ms (${RECORD_MS / 1000}s)`);
if (wantGif) {
  console.log(`GIF framerate: ${GIF_FPS} fps`);
}

// ---------- Pass 1: measure quest section dimensions ----------
const browser = await chromium.launch({ headless: true });
const context = await browser.newContext({
  viewport: { width: 1920, height: 1080 },
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

console.log('Measuring quest section dimensions...');
const dimensions = await page.evaluate(() => {
  const questSection = document.querySelector('#quest-section-wrapper');
  if (!questSection) {
    return { width: 1920, height: 1080, top: 0, left: 0 };
  }

  const bbox = questSection.getBoundingClientRect();
  const padding = 20;

  return {
    width: Math.ceil(bbox.width + padding * 2),
    height: Math.ceil(bbox.height + padding * 2),
    top: Math.max(0, Math.floor(bbox.top - padding)),
    left: Math.max(0, Math.floor(bbox.left - padding)),
  };
});

console.log(
  `Quest section: ${dimensions.width}x${dimensions.height} at (${dimensions.left}, ${dimensions.top})`,
);
await browser.close();

// ---------- Pass 2: record video with clipping ----------
console.log('Starting recording...');
const recordingBrowser = await chromium.launch({ headless: true });

// Use full viewport size for navigation, then we'll clip during recording
const recordingContext = await recordingBrowser.newContext({
  viewport: { width: 1920, height: 1080 },
  deviceScaleFactor: 2,
  recordVideo: {
    dir: outputDir,
    size: { width: dimensions.width, height: dimensions.height },
  },
});

const recordingPage = await recordingContext.newPage();

console.log('Loading page for recording...');
await recordingPage.goto(APP_URL, { waitUntil: 'domcontentloaded', timeout: 30000 });
await recordingPage.waitForTimeout(PRE_WAIT_MS);

// Wait for quest section
await recordingPage.waitForSelector('#quest-section-wrapper');

// Scroll to quest section and center it in viewport
await recordingPage.evaluate(() => {
  const questSection = document.querySelector('#quest-section-wrapper');
  if (questSection) {
    questSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
});

// Wait for scroll to finish
await recordingPage.waitForTimeout(1000);

// Set viewport clip to capture only the quest section
await recordingPage.setViewportSize({
  width: dimensions.width,
  height: dimensions.height,
});

// Scroll back to quest section after viewport change
await recordingPage.evaluate(({ top, left }) => {
  window.scrollTo(left, top);
}, { top: dimensions.top, left: dimensions.left });

// Record carousel animation
console.log(`Recording for ${RECORD_MS / 1000} seconds...`);
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

// Move/rename recorded file to deterministic name
await fs.rename(recordedPath, webmFile);
console.log(`Recorded WebM: ${webmFile}`);

// ---------- Convert to GIF (if requested) ----------
if (wantGif) {
  await ensureFfmpeg();
  console.log('Converting WebM to GIF...');
  // Palettegen/paletteuse chain for quality; lanczos scaling; user FPS
  const ffmpegCmd =
    `ffmpeg -hide_banner -loglevel error -i "${webmFile}" ` +
    `-vf "fps=${GIF_FPS},scale=${dimensions.width}:-1:flags=lanczos,` +
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
} else {
  console.log(`WEBM saved to: ${webmFile}`);
}
