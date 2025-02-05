package org.c3lang.intellij.psi;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.stubs.IStubElementType;
import org.c3lang.intellij.C3SourceFileType;
import org.c3lang.intellij.stubs.*;

public class C3StubElementTypeFactory {
    private final static Logger log = Logger.getInstance(C3StubElementTypeFactory.class.getName());

    public static final String FUNC_DEF = "FUNC_DEF";
    public static final String MODULE = "MODULE";
    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String MACRO_DEFINITION = "MACRO_DEFINITION";
    public static final String CONST_DECLARATION_STMT = "CONST_DECLARATION_STMT";

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
            case TYPE_NAME ->  C3TypeNameElementType.Companion.getInstance();
            case MACRO_DEFINITION ->  C3MacroDefinitionElementType.Companion.getInstance();
            case CONST_DECLARATION_STMT ->  C3ConstDeclarationStmtElementType.Companion.getInstance();
            default -> null;
        };
        if (type == null) {
            log.warn("Unknown stub factory: " + name);
        }
        return type;
    }

}