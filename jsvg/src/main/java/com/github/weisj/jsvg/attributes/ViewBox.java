/*
 * MIT License
 *
 * Copyright (c) 2021 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.weisj.jsvg.attributes;

import java.awt.geom.Rectangle2D;

import com.github.weisj.jsvg.geometry.size.Length;

public final class ViewBox extends Rectangle2D.Float {

    public ViewBox(float[] viewBox) {
        super(viewBox[0], viewBox[1], viewBox[2], viewBox[3]);
    }

    public ViewBox(float x, float y, float w, float h) {
        super(x, y, w, h);
    }

    public ViewBox(float w, float h) {
        this(0, 0, w, h);
    }

    public boolean hasUnspecifiedWidth() {
        return Length.isUnspecified(width);
    }

    public boolean hasUnspecifiedHeight() {
        return Length.isUnspecified(height);
    }

    @Override
    public String toString() {
        return "ViewBox[" + x + "," + y + "," + width + "," + height + "]";
    }

    public final float normedDiagonalLength() {
        // Per spec to be computed as: sqrt((width)**2 + (height)**2)/sqrt(2)
        // Dividing by 2 inside the sqrt is more efficient.
        return (float) Math.sqrt((width * width + height * height) / 2);
    }
}