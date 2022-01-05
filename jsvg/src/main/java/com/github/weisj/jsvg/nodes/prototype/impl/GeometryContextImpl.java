/*
 * MIT License
 *
 * Copyright (c) 2022 Jannis Weis
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
package com.github.weisj.jsvg.nodes.prototype.impl;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.weisj.jsvg.geometry.size.Length;
import com.github.weisj.jsvg.geometry.size.MeasureContext;
import com.github.weisj.jsvg.nodes.ClipPath;
import com.github.weisj.jsvg.nodes.Mask;
import com.github.weisj.jsvg.nodes.filter.Filter;
import com.github.weisj.jsvg.nodes.prototype.HasClip;
import com.github.weisj.jsvg.nodes.prototype.HasFilter;
import com.github.weisj.jsvg.nodes.prototype.Transformable;
import com.github.weisj.jsvg.parser.AttributeNode;

public final class GeometryContextImpl implements Transformable, HasClip, HasFilter {

    private final @Nullable AffineTransform transform;
    private final @NotNull Length transformOriginX;
    private final @NotNull Length transformOriginY;

    private final @Nullable ClipPath clipPath;
    private final @Nullable Mask mask;
    private final @Nullable Filter filter;


    private GeometryContextImpl(@Nullable AffineTransform transform, @NotNull Length transformOriginX,
            @NotNull Length transformOriginY, @Nullable ClipPath clipPath, @Nullable Mask mask,
            @Nullable Filter filter) {
        this.transform = transform;
        this.transformOriginX = transformOriginX;
        this.transformOriginY = transformOriginY;
        this.clipPath = clipPath;
        this.mask = mask;
        this.filter = filter;
    }

    public static @NotNull GeometryContextImpl parse(@NotNull AttributeNode attributeNode) {
        Length[] transformOrigin = attributeNode.getLengthList("transform-origin");
        return new GeometryContextImpl(
                attributeNode.parseTransform("transform"),
                transformOrigin.length > 0 ? transformOrigin[0] : Length.ZERO,
                transformOrigin.length > 1 ? transformOrigin[1] : Length.ZERO,
                attributeNode.getClipPath(),
                attributeNode.getMask(),
                attributeNode.getFilter());
    }

    @Override
    public @Nullable ClipPath clipPath() {
        return clipPath;
    }

    @Override
    public @Nullable Mask mask() {
        return mask;
    }

    @Override
    public @Nullable Filter filter() {
        return filter;
    }

    @Override
    public @Nullable AffineTransform transform() {
        return transform;
    }

    @Override
    public @NotNull Point2D transformOrigin(@NotNull MeasureContext context) {
        return new Point2D.Float(
                transformOriginX.resolveWidth(context),
                transformOriginY.resolveHeight(context));
    }
}
