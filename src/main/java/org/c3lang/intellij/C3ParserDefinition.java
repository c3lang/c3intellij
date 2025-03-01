package org.c3lang.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.lexer.C3LexerAdapter;
import org.c3lang.intellij.parser.C3Parser;
import org.c3lang.intellij.psi.C3ElementType;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;

public class C3ParserDefinition implements ParserDefinition {

    public static IElementType LINE_COMMENT = new C3ElementType("LINE_COMMENT");
    public static IElementType BLOCK_COMMENT = new C3ElementType("BLOCK_COMMENT_LEAD");
    public static IElementType DOC_COMMENT = new C3ElementType("DOC_COMMENT");

    public static C3FileElementType FILE = new C3FileElementType();

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new C3LexerAdapter();
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return C3TokenSets.WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return C3TokenSets.COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return C3TokenSets.STRINGS;
    }

    @NotNull
    @Override
    public PsiParser createParser(final Project project) {
        return new C3Parser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new C3File(viewProvider);
    }

    @Override
    @NotNull
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return C3Types.Factory.createElement(node);
    }
}
