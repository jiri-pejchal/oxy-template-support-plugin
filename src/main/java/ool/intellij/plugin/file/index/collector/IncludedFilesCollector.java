package ool.intellij.plugin.file.index.collector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ool.intellij.plugin.psi.DirectiveParamFileReference;
import ool.intellij.plugin.psi.visitor.DirectiveStatementVisitor;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import org.jetbrains.annotations.NotNull;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class IncludedFilesCollector extends DirectiveStatementVisitor
{
    private final Map<DirectiveParamFileReference, VirtualFile> includedFiles;
    private final List<VirtualFile> recursionGuard;

    public IncludedFilesCollector()
    {
        includedFiles = new HashMap<>();
        recursionGuard = new LinkedList<>();
    }

    @Override
    public void visitDirectiveParamFileReference(@NotNull DirectiveParamFileReference fileReference)
    {
        PsiFileSystemItem item;

        for (PsiReference reference : fileReference.getReferences())
        {
            if (reference instanceof FileReference && (item = ((FileReference) reference).resolve()) != null
                    && ! (item instanceof PsiDirectory) && item.getContainingFile() != null)
            {
                VirtualFile file = item.getContainingFile().getVirtualFile();

                if ( ! includedFiles.containsValue(file) && ! recursionGuard.contains(file))
                {
                    recursionGuard.add(file);

                    item.getContainingFile().acceptChildren(this);
                }

                includedFiles.put(fileReference, file);
            }
        }

        super.visitElement(fileReference);
    }

    @NotNull
    public IncludedFilesCollector collect(@NotNull PsiFile file)
    {
        visitFile(file);

        return this;
    }

    @NotNull
    public Map<DirectiveParamFileReference, VirtualFile> getResult()
    {
        return includedFiles;
    }

}
