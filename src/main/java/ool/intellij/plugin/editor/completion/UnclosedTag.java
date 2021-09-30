package ool.intellij.plugin.editor.completion;

import java.util.regex.Pattern;

import ool.intellij.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.intellij.plugin.file.OxyTemplateFileViewProvider;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.psi.OxyTemplateHelper;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * 1/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnclosedTag extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("(\\w+:)?[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*>");

    public UnclosedTag()
    {
        extend(CompletionType.BASIC, psiElement(XmlTokenType.XML_NAME).afterSibling(psiElement(XmlTokenType.XML_END_TAG_START)),
            new CompletionProvider<CompletionParameters>()
            {
                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           @NotNull ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    FileViewProvider provider = parameters.getOriginalFile().getViewProvider();

                    if ( ! (provider instanceof OxyTemplateFileViewProvider))
                    {
                        return;
                    }

                    int offset = parameters.getOffset();

                    PsiElement elementAt = provider.findElementAt(offset - 1, OxyTemplate.INSTANCE);

                    String macroTagToBeClosedName = OxyTemplateHelper.getPreviousUnclosedMacroTagName(elementAt);

                    if (macroTagToBeClosedName != null)
                    {
                        resultSet.consume(LookupElementBuilder.create("m:" + macroTagToBeClosedName + ">")
                                .withPresentableText("m:" + macroTagToBeClosedName)
                                .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME))
                                .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                    }
                }
            }
        );
    }

}
