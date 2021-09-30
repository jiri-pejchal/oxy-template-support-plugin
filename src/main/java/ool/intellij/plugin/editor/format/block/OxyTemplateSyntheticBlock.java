package ool.intellij.plugin.editor.format.block;

import java.util.List;

import ool.intellij.plugin.editor.format.block.innerJs.InnerJsBlockWrapper;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.google.common.collect.ImmutableList;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateSyntheticBlock implements Block
{
    private final List<Block> children;

    private final Indent indent;

    private final Indent childIndent;

    public OxyTemplateSyntheticBlock(@NotNull final List<Block> children, @Nullable final Indent indent,
                                     @Nullable final Indent childIndent)
    {
        assert children.size() > 0;

        this.children = ImmutableList.copyOf(children);
        this.childIndent = childIndent;
        this.indent = indent;
    }

    @NotNull
    @Override
    public TextRange getTextRange()
    {
        return TextRange.create(children.get(0).getTextRange().getStartOffset(),
                children.get(children.size() - 1).getTextRange().getEndOffset());
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks()
    {
        return children;
    }

    @Nullable
    @Override
    public Wrap getWrap()
    {
        return null;
    }

    @Nullable
    @Override
    public Indent getIndent()
    {
        return indent;
    }

    @Nullable
    @Override
    public Alignment getAlignment()
    {
        return null;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2)
    {
        if (child1 instanceof OxyTemplateBlock && child2 instanceof InnerJsBlockWrapper
                || child2 instanceof OxyTemplateBlock && child1 instanceof InnerJsBlockWrapper)
        {
            return Spacing.createSpacing(1, 1, 0, true, 1);
        }
        if (child1 instanceof OxyTemplateBlock && child2 instanceof OxyTemplateBlock)
        {
            OxyTemplateBlock firstBlock = (OxyTemplateBlock) child1;
            OxyTemplateBlock secondBlock = (OxyTemplateBlock) child2;

            if ((firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_NAME || firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_ATTRIBUTE)
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_ATTRIBUTE)
            {
                return Spacing.createSpacing(1, 1, 0, true, 0);
            }
            else if ((firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_NAME
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.T_XML_EMPTY_TAG_END) ||
                    (firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_ATTRIBUTE
                            && secondBlock.getNode().getElementType() == OxyTemplateTypes.T_XML_EMPTY_TAG_END))
            {
                return Spacing.createSpacing(0, 0, 0, false, 0);
            }
            else if ((firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_NAME
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.T_XML_OPEN_TAG_END) ||
                    (firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_ATTRIBUTE
                            && secondBlock.getNode().getElementType() == OxyTemplateTypes.T_XML_OPEN_TAG_END))
            {
                return Spacing.createSpacing(0, 0, 0, false, 0);
            }
            else if ((firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_NAME
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_END))
            {
                return Spacing.createSpacing(0, 0, 0, false, 0);
            }
        }

        return null;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(final int newChildIndex)
    {
        return new ChildAttributes(childIndent, null);
    }

    @Override
    public boolean isIncomplete()
    {
        return false;
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

}
