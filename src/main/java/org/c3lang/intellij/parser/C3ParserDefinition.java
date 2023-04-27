package org.c3lang.intellij.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.C3TokenSets;
import org.c3lang.intellij.lexer.C3LexerAdapter;
import org.c3lang.intellij.parser.psi.C3ElementType;
import org.c3lang.intellij.parser.psi.C3File;
import org.c3lang.intellij.parser.psi.C3Types;
import org.jetbrains.annotations.NotNull;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3ParserDefinition implements ParserDefinition, C3TokenSets
{

    public static IElementType LINE_COMMENT = new C3ElementType("LINE_COMMENT");
    public static IElementType MULTI_COMMENT = new C3ElementType("MULTI_COMMENT_LEAD");
    public static IElementType LINE_DOC_COMMENT = new C3ElementType("LINE_DOC_COMMENT");
    public static IElementType DOC_COMMENT = new C3ElementType("DOC_COMMENT");

    public static IFileElementType FILE = new IFileElementType(C3Language.INSTANCE);


    @NotNull
    @Override
    public Lexer createLexer(Project project)
    {
        return new C3LexerAdapter();
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens()
    {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens()
    {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements()
    {
        return STRINGS;
    }

    @NotNull
    @Override
    public PsiParser createParser(final Project project)
    {
        return new C3Parser();
    }

    @NotNull @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider)
    {
        return new C3File(viewProvider);
    }

    @Override
    @NotNull
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right)
    {
        return SpaceRequirements.MAY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node)
    {
        return C3Types.Factory.createElement(node);
    }
}
