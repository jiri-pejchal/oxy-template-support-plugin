package ool.idea.plugin.editor.completion.lookupElement;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import ool.idea.plugin.file.OxyTemplateFileType;
import org.jetbrains.annotations.NotNull;

/**
 * 1/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroNameLookupElementProvider implements BaseLookupElementProvider
{
    public static final JsMacroNameLookupElementProvider INSTANCE = new JsMacroNameLookupElementProvider();

    @NotNull
    @Override
    public LookupElement create(String lookupText, @NotNull Object lookupObject)
    {
        return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(lookupObject, lookupText + "()")
            .withIcon(OxyTemplateFileType.INSTANCE.getIcon())
            .withPresentableText(lookupText)
            .withInsertHandler(new InsertHandler<LookupElement>()
                {
                    @Override
                    public void handleInsert(InsertionContext context, LookupElement item)
                    {
                        CaretModel caretModel = context.getEditor().getCaretModel();
                        caretModel.moveToOffset(caretModel.getOffset() - 1);
                    }
                }
            ).withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE), Integer.MAX_VALUE);
    }

}
