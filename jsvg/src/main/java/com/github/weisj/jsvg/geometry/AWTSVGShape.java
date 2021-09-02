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
package com.github.weisj.jsvg.geometry;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.geometry.size.MeasureContext;
import com.github.weisj.jsvg.util.Todo;

public class AWTSVGShape implements SVGShape {
    public static final SVGShape EMPTY = new AWTSVGShape(new Rectangle(), 0f);
    private final @NotNull Shape shape;
    private Rectangle2D bounds;

    private double pathLength;

    public AWTSVGShape(@NotNull Shape shape) {
        this(shape, Double.NaN);
    }

    private AWTSVGShape(@NotNull Shape shape, double pathLength) {
        this.shape = shape;
        this.pathLength = pathLength;
    }

    @Override
    public @NotNull Shape shape(@NotNull MeasureContext measureContext, boolean validate) {
        return shape;
    }

    @Override
    public Rectangle2D bounds(@NotNull MeasureContext measureContext, boolean validate) {
        if (bounds == null) bounds = shape.getBounds2D();
        return bounds;
    }

    @Override
    public double pathLength(@NotNull MeasureContext measureContext) {
        if (Double.isNaN(pathLength)) {
            pathLength = computePathLength();
        }
        return pathLength;
    }

    private double computePathLength() {
        return Todo.todo();
    }
}
