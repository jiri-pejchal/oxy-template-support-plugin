package ool.intellij.plugin.editor.highlighter;

import ool.intellij.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.codeInsight.highlighting.BraceMatcher;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MatchingElementHighlighter implements BraceMatcher
{
    private static final int OXY_TEMPLATE_TOKEN_GROUP = 2;

    @Override
    public boolean isLBraceToken(HighlighterIterator iterator, @NotNull CharSequence fileText, @NotNull FileType fileType)
    {
        IElementType tokenType = iterator.getTokenType();

        if (tokenType == OxyTemplateTypes.T_XML_TAG_START
                || OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(tokenType))
        {
            return true;
        }
        else if (tokenType == OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY)
        {
            iterator.advance();

            if (iterator.atEnd())
            {
                iterator.retreat();
                return false;
            }

            tokenType = iterator.getTokenType();
            iterator.retreat();

            return tokenType == OxyTemplateTypes.T_DIRECTIVE_PARAM;
        }
        else if (tokenType == OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY)
        {
            iterator.retreat();
            tokenType = iterator.getTokenType();

            if (OxyTemplateParserDefinition.WHITE_SPACES.contains(tokenType))
            {
                iterator.retreat();
                tokenType = iterator.getTokenType();
                iterator.advance();
            }

            iterator.advance();

            return tokenType == OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT;
        }

        return false;
    }

    @Override
    public boolean isRBraceToken(HighlighterIterator iterator, @NotNull CharSequence fileText, @NotNull FileType fileType)
    {
        IElementType tokenType = iterator.getTokenType();

        if (tokenType == OxyTemplateTypes.T_CLOSE_BLOCK_MARKER || tokenType == OxyTemplateTypes.T_XML_EMPTY_TAG_END)
        {
            return true;
        }
        else if (tokenType == OxyTemplateTypes.T_XML_CLOSE_TAG_END)
        {
            return findEndTagStart(iterator);
        }
        else if (tokenType == OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY)
        {
            iterator.retreat();
            tokenType = iterator.getTokenType();
            iterator.advance();

            return tokenType == OxyTemplateTypes.T_DIRECTIVE_PARAM;
        }
        else if (tokenType == OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY)
        {
            iterator.retreat();
            tokenType = iterator.getTokenType();

            if (OxyTemplateParserDefinition.WHITE_SPACES.contains(tokenType))
            {
                iterator.retreat();
                tokenType = iterator.getTokenType();
                iterator.advance();
            }

            iterator.advance();

            return tokenType != OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT;
        }

        return false;
    }

    @Override
    public boolean isPairBraces(@NotNull IElementType tokenType, @NotNull IElementType tokenType2)
    {
        return tokenType == OxyTemplateTypes.T_XML_TAG_START && tokenType2 == OxyTemplateTypes.T_XML_CLOSE_TAG_END
                || tokenType == OxyTemplateTypes.T_XML_CLOSE_TAG_END && tokenType2 == OxyTemplateTypes.T_XML_TAG_START
                || tokenType == OxyTemplateTypes.T_XML_TAG_START && tokenType2 == OxyTemplateTypes.T_XML_EMPTY_TAG_END
                || tokenType == OxyTemplateTypes.T_XML_EMPTY_TAG_END && tokenType2 == OxyTemplateTypes.T_XML_TAG_START
                || OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(tokenType) && OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(tokenType2)
                || OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(tokenType) && OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(tokenType2)
                || tokenType2 == OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY && tokenType == OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY
                || tokenType2 == OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY && tokenType == OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY;
    }

    @Override
    public boolean isStructuralBrace(@NotNull HighlighterIterator iterator, @NotNull CharSequence text, @NotNull FileType fileType)
    {
        return false;
    }

    private static boolean findEndTagStart(HighlighterIterator iterator)
    {
        IElementType tokenType = iterator.getTokenType();
        int balance = 0, count = 0;

        while (balance >= 0 && !iterator.atEnd())
        {
            iterator.retreat();
            count++;

            tokenType = iterator.getTokenType();

            if (tokenType == OxyTemplateTypes.T_XML_CLOSE_TAG_END || tokenType == OxyTemplateTypes.T_XML_OPEN_TAG_END)
            {
                balance++;
            }
            else if (tokenType == OxyTemplateTypes.T_XML_TAG_START || tokenType == OxyTemplateTypes.T_XML_CLOSE_TAG_START)
            {
                balance--;
            }
        }

        while (count-- > 0) iterator.advance();

        return tokenType == OxyTemplateTypes.T_XML_CLOSE_TAG_START;
    }

    @Nullable
    @Override
    public IElementType getOppositeBraceTokenType(@NotNull IElementType type)
    {
        return null;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType)
    {
        return true;
    }

    @Override
    public int getCodeConstructStart(@NotNull PsiFile file, int openingBraceOffset)
    {
        return openingBraceOffset;
    }

    @Override
    public int getBraceTokenGroupId(@NotNull IElementType tokenType)
    {
        return OXY_TEMPLATE_TOKEN_GROUP;
    }

}
