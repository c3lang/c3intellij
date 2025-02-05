package org.c3lang.intellij.psi;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.stubs.IStubElementType;
import org.c3lang.intellij.stubs.*;

public class C3StubElementTypeFactory {
    private final static Logger log = Logger.getInstance(C3StubElementTypeFactory.class);

    public static final String FUNC_DEF = "FUNC_DEF";
    public static final String MODULE = "MODULE";
    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String MACRO_DEFINITION = "MACRO_DEFINITION";
    public static final String CONST_DECLARATION_STMT = "CONST_DECLARATION_STMT";
    public static final String STRUCT_MEMBER_DECLARATION = "STRUCT_MEMBER_DECLARATION";
    public static final String STRUCT_DECLARATION = "STRUCT_DECLARATION";
    public static final String ENUM_DECLARATION = "ENUM_DECLARATION";

    public static IStubElementType<?, ?> stubFactory(String name) {
        final C3StubElementType<?, ?> type = switch (name) {
            case FUNC_DEF -> C3FuncDefElementType.getInstance();
            case MODULE -> C3ModuleElementType.getInstance();
            case TYPE_NAME -> C3TypeNameElementType.getInstance();
            case MACRO_DEFINITION -> C3MacroDefinitionElementType.getInstance();
            case CONST_DECLARATION_STMT -> C3ConstDeclarationStmtElementType.getInstance();
            case STRUCT_MEMBER_DECLARATION -> C3StructMemberDeclarationElementType.getInstance();
            case STRUCT_DECLARATION -> C3StructDeclarationElementType.getInstance();
            case ENUM_DECLARATION -> C3EnumConstantElementType.getInstance();
            default -> null;
        };
        if (type == null) {
            log.warn("Unknown stub factory: " + name);
        }
        return type;
    }

}