package org.c3lang.intellij;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3ColorSettingsPage implements ColorSettingsPage
{
    private static AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
        new AttributesDescriptor("Syntax error", C3SyntaxHighlighter.BAD_CHARACTER),
        new AttributesDescriptor("Braces", C3SyntaxHighlighter.BRACES),
    };

    @Override
    public @Nullable Icon getIcon() {
        return C3Icons.FILE;
    }


    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new C3SyntaxHighlighter();
    }

    @Override
    public @NonNls @NotNull String getDemoText() {
        return "module stack <Type>;\n" +
                "// Above: the parameterized type is applied to the entire module.\n" +
                "\n" +
                "struct Stack\n" +
                "{\n" +
                "    usz capacity;\n" +
                "    usz size;\n" +
                "    Type* elems;\n" +
                "}\n" +
                "\n" +
                "// The type methods offers dot syntax calls,\n" +
                "// so this function can either be called \n" +
                "// Stack.push(&my_stack, ...) or\n" +
                "// my_stack.push(...)\n" +
                "fn void Stack.push(Stack* this, Type element)\n" +
                "{\n" +
                "    if (this.capacity == this.size)\n" +
                "    {\n" +
                "        this.capacity *= 2;\n" +
                "\t\tif (this.capacity < 16) this.capacity = 16;\n" +
                "        this.elems = mem::realloc(this.elems, Type.sizeof * this.capacity);\n" +
                "    }\n" +
                "    this.elems[this.size++] = element;\n" +
                "}\n" +
                "\n" +
                "fn Type Stack.pop(Stack* this)\n" +
                "{\n" +
                "    assert(this.size > 0);\n" +
                "    return this.elems[--this.size];\n" +
                "}\n" +
                "\n" +
                "fn bool Stack.empty(Stack* this)\n" +
                "{\n" +
                "    return !this.size;\n" +
                "}";
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull String getDisplayName() {
        return "C3";
    }
}
