/**
 * Parse image URLs from the images field.
 * Supports both JSON array format (new) and comma-separated format (legacy).
 * @param {string} images - The images field value from the database
 * @returns {string[]} Array of image URLs
 */
export function parseImages(images) {
  if (!images || images.trim() === '') {
    return []
  }
  try {
    // Try parsing as JSON array first
    const parsed = JSON.parse(images)
    if (Array.isArray(parsed)) {
      return parsed.filter(url => url && url.trim())
    }
  } catch (e) {
    // Not JSON, fall through to legacy format
  }
  // Legacy: comma-separated format
  return images.split(',').filter(url => url && url.trim()).map(url => url.trim())
}

/**
 * Get the first image URL.
 * @param {string} images - The images field value
 * @returns {string} First image URL or empty string
 */
export function getFirstImage(images) {
  const list = parseImages(images)
  return list[0] || ''
}

/**
 * Handle image load error - replace with a placeholder.
 * @param {Event} event - The error event from img tag
 */
export function handleImageError(event) {
  event.target.src = 'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">' +
    '<rect width="200" height="200" fill="#F5F7FA"/>' +
    '<text x="100" y="100" text-anchor="middle" dominant-baseline="central" font-size="14" fill="#C0C4CC">图片加载失败</text>' +
    '</svg>'
  )
  event.target.onerror = null // prevent infinite loop
}