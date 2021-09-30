package ool.intellij.plugin.psi.impl;

import ool.intellij.plugin.psi.DirectiveParamFileReference;
import ool.intellij.plugin.psi.DirectiveStatement;
import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.MacroEmptyTag;
import ool.intellij.plugin.psi.MacroName;
import ool.intellij.plugin.psi.MacroTag;
import ool.intellij.plugin.psi.OxyTemplateElementFactory;
import ool.intellij.plugin.psi.OxyTemplateTypes;
import ool.intellij.plugin.psi.reference.MacroReferenceSet;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.tree.DefaultRoleFinder;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplatePsiUtil
{
    public static DefaultRoleFinder macroOpenTagEndFinder = new DefaultRoleFinder(OxyTemplateTypes.T_XML_OPEN_TAG_END);
    public static DefaultRoleFinder macroCloseTagStartFinder = new DefaultRoleFinder(OxyTemplateTypes.T_XML_CLOSE_TAG_START);

    @NotNull
    public static PsiReference[] getReferences(@NotNull DirectiveParamFileReference directiveParamFileReference)
    {
        return new FileReferenceSet(directiveParamFileReference).getAllReferences();
    }

    @NotNull
    public static PsiReference[] getReferences(@NotNull MacroName macroName)
    {
        return new MacroReferenceSet(macroName).getAllReferences();
    }

    @Nullable
    public static PsiReference getReference(@NotNull MacroName macroName)
    {
        PsiReference[] references = macroName.getReferences();

        return references.length > 0 ? references[0] : null;
    }

    public static PsiElement setName(@NotNull MacroName macroName, @NotNull String newName)
    {
        return macroName.replace(OxyTemplateElementFactory.createMacroName(macroName.getProject(), newName));
    }

    @NotNull
    public static String getName(@NotNull MacroName macroName)
    {
        return macroName.getText();
    }

    public static boolean isClosingTagMacroName(@NotNull final MacroName macroName)
    {
        if (PsiTreeUtil.getParentOfType(macroName, MacroEmptyTag.class) != null)
        {
            return false;
        }

        MacroCall tag = PsiTreeUtil.getParentOfType(macroName, MacroCall.class);
        MacroName openingTagMacroName;

        return tag != null && (openingTagMacroName = tag.getMacroName()) != null &&
                ! openingTagMacroName.isEquivalentTo(macroName);
    }

    public static String getName(@NotNull DirectiveStatement directiveStatement)
    {
        PsiElement psiElement = directiveStatement.getDirectiveOpenStatement().getNextSibling();

        if (psiElement instanceof PsiWhiteSpace)
        {
            psiElement = psiElement.getNextSibling();
        }

        return psiElement.getText();
    }

    @NotNull
    public static TextRange getContentRange(@NotNull MacroTag tag)
    {
        ASTNode macroOpenTagEnd = macroOpenTagEndFinder.findChild(tag.getNode());
        ASTNode macroCloseTagStart = macroCloseTagStartFinder.findChild(tag.getNode());

        assert macroOpenTagEnd != null;

        if (macroCloseTagStart != null)
        {
            return TextRange.create(macroOpenTagEnd.getStartOffset() + macroOpenTagEnd.getTextLength(),
                    macroCloseTagStart.getStartOffset());
        }

        return TextRange.create(macroOpenTagEnd.getStartOffset() + macroOpenTagEnd.getTextLength(),
                tag.getNode().getStartOffset() + tag.getTextLength());
    }

}
