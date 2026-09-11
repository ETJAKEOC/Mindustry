package arc.backend.robovm;

/**
 * @param x               Offset from top left corner in points
 * @param width           Dimensions of drawing surface in points
 * @param backBufferWidth Dimensions of drawing surface in pixels
 */
public record IOSScreenBounds(int x, int y, int width, int height, int backBufferWidth,
                              int backBufferHeight) {

}