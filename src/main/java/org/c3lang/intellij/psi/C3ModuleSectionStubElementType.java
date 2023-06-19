package org.c3lang.intellij.psi;

import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.LighterASTTokenNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LightTreeUtil;
import com.intellij.psi.stubs.*;
import com.intellij.util.CharTable;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.psi.impl.C3ModuleSectionImpl;
import org.c3lang.intellij.psi.impl.C3ModuleSectionStubImpl;
import org.c3lang.intellij.stubs.C3ModulesIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class C3ModuleSectionStubElementType extends ILightStubElementType<C3ModuleSectionStub, C3ModuleSection>
{
    public C3ModuleSectionStubElementType()
    {
        super("C3ModuleSection", C3Language.INSTANCE);
    }

    @Override public @NotNull C3ModuleSectionStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node,
                                                             @NotNull StubElement<?> parentStub)
    {
        LighterASTNode path_node = LightTreeUtil.firstChildOfType(tree, node, C3Types.PATH_IDENT);
        String key = path_node == null ? "???" : intern(tree.getCharTable(), path_node);
        return new C3ModuleSectionStubImpl(parentStub, this, key);
    }

    @Override public C3ModuleSection createPsi(@NotNull C3ModuleSectionStub stub)
    {
        return new C3ModuleSectionImpl(stub, this);
    }

    @Override public @NotNull C3ModuleSectionStub createStub(@NotNull C3ModuleSection psi, StubElement<? extends PsiElement> parentStub)
    {
        String path = psi.getModule().getPathIdent().getText().replace('\t', ' ').replace(" ", "");
        return new C3ModuleSectionStubImpl(parentStub, this, path);
    }

    @Override public @NotNull String getExternalId()
    {
        return "c3.MODULE_SECTION";
    }

    @Override public void serialize(@NotNull C3ModuleSectionStub c3ModuleSectionStub,
                                    @NotNull StubOutputStream stubOutputStream) throws IOException
    {
        stubOutputStream.writeName(c3ModuleSectionStub.getModuleName());
    }

    public static String intern(@NotNull CharTable table, @NotNull LighterASTNode node)
    {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }

    @Override
    public @NotNull C3ModuleSectionStub deserialize(@NotNull StubInputStream stream, StubElement parentStub) throws
                                                                                                             IOException
    {
        String name = stream.readNameString();
        if (name == null) name = "???";
        return new C3ModuleSectionStubImpl(parentStub, this, name);
    }


    @Override public void indexStub(@NotNull C3ModuleSectionStub stub, @NotNull IndexSink sink)
    {
        sink.occurrence(C3ModulesIndex.KEY, stub.getModuleName());
    }
}
