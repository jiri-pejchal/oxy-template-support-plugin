package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class ExpressionStatement extends CompletionContributor
{
    public ExpressionStatement()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(OxyTemplateTypes.T_MACRO_PARAM).withLanguage(OxyTemplate.INSTANCE),
                new CompletionProvider<CompletionParameters>()
                {

                    @Override
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet)
                    {

                        resultSet.addElement(LookupElementBuilder.create("expr: "));
                    }
                }
        );
    }

}
