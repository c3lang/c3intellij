package org.c3lang.intellij;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3Icons {
    public static final Icon FILE = IconLoader.getIcon("/icons/c3.svg", C3Icons.class.getClassLoader());
    public static final Icon LIB_FILE = IconLoader.getIcon("/icons/c3l.svg", C3Icons.class.getClassLoader());

    public static class Nodes {
        public static final Icon BITSTRUCT = IconLoader.getIcon("/icons/nodes/bitstruct.svg", C3Icons.class.getClassLoader());
        public static final Icon CONSTANT = IconLoader.getIcon("/icons/nodes/constant.svg", C3Icons.class.getClassLoader());
        public static final Icon ENUM = IconLoader.getIcon("/icons/nodes/enum.svg", C3Icons.class.getClassLoader());
        public static final Icon FAULT = IconLoader.getIcon("/icons/nodes/fault.svg", C3Icons.class.getClassLoader());
        public static final Icon FUNCTION = IconLoader.getIcon("/icons/nodes/function.svg", C3Icons.class.getClassLoader());
        public static final Icon INTERFACE = IconLoader.getIcon("/icons/nodes/interface.svg", C3Icons.class.getClassLoader());
        public static final Icon MACRO = IconLoader.getIcon("/icons/nodes/macro.svg", C3Icons.class.getClassLoader());
        public static final Icon MODULE = IconLoader.getIcon("/icons/nodes/module.svg", C3Icons.class.getClassLoader());
        public static final Icon STRUCT = IconLoader.getIcon("/icons/nodes/struct.svg", C3Icons.class.getClassLoader());
        public static final Icon STRUCT_FIELD = IconLoader.getIcon("/icons/nodes/struct_field.svg", C3Icons.class.getClassLoader());
        public static final Icon UNION = IconLoader.getIcon("/icons/nodes/union.svg", C3Icons.class.getClassLoader());
        public static final Icon VARIABLE = IconLoader.getIcon("/icons/nodes/variable.svg", C3Icons.class.getClassLoader());
    }

}
