// capture-quest.mjs
// Captures the BioArena Community Quest section from localhost:8100
//
// Usage:
//   node capture-quest.mjs
//   node capture-quest.mjs --gif
//   node capture-quest.mjs --mp4
//   node capture-quest.mjs --mp4 --fps=15 --record-ms=30000 --trim-start=6
//   node capture-quest.mjs --url=http://localhost:7860
//
// Notes:
// - Captures just the quest section from the running BioArena app
// - --gif creates a GIF (default: WebM is kept).
// - --mp4 creates an MP4 video (RECOMMENDED for LinkedIn - much smaller than GIF).
// - --fps controls output framerate (default: 15).
// - --record-ms controls how long to record (default: 12000 ms = 2 carousel rotations at 6s each).
// - --trim-start removes N seconds from start to eliminate loading artifacts (default: 5).
// - --scale controls output size as percentage (default: 100, use 75-90 for smaller files).
// - --url specifies the app URL (default: http://localhost:8100).
// - Quest carousel rotates every 6 seconds by default (5 images = 30 seconds total).
// - For LinkedIn (< 5 MB): use --mp4 (produces files ~500KB-2MB for 30s videos).
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
const wantMp4 = hasFlag(args, 'mp4');
const format = wantGif ? 'gif' : wantMp4 ? 'mp4' : 'webm';
const GIF_FPS = parseFlagNum(args, 'fps', 15);
// Default: 12 seconds = 2 full carousel rotations (6s each)
const RECORD_MS = parseFlagNum(args, 'record-ms', 12000);
const APP_URL = parseFlagString(args, 'url', 'http://localhost:8100');
// Wait 3 seconds for initial render and potential redirects
const PRE_WAIT_MS = 3000;
// Trim first N seconds from video to remove loading artifacts
const TRIM_START_SECONDS = parseFlagNum(args, 'trim-start', 5);
// Scale output (percentage, 100 = original size, 75 = 75% of original)
const SCALE_PERCENT = parseFlagNum(args, 'scale', 100);

// Output files
const outputDir = __dirname;
const webmFile = path.join(outputDir, `bixarena-quest.webm`);
const outputFile = path.join(outputDir, `bixarena-quest.${format}`);

console.log(`App URL: ${APP_URL}`);
console.log(`Output format: ${format.toUpperCase()}`);
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

// ---------- Pass 2: Record with extra time at start ----------
console.log('Starting video recording...');
const recordingBrowser = await chromium.launch({ headless: true });

// Create context with video recording
// Record EXTRA time to capture loading, then we'll trim it with FFmpeg
const recordingContext = await recordingBrowser.newContext({
  viewport: { width: dimensions.width, height: dimensions.height },
  colorScheme: 'dark',
  deviceScaleFactor: 2,
  recordVideo: {
    dir: outputDir,
    size: { width: dimensions.width, height: dimensions.height },
  },
});

const recordingPage = await recordingContext.newPage();

console.log('Loading page and positioning...');
await recordingPage.goto(APP_URL, { waitUntil: 'domcontentloaded', timeout: 30000 });
await recordingPage.waitForTimeout(PRE_WAIT_MS);
await recordingPage.waitForSelector('#quest-section-wrapper');

// Switch to dark mode
await recordingPage.evaluate(() => {
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
});
await recordingPage.waitForTimeout(500);

// Position instantly to quest section
await recordingPage.evaluate(({ top, left }) => {
  window.scrollTo({ top, left, behavior: 'instant' });
}, { top: dimensions.top, left: dimensions.left });

// Wait for rendering to stabilize before starting the "real" recording
await recordingPage.waitForTimeout(1000);

// Record carousel animation (+ extra time that will be trimmed)
const totalRecordMs = RECORD_MS + (TRIM_START_SECONDS * 1000);
console.log(`Recording for ${totalRecordMs / 1000}s (will trim first ${TRIM_START_SECONDS}s)...`);
await recordingPage.waitForTimeout(totalRecordMs);

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

  // H.264 encoding with high compression for small file size
  // -crf 28 balances quality and file size (lower = better quality, 18-28 is good range)
  // -preset slow gives better compression at same quality
  // -pix_fmt yuv420p ensures compatibility with all players
  // -movflags +faststart enables progressive download (video starts before fully loaded)
  // -stream_loop -1 would loop infinitely in some players, but not widely supported
  // Note: LinkedIn and most social platforms auto-loop videos in feed regardless
  const ffmpegCmd =
    `ffmpeg -hide_banner -loglevel error -i "${webmFile}" ` +
    `-vf "fps=${GIF_FPS},scale=${scaledWidth}:-1:flags=lanczos" ` +
    `-c:v libx264 -crf 28 -preset slow -pix_fmt yuv420p ` +
    `-movflags +faststart "${outputFile}" -y`;

  try {
    await execAsync(ffmpegCmd);
    console.log('MP4 conversion complete. Cleaning up WebM...');
    await fs.unlink(webmFile);
  } catch (error) {
    console.error('Error converting to MP4:', error?.stderr || error);
    console.log(`WebM kept at: ${webmFile}`);
    process.exit(1);
  }
  console.log(`MP4 saved to: ${outputFile}`);
} else {
  console.log(`WEBM saved to: ${webmFile}`);
}
