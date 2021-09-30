package ool.intellij.plugin.editor.highlighter;

import java.util.Map;
import javax.swing.*;

import ool.intellij.plugin.file.type.OxyTemplateFileType;
import ool.intellij.plugin.lang.I18nSupport;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 12/12/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateColorSettingsPage implements ColorSettingsPage
{
    @Nullable
    @Override
    public Icon getIcon()
    {
        return OxyTemplateFileType.INSTANCE.getIcon();
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter()
    {
        return new OxyTemplateSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText()
    {
        return "<%@ layout \"_layout.jst\" %>\n" +
                "\n" +
                "<m:foo.bar asdf=\"expr: __js__\">\n" +
                "    <m:bar.baz param_key=\"va&quot;lue\" />\n" +
                "</m:foo.bar>\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
    {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors()
    {
        return new AttributesDescriptor[]{
                new AttributesDescriptor(I18nSupport.message("color.settings.inner.block.boundary.marker"), OxyTemplateSyntaxHighlighter.BLOCK),
                new AttributesDescriptor(I18nSupport.message("color.settings.directive"), OxyTemplateSyntaxHighlighter.KEY),
                new AttributesDescriptor(I18nSupport.message("color.settings.directive.param"), OxyTemplateSyntaxHighlighter.VALUE),
                new AttributesDescriptor(I18nSupport.message("color.settings.macro.tag.boundary"), OxyTemplateSyntaxHighlighter.MACRO_TAG_BOUNDARY),
                new AttributesDescriptor(I18nSupport.message("color.settings.macro.name.xml.namespace"), OxyTemplateSyntaxHighlighter.MACRO_XML_NAMESPACE),
                new AttributesDescriptor(I18nSupport.message("color.settings.macro.name"), OxyTemplateSyntaxHighlighter.MACRO_NAME),
                new AttributesDescriptor(I18nSupport.message("color.settings.macro.parameter.name"), OxyTemplateSyntaxHighlighter.MACRO_PARAM_NAME),
                new AttributesDescriptor(I18nSupport.message("color.settings.macro.parameter.value"), OxyTemplateSyntaxHighlighter.MACRO_PARAM_VALUE),
                new AttributesDescriptor(I18nSupport.message("color.settings.macro.parameter.encoded.entity"), OxyTemplateSyntaxHighlighter.MACRO_PARAM_VALUE_ENCODED_ENTITY),
                new AttributesDescriptor(I18nSupport.message("color.settings.macro.parameter.expression.statement"), OxyTemplateSyntaxHighlighter.MACRO_PARAM_EXPRESSION_STATEMENT),
        };
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors()
    {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NonNls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return "Oxy Template";
    }

}
