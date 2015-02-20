package ool.idea.plugin.lexer;

import java.io.IOException;
import ool.idea.plugin.psi.OxyTemplateTypes;
import static com.intellij.psi.TokenType.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * 12/13/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroTest extends AbstractLexerTest
{
    @Test
    public void macroCloseTagTest() throws IOException
    {
        String input = "</m:foo.bar>";
        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_CLOSE_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_CLOSE_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void macroWithParamTest() throws IOException
    {
        String input = "<m:foo.bar param_name=\"param_value\">";
        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_OPEN_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void unpairedMacroWithParamTest() throws IOException
    {
        String input = "<m:foo.bar param_name=\"param_value\" />";
        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_UNPAIRED_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void expressionStatementTest() throws IOException
    {
        String input = "<m:oxy.ifTrue value=\"expr: true\" />";

        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, nextToken());

        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_UNPAIRED_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void macroHtmlMixTest() throws IOException
    {
        String input =
                "<div>\n" +
                "    <m:bar.baz>\n" +
                "        <li>\n" +
                "            <m:foo.bar />\n" +
                "        </li>\n" +
                "    </m:bar.baz>\n" +
                "</div>\n";

        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_TEMPLATE_HTML_CODE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_OPEN_TAG_END, nextToken());

        assertEquals(OxyTemplateTypes.T_TEMPLATE_HTML_CODE, nextToken());

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_UNPAIRED_TAG_END, nextToken());

        assertEquals(OxyTemplateTypes.T_TEMPLATE_HTML_CODE, nextToken());

        assertEquals(OxyTemplateTypes.T_XML_CLOSE_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_CLOSE_TAG_END, nextToken());

        assertEquals(OxyTemplateTypes.T_TEMPLATE_HTML_CODE, nextToken());

        assertEquals(null, nextToken());
    }

}