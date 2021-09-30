package ool.intellij.plugin.editor.format.block.builder;

import java.util.List;

import ool.intellij.plugin.editor.format.block.innerJs.InnerJsBlock;
import ool.intellij.plugin.editor.format.block.innerJs.InnerJsBlockWrapper;
import ool.intellij.plugin.editor.format.block.xml.OxyTemplateXmlBlock;
import ool.intellij.plugin.editor.format.block.xml.OxyTemplateXmlTagBlock;
import ool.intellij.plugin.lang.OxyTemplateInnerJs;
import ool.intellij.plugin.psi.OxyTemplateHelper;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingContext;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageFormatting;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.xml.AnotherLanguageBlockWrapper;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.impl.source.xml.XmlDocumentImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInjectedBlockBuilder
{
    protected final XmlFormattingPolicy policy;

    protected final AbstractXmlTemplateFormattingModelBuilder builder;

    public OxyTemplateInjectedBlockBuilder(@NotNull final AbstractXmlTemplateFormattingModelBuilder builder,
                                           @NotNull final XmlFormattingPolicy policy)
    {
        this.builder = builder;
        this.policy = policy;
    }

    @NotNull
    protected IElementType getOuterLanguageElement()
    {
        return OxyTemplateTypes.T_OUTER_TEMPLATE_ELEMENT;
    }

    @NotNull
    public Block createInjectedBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment,
                                     @Nullable Indent indent, @NotNull TextRange range)
    {
        final Block resultBlock;

        if (node.getPsi() instanceof XmlTag)
        {
            resultBlock = new OxyTemplateXmlTagBlock(builder, node, wrap, alignment, policy, indent);
        }
        else
        {
            resultBlock = new OxyTemplateXmlBlock(builder, node, wrap, alignment, policy, indent, range);
        }

        return new AnotherLanguageBlockWrapper(node, policy, resultBlock, indent, node.getStartOffset(), range);
    }

    @Nullable
    public ASTNode buildInjectedBlocks(@NotNull List<Block> result, @NotNull ASTNode injectedNode, @Nullable Indent indent,
                                       final @NotNull TextRange allowedRange)
    {
        FileViewProvider viewProvider = injectedNode.getPsi().getContainingFile().getViewProvider();
        TextRange allowedRangeLocal = allowedRange;

        PsiElement elementAt = viewProvider.findElementAt(injectedNode.getStartOffset(), HTMLLanguage.INSTANCE);

        while (elementAt != null)
        {
            if (elementAt.getNode().getElementType() == getOuterLanguageElement())
            {
                break;
            }
            else if ( ! allowedRangeLocal.contains(elementAt.getTextRange()))
            {
                /**
                 * It is important to cover all content within allowedRange. When the current element doesn't fit in
                 * (e.g. unclosed tags etc.), let's just try it's first child.
                 */
                while (elementAt.getFirstChild() != null
                        && ! allowedRangeLocal.contains((elementAt = elementAt.getFirstChild()).getTextRange())) ;
            }

            /**
             * shift to topmost element, that covers the range, e.g.:
             * ...html...<m:foo... - T_XML_TAG_START -> MACRO_[EMPTY_]TAG
             */
            while (OxyTemplateHelper.checkRangeContainsParent(elementAt, allowedRangeLocal, getOuterLanguageElement()))
            {
                elementAt = elementAt.getParent();
            }

            if ( ! FormatterUtil.containsWhiteSpacesOnly(elementAt.getNode()))
            {
                TextRange intersection = elementAt.getNode().getTextRange().intersection(allowedRangeLocal);

                assert intersection != null;

                result.add(createInjectedBlock(elementAt.getNode(), null, null, indent, intersection));
            }

            while (elementAt.getNextSibling() == null && ! (elementAt.getParent() instanceof XmlDocumentImpl))
            {
                elementAt = elementAt.getParent();
            }

            elementAt = elementAt.getNextSibling();

            if (elementAt != null)
            {
                if (elementAt instanceof XmlText && OxyTemplateHelper.containsElement(elementAt, allowedRangeLocal,
                        getOuterLanguageElement()))
                {
                    elementAt = elementAt.getFirstChild();
                }

                if (elementAt.getNode().getStartOffset() >= allowedRange.getEndOffset())
                {
                    break;
                }

                allowedRangeLocal = TextRange.create(elementAt.getNode().getStartOffset(), allowedRange.getEndOffset());
            }
        }

        return shiftOriginalNode(injectedNode, result);
    }

    @Nullable
    public ASTNode buildInjectedJsBlocks(@NotNull List<Block> result, @NotNull ASTNode injectedNode, @Nullable Indent indent,
                                         @NotNull TextRange allowedRange)
    {
        PsiElement childPsi = injectedNode.getPsi();
        FormattingModelBuilder modelBuilder = LanguageFormatting.INSTANCE.forContext(OxyTemplateInnerJs.INSTANCE, childPsi);
        PsiFile jsPsiFile = childPsi.getContainingFile().getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE);

        assert modelBuilder != null && jsPsiFile != null;

        PsiElement element = jsPsiFile.findElementAt(injectedNode.getStartOffset());

        assert element != null;

        while (OxyTemplateHelper.checkRangeContainsParent(element, allowedRange, OxyTemplateTypes.T_INNER_TEMPLATE_ELEMENT))
        {
            element = element.getParent();
        }

        /**
         * All we are interested in is the range passed to InnerJsBlockWrapper. It must fully cover injectedNode range
         * and must be within limits of allowedRange.
         */
        while (true)
        {
            PsiElement nextSibling = element.getNextSibling();

            while (element.getNextSibling() == null && element.getNode().getTextRange().getEndOffset() != allowedRange.getEndOffset())
            {
                element = element.getParent();
                nextSibling = element.getNextSibling();
            }
            if (nextSibling == null || nextSibling.getNode().getElementType() == OxyTemplateTypes.T_INNER_TEMPLATE_ELEMENT)
            {
                break;
            }
            if ( ! allowedRange.contains(nextSibling.getTextRange()))
            {
                while (nextSibling.getFirstChild() != null
                        && ! allowedRange.contains((nextSibling = nextSibling.getFirstChild()).getTextRange())) ;
            }

            element = nextSibling;
        }

        TextRange jsBlockRange = TextRange.create(injectedNode.getStartOffset(),
                element.getNode().getStartOffset() + element.getTextLength());

        FormattingModel childModel = modelBuilder.createModel(FormattingContext.create(jsPsiFile, policy.getSettings()));

        result.add(new InnerJsBlockWrapper((InnerJsBlock) childModel.getRootBlock(), injectedNode.getStartOffset(),
                allowedRange.intersection(jsBlockRange), indent));

        return shiftOriginalNode(injectedNode, result);
    }

    // -----------------------------------------------------------------------------------------------------------

    @Nullable
    protected ASTNode shiftOriginalNode(@NotNull ASTNode originalNode, @NotNull List<Block> result)
    {
        ASTNode current = originalNode;
        Block last = result.get(result.size() - 1);

        while (current != null)
        {
            if (current.getStartOffset() + current.getTextLength() >= last.getTextRange().getEndOffset())
            {
                if (current.getStartOffset() < last.getTextRange().getEndOffset() && current.getFirstChildNode() != null)
                {
                    current = current.getFirstChildNode();
                }
                else
                {
                    break;
                }
            }

            current = current.getTreeNext();
        }

        assert current == null || current.getElementType() == OxyTemplateTypes.T_TEMPLATE_HTML_CODE
                || current.getElementType() == OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE;

        return current;
    }

}
