package ool.intellij.plugin.editor.type;

import ool.intellij.plugin.file.OxyTemplateFileViewProvider;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.intellij.plugin.psi.MacroAttribute;
import ool.intellij.plugin.psi.MacroEmptyTag;
import ool.intellij.plugin.psi.OxyTemplateHelper;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class TagCloseHandler extends TypedHandlerDelegate
{
    @NotNull
    @Override
    public Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType)
    {
        FileViewProvider provider = file.getViewProvider();
        int offset;
        PsiElement elementAt;

        if ( ! (provider instanceof OxyTemplateFileViewProvider) || (offset = editor.getCaretModel().getOffset()) < 1)
        {
            return Result.CONTINUE;
        }

        if (c == '>' && isOpenMacroEnd(provider.findElementAt(offset - 1, OxyTemplate.INSTANCE)))
        {
            // <m:foo.bar> -> <m:foo.bar></m:foo.bar>
            String macroTagToBeClosedName;

            editor.getDocument().insertString(offset, ">");
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            elementAt = provider.findElementAt(offset, OxyTemplate.INSTANCE);

            if ((macroTagToBeClosedName = OxyTemplateHelper.getPreviousUnclosedMacroTagName(elementAt)) != null)
            {
                String closeTag = "</m:" + macroTagToBeClosedName + ">";
                editor.getDocument().insertString(offset + 1, closeTag);
            }

            editor.getCaretModel().moveToOffset(offset + 1);

            return Result.STOP;
        }
        else if (c == '/')
        {
            // <m:foo.bar / -> <m:foo.bar />
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

            if (isOpenMacroEnd(provider.findElementAt(offset - 1, OxyTemplate.INSTANCE)))
            {
                editor.getDocument().insertString(offset, "/>");
                editor.getCaretModel().moveToOffset(offset + 2);

                return Result.STOP;
            }
        }

        return Result.CONTINUE;
    }

    private static boolean isOpenMacroEnd(@Nullable final PsiElement elementAt)
    {
        if (elementAt == null)
        {
            return false;
        }

        PsiElement psiElement;

        // <m:foo.bar [param="value"] _
        if (OxyTemplateParserDefinition.WHITE_SPACES.contains(elementAt.getNode().getElementType()))
        {
            psiElement = elementAt.getPrevSibling();

            if (psiElement instanceof MacroEmptyTag
                    && psiElement.getLastChild().getNode().getElementType() != OxyTemplateTypes.T_XML_EMPTY_TAG_END)
            {
                return true;
            }
        }
        // <m:foo.bar[ param]_
        else if (elementAt.getNode().getElementType() == OxyTemplateTypes.T_MACRO_NAME)
        {
            if ((psiElement = PsiTreeUtil.getParentOfType(elementAt, MacroEmptyTag.class)) != null
                    && (psiElement.getLastChild() instanceof PsiErrorElement || psiElement.getLastChild() instanceof MacroAttribute
                        && psiElement.getLastChild().getLastChild() instanceof PsiErrorElement)
                    && elementAt.getParent().isEquivalentTo((psiElement = psiElement.getLastChild().getPrevSibling())
                        instanceof PsiWhiteSpace ? psiElement.getPrevSibling() : psiElement))
            {
                return true;
            }
        }
        // <m:foo.bar param="value"_
        else if ((psiElement = PsiTreeUtil.getParentOfType(elementAt, MacroAttribute.class)) != null)
        {
            MacroEmptyTag tag = PsiTreeUtil.getParentOfType(psiElement, MacroEmptyTag.class);

            if (tag != null && psiElement.getLastChild().isEquivalentTo(elementAt)
                    && tag.getLastChild() instanceof PsiErrorElement
                    && tag.getLastChild().getPrevSibling().isEquivalentTo(psiElement))
            {
                return true;
            }
        }

        return false;
    }

}
