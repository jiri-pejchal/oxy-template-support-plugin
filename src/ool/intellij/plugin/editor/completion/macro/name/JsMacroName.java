package ool.intellij.plugin.editor.completion.macro.name;

import java.util.regex.Pattern;

import ool.intellij.plugin.editor.completion.handler.IncludeAutoInsert;
import ool.intellij.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.intellij.plugin.file.index.nacro.MacroIndex;
import ool.intellij.plugin.file.type.OxyTemplateFileType;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 1/14/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroName extends AbstractMacroName
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("\\);[A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*\\(");

    private String namespaceFqn;

    @Override
    protected boolean accept(@NotNull PsiElement position)
    {
        PsiElement topReference;

        if (position.getNode().getElementType() == JSTokenTypes.IDENTIFIER
                && (topReference = PsiTreeUtil.getTopmostParentOfType(position, JSReferenceExpression.class)) != null
                && MacroIndex.isInMacroNamespace(topReference.getText()))
        {
            namespaceFqn = MacroIndex.normalizeMacroName(topReference.getText().substring(0, position.getStartOffsetInParent()));

            return true;
        }

        return false;
    }

    @Override
    public boolean addMacroNameCompletionVariant(@NotNull CompletionResultSet result, @NotNull PsiElement position, @NotNull PsiElement lookupElement,
                                                 @NotNull String fqn, @NotNull String macroName, @NotNull String macroNamespace)
    {
        result.withPrefixMatcher(new CamelHumpMatcher(namespaceFqn + position.getText().replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "")))
            .consume(LookupElementDecorator.withInsertHandler(LookupElementBuilder
                .create(lookupElement, fqn + "();")
                .withIcon(OxyTemplateFileType.INSTANCE.getIcon())
                .withPresentableText(macroName + "([Object] params) (" + macroNamespace + ")")
                .withTailText(" " + lookupElement.getContainingFile().getVirtualFile().getPath().replaceFirst("^.+((src)|(WEB_INF))/", ""), true)
                .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME)
                {
                    @Override
                    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item)
                    {
                        CaretModel caretModel = context.getEditor().getCaretModel();
                        caretModel.moveToOffset(caretModel.getOffset() - 2);

                        super.handleInsert(context, item);
                    }
                })
                .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE),
            new IncludeAutoInsert()));

        return fqn.startsWith(namespaceFqn.substring(0, Math.max(namespaceFqn.length(), namespaceFqn.indexOf("."))));
    }

}
