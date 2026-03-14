let cachedConfig: unknown = null;

export async function prefetchConfig<T = unknown>(url = './config/config.json'): Promise<T | null> {
  try {
    const response = await fetch(url);
    if (response.ok) {
      cachedConfig = await response.json();
      return cachedConfig as T;
    } else {
      console.warn(`Failed to prefetch config: ${response.status}`);
    }
  } catch (error) {
    console.error('Failed to prefetch config:', error);
  }
  return null;
}

export function getCachedConfig<T = unknown>(): T | null {
  return cachedConfig as T | null;
}
