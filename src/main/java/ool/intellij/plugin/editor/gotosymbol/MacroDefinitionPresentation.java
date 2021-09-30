package ool.intellij.plugin.editor.gotosymbol;

import javax.swing.*;

import ool.intellij.plugin.file.type.OxyTemplateFileType;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.Nullable;

/**
 * 2/6/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroDefinitionPresentation implements ItemPresentation
{
    private final String name;

    private final String namespace;

    private final String containingFileName;

    public MacroDefinitionPresentation(String name, String namespace, String containingFileName)
    {
        this.name = name;
        this.namespace = namespace;
        this.containingFileName = containingFileName;
    }

    @Nullable
    @Override
    public String getPresentableText()
    {
        return name;
    }

    @Nullable
    @Override
    public String getLocationString()
    {
        return namespace + " (" + containingFileName + ", " + namespace.replaceFirst(".+\\.", "") + ")";
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused)
    {
        return OxyTemplateFileType.INSTANCE.getIcon();
    }

}
