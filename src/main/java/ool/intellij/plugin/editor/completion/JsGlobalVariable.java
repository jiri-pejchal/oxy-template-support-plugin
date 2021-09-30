package ool.intellij.plugin.editor.completion;

import java.util.regex.Pattern;

import ool.intellij.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.intellij.plugin.file.type.OxyTemplateFileType;
import ool.intellij.plugin.psi.reference.innerjs.InnerJsReferenceExpressionResolver;
import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;
import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableIndex;
import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableTypeProvider;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 1/14/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsGlobalVariable extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("[A-Za-z0-9_]*");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        PsiElement psiElement = parameters.getPosition();

        if (psiElement.getNode().getElementType() != JSTokenTypes.IDENTIFIER
                || ! InnerJsReferenceExpressionResolver.isGlobalVariableSuspect(psiElement))
        {
            return;
        }

        for (GlobalVariableDefinition variable : GlobalVariableIndex.getGlobals(parameters.getOriginalFile().getProject()).values())
        {
            String typeText = (variable.getType() == null || variable.getName().equals(GlobalVariableTypeProvider.CONTROLLERS_GLOBAL_VARIABLE_NAME))
                    ? "" : variable.getType().getTypeText().replaceFirst("^.+\\.", "");

            result.consume(LookupElementBuilder.create(variable.getName())
                    .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME))
                    .withTailText(" (" + variable.getContainingFile().getName() + ")", true)
                    .withTypeText(typeText, true)
                    .withIcon(OxyTemplateFileType.INSTANCE.getIcon())
                    .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
        }
    }

}
