// capture-diagram.mjs
// Usage:
//   node capture-diagram.mjs diagram.html
//   node capture-diagram.mjs diagram.html --gif
//   node capture-diagram.mjs diagram.html --gif --trim-start=2 --fps=24 --record-ms=5000 --pre-wait-ms=8000
//
// Notes:
// - --gif creates a GIF (otherwise WebM is kept).
// - --trim-start cuts the first N seconds (default: 2). Set to 0 to disable trimming.
// - --fps controls GIF framerate (default: 24).
// - --pre-wait-ms waits for your diagram to render (default: 8000 ms).
// - --record-ms controls how long to record (default: 5000 ms).
//
// Requirements:
// - Playwright (Chromium) installed: `pnpm dlx playwright install chromium --with-deps`
// - ffmpeg available on PATH for trimming and/or GIF conversion

import { chromium } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';
import { execSync, exec } from 'child_process';
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

function hasFlag(args, name) {
  return args.some((a) => a === `--${name}`);
}

async function fileExists(p) {
  try {
    await fs.access(p);
    return true;
  } catch {
    return false;
  }
}

async function ensureFfmpeg() {
  try {
    await execAsync('ffmpeg -version');
  } catch {
    console.error('Error: ffmpeg is not available on PATH. Please install ffmpeg.');
    process.exit(1);
  }
}

// Optional: ensure Playwright browsers are installed
function ensurePlaywrightBrowsers() {
  console.log('Ensuring Playwright Chromium is installed...');
  try {
    execSync('playwright install chromium --with-deps', { stdio: 'inherit' });
  } catch (error) {
    console.error('Failed to install Playwright browsers:', error.message);
    process.exit(1);
  }
}

// ---------- CLI parsing ----------
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const args = process.argv.slice(2);
const inputFile = args.find((arg) => !arg.startsWith('--'));
if (!inputFile) {
  console.error(
    'Usage: node capture-diagram.mjs <html-file> [--gif] [--trim-start=SECONDS] [--fps=N] [--pre-wait-ms=MS] [--record-ms=MS]',
  );
  process.exit(1);
}

const wantGif = hasFlag(args, 'gif');
const format = wantGif ? 'gif' : 'webm';
const TRIM_START = parseFlagNum(args, 'trim-start', 2); // default skip first 2s
const GIF_FPS = parseFlagNum(args, 'fps', 24);
const PRE_WAIT_MS = parseFlagNum(args, 'pre-wait-ms', 8000);
const RECORD_MS = parseFlagNum(args, 'record-ms', 5000);

// ---------- Resolve paths & validate ----------
const htmlFile = path.resolve(inputFile);
const htmlDir = path.dirname(htmlFile);
const htmlBasename = path.basename(htmlFile, '.html');
const webmFile = path.join(htmlDir, `${htmlBasename}.webm`);
const outputFile = path.join(htmlDir, `${htmlBasename}.${format}`);

if (!(await fileExists(htmlFile))) {
  console.error(`Error: File not found: ${htmlFile}`);
  process.exit(1);
}

console.log(`Input file: ${htmlFile}`);
console.log(`Output format: ${format.toUpperCase()}`);
console.log(`Pre-wait: ${PRE_WAIT_MS}ms, Record: ${RECORD_MS}ms, Trim start: ${TRIM_START}s`);

// ensurePlaywrightBrowsers(); // uncomment if you want to auto-install

// ---------- Pass 1: measure dimensions ----------
const browser = await chromium.launch({ headless: true });
const context = await browser.newContext();
const page = await context.newPage();

console.log('Loading diagram to measure dimensions...');
await page.goto('file://' + htmlFile, { waitUntil: 'networkidle' });

console.log('Waiting for diagram to render...');
await page.waitForTimeout(PRE_WAIT_MS);

const dimensions = await page.evaluate(() => {
  const svg = document.querySelector('svg');
  if (!svg) {
    // Fallback if no SVG found
    return { width: 1920, height: 1080 };
  }
  const bbox = svg.getBoundingClientRect();
  const padding = 40;
  return {
    width: Math.ceil(bbox.width + padding * 2),
    height: Math.ceil(bbox.height + padding * 2),
  };
});

console.log(`Diagram dimensions: ${dimensions.width}x${dimensions.height}`);
await browser.close();

// ---------- Pass 2: record video ----------
console.log('Starting recording...');
const recordingBrowser = await chromium.launch({ headless: true });
const recordingContext = await recordingBrowser.newContext({
  viewport: { width: dimensions.width, height: dimensions.height },
  deviceScaleFactor: 2,
  recordVideo: {
    dir: htmlDir,
    size: { width: dimensions.width, height: dimensions.height },
  },
});

const recordingPage = await recordingContext.newPage();

console.log('Loading diagram with recording...');
await recordingPage.goto('file://' + htmlFile, { waitUntil: 'networkidle' });
await recordingPage.waitForTimeout(PRE_WAIT_MS);

// Record animation frames
console.log('Recording...');
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

// ---------- Optional: trim the start ----------
if (TRIM_START > 0) {
  await ensureFfmpeg();
  console.log(`Trimming first ${TRIM_START}s...`);
  const trimmedWebm = path.join(htmlDir, `${htmlBasename}.trimmed.webm`);

  // Re-encode for accurate cut even when not on a keyframe
  // (Slower but avoids visible jitter that can happen with stream-copy.)
  const trimCmd = `ffmpeg -hide_banner -loglevel error -i "${webmFile}" -ss ${TRIM_START} -c:v libvpx-vp9 -pix_fmt yuv420p -an "${trimmedWebm}" -y`;
  try {
    await execAsync(trimCmd);
    await fs.unlink(webmFile);
    await fs.rename(trimmedWebm, webmFile);
    console.log('Trim complete.');
  } catch (error) {
    console.error('Error trimming video:', error?.stderr || error);
    console.log('Proceeding with untrimmed file.');
  }
}

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
