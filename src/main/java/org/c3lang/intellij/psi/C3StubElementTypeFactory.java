package org.c3lang.intellij.psi;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.stubs.IStubElementType;
import org.c3lang.intellij.C3SourceFileType;
import org.c3lang.intellij.stubs.types.C3FuncDefElementType;
import org.c3lang.intellij.stubs.types.C3ModuleElementType;
import org.c3lang.intellij.stubs.types.C3StructDeclarationElementType;

public class C3StubElementTypeFactory {
    private final static Logger log = Logger.getInstance(C3StubElementTypeFactory.class.getName());

    public static final String FUNC_DEF = "FUNC_DEF";
    public static final String MODULE = "MODULE";
    public static final String STRUCT_DECLARATION = "STRUCT_DECLARATION";

    public static C3TypeName createTypeName(Project project, String name) {
        return (C3TypeName) createFile(project, "struct " + name + "{int a}").getNode().findChildByType(C3Types.TYPE_NAME);
    }

    public static C3File createFile(Project project, String text) {
        String name = "dummy.c3";
        return (C3File) PsiFileFactory.getInstance(project).createFileFromText(name, C3SourceFileType.INSTANCE, text);
    }

    public static IStubElementType<?, ?> stubFactory(String name) {
        final C3StubElementType<?, ?> type = switch (name) {
            case FUNC_DEF -> C3FuncDefElementType.INSTANCE;
            case MODULE -> C3ModuleElementType.INSTANCE;
            case STRUCT_DECLARATION ->  C3StructDeclarationElementType.INSTANCE;
            default -> null;
        };
        if (type == null) {
            log.warn("Unknown stub factory: " + name);
        }
        return type;
    }

}