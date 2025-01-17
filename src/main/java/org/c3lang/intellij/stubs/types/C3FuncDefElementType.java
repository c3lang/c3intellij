package org.c3lang.intellij.stubs.types;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.io.DataInputOutputUtil;
import com.intellij.util.io.StringRef;
import org.c3lang.intellij.index.C3FunctionLongNameIndex;
import org.c3lang.intellij.index.C3FunctionShortNameIndex;
import org.c3lang.intellij.index.C3FunctionSuffixNameIndex;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.impl.C3FuncDefImpl;
import org.c3lang.intellij.stubs.C3FuncDefStub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class C3FuncDefElementType extends C3StubElementType<C3FuncDefStub, C3FuncDef> {

    public static final C3FuncDefElementType INSTANCE = new C3FuncDefElementType();

    public C3FuncDefElementType() {
        super(C3StubElementTypeFactory.FUNC_DEF);
    }

    @Override
    public C3FuncDef createPsi(@NotNull C3FuncDefStub stub) {
        return new C3FuncDefImpl(stub, this);
    }

    @Override
    public @NotNull C3FuncDefStub createStub(@NotNull C3FuncDef psi, StubElement<? extends PsiElement> stubElement) {
        final var moduleSection = PsiTreeUtil.getParentOfType(psi, C3ModuleSection.class);
        final var funcName = Objects.requireNonNull(PsiTreeUtil.findChildOfType(psi, C3FuncName.class));
        final var returnType = Objects.requireNonNull(PsiTreeUtil.findChildOfType(psi, C3OptionalType.class)).getType().getText();

        final String module = Optional.ofNullable(moduleSection)
                .map(C3ModuleSection::getModule)
                .map(C3Module::getModulePath)
                .map(PsiElement::getText).orElse(null);

        return new C3FuncDefStub(stubElement,
                this,
                StringRef.fromString(funcName.getText()),
                module,
                returnType
        );
    }

    @Override
    public void serialize(@NotNull C3FuncDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getFunctionName());
        DataInputOutputUtil.writeNullable(dataStream, stub.getModule(), dataStream::writeUTFFast);
        dataStream.writeUTFFast(stub.getReturnType());
    }

    @Override
    public void indexStub(@NotNull C3FuncDefStub stub, @NotNull IndexSink sink) {
        sink.occurrence(C3FunctionShortNameIndex.KEY, stub.getFunctionName());
        sink.occurrence(C3FunctionLongNameIndex.KEY, stub.getModule() + "::" + stub.getFunctionName());

        Optional.ofNullable(stub.getModule())
                .map(str -> str.lastIndexOf("::"))
                .filter(index -> index >= 0)
                .map(scope -> stub.getModule().substring(scope + 2) + "::" + stub.getFunctionName())
                .ifPresent(functionName -> sink.occurrence(C3FunctionSuffixNameIndex.KEY, functionName));
    }

    @Override
    public @NotNull C3FuncDefStub deserialize(@NotNull StubInputStream dataStream, StubElement stubElement) throws IOException {
        final var functionName = dataStream.readName();
        final var module = DataInputOutputUtil.readNullable(dataStream, dataStream::readUTFFast);
        final var returnType = dataStream.readUTFFast();

        return new C3FuncDefStub(
                stubElement,
                this,
                functionName,
                module,
                returnType
        );
    }
}
