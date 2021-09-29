package ool.intellij.plugin.lang;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.templateLanguages.TemplateLanguage;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplate extends Language implements TemplateLanguage
{
    public static final OxyTemplate INSTANCE = new OxyTemplate();

    private OxyTemplate()
    {
        super("OxyTemplate", "application/x-oxy-template");
    }

    public static LanguageFileType getDefaultTemplateLang()
    {
        return HtmlFileType.INSTANCE;
    }

}
