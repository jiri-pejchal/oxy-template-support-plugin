package ool.intellij.plugin.editor.inspection.filter;

import java.util.Arrays;
import java.util.List;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.lang.javascript.highlighting.IntentionAndInspectionFilter;
import com.intellij.lang.javascript.inspections.JSCommentMatchesSignatureInspection;
import com.intellij.lang.javascript.inspections.JSDuplicatedDeclarationInspection;
import com.intellij.lang.javascript.inspection.JSUnusedAssignmentInspection;
import com.intellij.lang.javascript.inspections.JSValidateJSDocInspection;
import com.intellij.lang.javascript.inspections.UnterminatedStatementJSInspection;
import com.sixrr.inspectjs.validity.BadExpressionStatementJSInspection;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsIntentionAndInspectionFilter extends IntentionAndInspectionFilter
{
    private static final List<String> unsupportedInspectionIDs = Arrays.asList(
            InspectionProfileEntry.getShortName(BadExpressionStatementJSInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(UnterminatedStatementJSInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(JSUnusedAssignmentInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(JSDuplicatedDeclarationInspection.class.getSimpleName()),
            // JSDoc has custom handlers
            InspectionProfileEntry.getShortName(JSValidateJSDocInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(JSCommentMatchesSignatureInspection.class.getSimpleName())
    );

    @Override
    public boolean isSupportedInspection(String inspectionToolId)
    {
        return ! unsupportedInspectionIDs.contains(inspectionToolId);
    }

}
