package org.c3lang.intellij.psi;

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public sealed interface StructDeclarationFields permits StructDeclarationFields.Complex, StructDeclarationFields.Simple
{
    Companion Companion = new Companion();

    @NotNull
    FullyQualifiedPath getDeclaredIn();

    void serialize(@NotNull StubOutputStream stream) throws IOException;

    static @Nullable StructDeclarationFields build(@NotNull C3StructMemberDeclaration source)
    {
        return Companion.build(source);
    }

    static @NotNull StructDeclarationFields deserialize(@NotNull StubInputStream stream) throws IOException
    {
        return Companion.deserialize(stream);
    }

    final class Complex implements StructDeclarationFields
    {
        private final FullyQualifiedPath declaredIn;

        public Complex(@NotNull FullyQualifiedPath declaredIn)
        {
            this.declaredIn = declaredIn;
        }

        @Override
        public @NotNull FullyQualifiedPath getDeclaredIn()
        {
            return declaredIn;
        }

        @Override
        public void serialize(@NotNull StubOutputStream stream) throws IOException
        {
            stream.writeUTFFast(declaredIn.getTypeName().getFullName());
            stream.writeUTFFast(declaredIn.getPath());
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Complex complex)) return false;
            return declaredIn.equals(complex.declaredIn);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(declaredIn);
        }

        @Override
        public String toString()
        {
            return "Complex(declaredIn=" + declaredIn + ")";
        }
    }

    final class Simple implements StructDeclarationFields
    {
        private final FullyQualifiedPath declaredIn;

        public Simple(@NotNull FullyQualifiedPath declaredIn)
        {
            this.declaredIn = declaredIn;
        }

        @Override
        public @NotNull FullyQualifiedPath getDeclaredIn()
        {
            return declaredIn;
        }

        @Override
        public void serialize(@NotNull StubOutputStream stream) throws IOException
        {
            stream.writeUTFFast(declaredIn.getTypeName().getFullName());
            stream.writeUTFFast(declaredIn.getPath());
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Simple simple)) return false;
            return declaredIn.equals(simple.declaredIn);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(declaredIn);
        }

        @Override
        public String toString()
        {
            return "Simple(declaredIn=" + declaredIn + ")";
        }
    }

    final class Companion
    {
        private Companion()
        {
        }

        public @Nullable StructDeclarationFields build(@NotNull C3StructMemberDeclaration source)
        {
            C3Type type = source.getType();
            if (type == null || type.getBaseType() == null) return null;

            FullyQualifiedName declaredIn = source.getDeclaredIn();
            if (declaredIn == null) return null;

            String declaredInPathPath = source.getDeclaredInPath();
            if (type.getBaseType().getFirstChild().getNode().getElementType() == C3Types.TYPE_IDENT)
            {
                return new Complex(new FullyQualifiedPath(
                    declaredIn,
                    declaredInPathPath != null ? declaredInPathPath : "<empty>"
                ));
            }

            return new Simple(new FullyQualifiedPath(declaredIn, "-build-"));
        }

        public @NotNull StructDeclarationFields deserialize(@NotNull StubInputStream stream) throws IOException
        {
            FullyQualifiedName typeName = FullyQualifiedName.Companion.parse(stream.readUTFFast());
            String path = stream.readUTFFast();
            return new Complex(new FullyQualifiedPath(typeName, path));
        }
    }
}
