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
package com.github.weisj.jsvg.attributes.font;

import java.awt.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.weisj.jsvg.AttributeNode;
import com.github.weisj.jsvg.attributes.AttributeParser;
import com.github.weisj.jsvg.attributes.Percentage;
import com.github.weisj.jsvg.geometry.size.Length;

public final class FontParser {
    private FontParser() {}

    // Todo: font-variant
    public static @NotNull AttributeFontSpec parseFontSpec(@NotNull AttributeNode node) {
        String[] fontFamilies = node.getStringList("font-family");

        // Todo: https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight#fallback_weights
        @Nullable FontWeight weight = parseWeight(node);
        @Nullable FontSize size = parseFontSize(node);
        @Nullable Length sizeAdjust = node.getLength("font-size-adjust");
        @Nullable FontStyle style = parseFontStyle(node);
        @Percentage float stretch = parseStretch(node);

        return new AttributeFontSpec(fontFamilies, style, sizeAdjust, stretch, size, weight);
    }

    private static @Nullable FontWeight parseWeight(@NotNull AttributeNode node) {
        FontWeight weight = node.getEnum("font-weight", PredefinedFontWeight.Number);
        if (weight == PredefinedFontWeight.Number) {
            if (node.hasAttribute("font-weight")) {
                weight = new NumberFontWeight(
                        Math.max(1, Math.min(1000, node.getFloat("font-weight",
                                PredefinedFontWeight.Normal.weight(0)))));
            } else {
                weight = null;
            }
        }
        return weight;
    }

    private static @Percentage float parseStretch(@NotNull AttributeNode node) {
        FontStretch stretch = node.getEnum("font-stretch", FontStretch.Percentage);
        return stretch == FontStretch.Percentage
                ? AttributeParser.parsePercentage(node.getValue("font-stretch"),
                        Length.UNSPECIFIED_RAW, 0.5f, 2f)
                : stretch.percentage();
    }

    private static @Nullable FontSize parseFontSize(@NotNull AttributeNode node) {
        FontSize fontSize = node.getEnum("font-size", PredefinedFontSize.Number);
        if (fontSize == PredefinedFontSize.Number) {
            Length size = node.getLength("font-size", Length.UNSPECIFIED);
            fontSize = !size.isUnspecified()
                    ? new LengthFontSize(size)
                    : null;
        }
        return fontSize;
    }

    private static @Nullable FontStyle parseFontStyle(@NotNull AttributeNode node) {
        FontStyle style = null;
        String styleStr = node.getValue("font-style");
        if ("normal".equalsIgnoreCase(styleStr)) {
            style = FontStyle.Normal;
        } else if ("italic".equalsIgnoreCase(styleStr)) {
            style = FontStyle.Italic;
        } else if (styleStr != null && styleStr.startsWith("oblique")) {
            String[] comps = styleStr.split(" ", 2);
            if (comps.length == 2) {
                style = new FontStyle.ObliqueStyle(
                        AttributeParser.parseFloat(comps[1], FontStyle.ObliqueStyle.DEFAULT_ANGLE));
            } else {
                style = FontStyle.Oblique;
            }
        }
        return style;
    }
}