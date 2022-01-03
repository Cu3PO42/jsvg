/*
 * MIT License
 *
 * Copyright (c) 2021-2022 Jannis Weis
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
package com.github.weisj.jsvg.nodes.text;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.weisj.jsvg.attributes.font.AttributeFontSpec;
import com.github.weisj.jsvg.attributes.font.FontParser;
import com.github.weisj.jsvg.attributes.font.SVGFont;
import com.github.weisj.jsvg.attributes.text.LengthAdjust;
import com.github.weisj.jsvg.geometry.size.Length;
import com.github.weisj.jsvg.nodes.SVGNode;
import com.github.weisj.jsvg.nodes.container.BaseRenderableContainerNode;
import com.github.weisj.jsvg.nodes.prototype.HasShape;
import com.github.weisj.jsvg.nodes.prototype.Renderable;
import com.github.weisj.jsvg.nodes.prototype.spec.NotImplemented;
import com.github.weisj.jsvg.parser.AttributeNode;
import com.github.weisj.jsvg.renderer.NodeRenderer;
import com.github.weisj.jsvg.renderer.RenderContext;

abstract class TextContainer extends BaseRenderableContainerNode<TextSegment>
        implements TextSegment.RenderableSegment, HasShape {
    private final List<@NotNull TextSegment> segments = new ArrayList<>();

    protected AttributeFontSpec fontSpec;
    protected LengthAdjust lengthAdjust;
    protected @NotImplemented Length textLength;

    @Override
    @MustBeInvokedByOverriders
    public void build(@NotNull AttributeNode attributeNode) {
        super.build(attributeNode);
        fontSpec = FontParser.parseFontSpec(attributeNode);
        lengthAdjust = attributeNode.getEnum("lengthAdjust", LengthAdjust.Spacing);
        textLength = attributeNode.getLength("textLength", Length.UNSPECIFIED);
    }

    @Override
    protected boolean acceptChild(@Nullable String id, @NotNull SVGNode node) {
        return node instanceof TextSegment;
    }

    @Override
    protected void doAdd(@NotNull SVGNode node) {
        segments.add((TextSegment) node);
    }

    @Override
    public final void addContent(char[] content) {
        if (content.length == 0) return;
        segments.add(new StringTextSegment(content));
    }

    @Override
    public List<? extends @NotNull TextSegment> children() {
        return segments;
    }

    protected abstract GlyphCursor createLocalCursor(@NotNull RenderContext context, @NotNull GlyphCursor current);

    protected abstract void cleanUpLocalCursor(@NotNull GlyphCursor current, @NotNull GlyphCursor local);

    protected final void renderSegment(@NotNull GlyphCursor cursor, @NotNull RenderContext context,
            @NotNull Graphics2D g) {
        prepareSegmentForRendering(cursor, context);

        double offset;
        switch (context.fontRenderContext().textAnchor()) {
            default:
            case Start:
                offset = 0;
                break;
            case Middle:
                offset = cursor.completeGlyphRunBounds.getWidth() / 2f;
                break;
            case End:
                offset = cursor.completeGlyphRunBounds.getWidth();
                break;
        }
        g.translate(-offset, 0);

        renderSegmentWithoutLayout(cursor, context, g);
    }

    @Override
    public void renderSegmentWithoutLayout(@NotNull GlyphCursor cursor, @NotNull RenderContext context,
            @NotNull Graphics2D g) {
        for (TextSegment segment : children()) {
            RenderContext currentContext = context;
            if (segment instanceof Renderable) {
                if (!((Renderable) segment).isVisible(context)) continue;
                currentContext = NodeRenderer.setupRenderContext(segment, context);
            }
            if (segment instanceof StringTextSegment) {
                GlyphRenderer.renderGlyphRun((StringTextSegment) segment, cursor.completeGlyphRunBounds, g);
            } else if (segment instanceof RenderableSegment) {
                ((RenderableSegment) segment).renderSegmentWithoutLayout(cursor, currentContext, g);
            } else {
                throw new IllegalStateException("Can't render segment " + segment);
            }
        }
    }

    @Override
    public void prepareSegmentForRendering(@NotNull GlyphCursor cursor, @NotNull RenderContext context) {
        // Todo: textLength should be taken into consideration.
        SVGFont font = context.font();

        GlyphCursor localCursor = createLocalCursor(context, cursor);

        for (TextSegment segment : children()) {
            RenderContext currentContext = context;
            if (segment instanceof Renderable) {
                if (!((Renderable) segment).isVisible(context)) continue;
                currentContext = NodeRenderer.setupRenderContext(segment, context);
            }
            if (segment instanceof StringTextSegment) {
                GlyphRenderer.prepareGlyphRun((StringTextSegment) segment, localCursor, font, currentContext);
            } else if (segment instanceof RenderableSegment) {
                ((RenderableSegment) segment).prepareSegmentForRendering(localCursor, currentContext);
            } else {
                throw new IllegalStateException("Can't render segment " + segment);
            }
        }

        cleanUpLocalCursor(cursor, localCursor);
    }

    @Override
    public void appendTextShape(@NotNull GlyphCursor cursor, @NotNull Path2D textShape,
            @NotNull RenderContext context) {
        SVGFont font = context.font();
        GlyphCursor localCursor = createLocalCursor(context, cursor);

        for (TextSegment segment : children()) {
            RenderContext currentContext = context;
            if (segment instanceof Renderable) {
                if (!((Renderable) segment).isVisible(context)) continue;
                currentContext = NodeRenderer.setupRenderContext(segment, context);
            }
            if (segment instanceof StringTextSegment) {
                Shape glyphRun = GlyphRenderer.layoutGlyphRun((StringTextSegment) segment, localCursor, font,
                        currentContext.measureContext(), currentContext.fontRenderContext());
                textShape.append(glyphRun, false);
            } else if (segment instanceof RenderableSegment) {
                ((RenderableSegment) segment).appendTextShape(localCursor, textShape, currentContext);
            } else {
                throw new IllegalStateException("Can't compute shape of segment " + segment);
            }
        }

        cleanUpLocalCursor(cursor, localCursor);
    }

    @Override
    public @NotNull Rectangle2D untransformedElementBounds(@NotNull RenderContext context) {
        return untransformedElementShape(context).getBounds2D();
    }
}
