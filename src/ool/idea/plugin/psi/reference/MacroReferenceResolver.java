package ool.idea.plugin.psi.reference;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import java.util.ArrayList;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.file.index.nacros.MacroIndex;
import org.jetbrains.annotations.NotNull;

/**
 * 2/6/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class MacroReferenceResolver implements ResolveCache.PolyVariantContextResolver<PsiPolyVariantReference>
{
    public static final MacroReferenceResolver DEFAULT = new MacroReferenceResolver();

    private final boolean resolveJavaMacros;

    private final boolean resolveJsMacros;

    public MacroReferenceResolver()
    {
        resolveJavaMacros = true;
        resolveJsMacros = true;
    }

    public MacroReferenceResolver(boolean resolveJsMacros)
    {
        this.resolveJsMacros = resolveJsMacros;
        this.resolveJavaMacros = true;
    }

    public MacroReferenceResolver(boolean resolveJsMacros, boolean resolveJavaMacros)
    {
        this.resolveJsMacros = resolveJsMacros;
        this.resolveJavaMacros = resolveJavaMacros;
    }

    @NotNull
    @Override
    public ResolveResult[] resolve(@NotNull PsiPolyVariantReference ref, @NotNull PsiFile containingFile, boolean incompleteCode)
    {
        String elemntText = ref.getElement().getText();
        String macroName = elemntText.substring(elemntText.indexOf('\"') + 1, ref.getRangeInElement().getEndOffset());

        Project project = ref.getElement().getProject();
        String normalizedMacroName = MacroIndex.normalizeMacroName(macroName);

        List<PsiElement> references = new ArrayList<PsiElement>(5);

        if(resolveJsMacros)
        {
            addJsMacroReferences(references, normalizedMacroName, ref.getElement(), project);
        }
        if(resolveJavaMacros)
        {
            addJavaMacroRefrences(references, normalizedMacroName, project);
        }

        ResolveResult[] resolveResults = new ResolveResult[references.size()];

        for(int i = 0; i < references.size(); i++)
        {
            resolveResults[i] = new PsiElementResolveResult(references.get(i));
        }

        return resolveResults;
    }

    protected void addJsMacroReferences(@NotNull List<PsiElement> result, @NotNull String macroName, @NotNull PsiElement referencingElement, @NotNull Project project)
    {
        addJsMacroReferences(result, macroName, project);
    }

    protected void addJsMacroReferences(@NotNull List<PsiElement> result, @NotNull String macroName, @NotNull Project project)
    {
        result.addAll(OxyTemplateIndexUtil.getJsMacroNameReferences(macroName, project));
    }

    protected void addJavaMacroRefrences(@NotNull List<PsiElement> result, @NotNull String macroName, @NotNull Project project)
    {
        PsiClass psiClass;

        if(result.size() == 0 && (psiClass = OxyTemplateIndexUtil.getJavaMacroNameReference(macroName, project)) != null)
        {
            result.add(psiClass);
        }
    }

}
