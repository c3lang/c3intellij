package org.c3lang.intellij.psi;

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.stubs.StubStreamExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ParamType
{
    public static final Companion Companion = new Companion();

    private final String name;
    private final ShortType type;

    public ParamType(@NotNull String name, @Nullable ShortType type)
    {
        this.name = name;
        this.type = type;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @Nullable
    public ShortType getType()
    {
        return type;
    }

    public static @NotNull List<ParamType> from(@NotNull List<C3ParamDecl> list)
    {
        return Companion.from(list);
    }

    public static @NotNull List<ParamType> toParamTypeList(@Nullable List<C3ParamDecl> list)
    {
        return Companion.toParamTypeList(list);
    }

    public static @NotNull List<ParamType> deserialize(@NotNull StubInputStream dataStream) throws IOException
    {
        return Companion.deserialize(dataStream);
    }

    public static void serialize(@NotNull StubOutputStream dataStream, @NotNull List<ParamType> parameterTypes) throws IOException
    {
        Companion.serialize(dataStream, parameterTypes);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ParamType paramType)) return false;
        return name.equals(paramType.name) && Objects.equals(type, paramType.type);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, type);
    }

    @Override
    public String toString()
    {
        return "ParamType(name=" + name + ", type=" + type + ")";
    }

    public static final class Companion
    {
        private Companion()
        {
        }

        public @NotNull List<ParamType> from(@NotNull List<C3ParamDecl> list)
        {
            List<ParamType> result = new ArrayList<>(list.size());
            for (C3ParamDecl decl : list)
            {
                C3Type type = decl.getParameter().getType();
                result.add(new ParamType(
                    decl.getParameter().getLastChild().getText(),
                    type != null ? ShortType.Companion.toShortType(type) : null
                ));
            }
            return result;
        }

        public @NotNull List<ParamType> toParamTypeList(@Nullable List<C3ParamDecl> list)
        {
            return list != null ? from(list) : List.of();
        }

        public @NotNull List<ParamType> deserialize(@NotNull StubInputStream dataStream) throws IOException
        {
            int count = dataStream.readVarInt();
            List<ParamType> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++)
            {
                String name = dataStream.readUTFFast();
                String typeValue = StubStreamExtensions.readNullableUTFFast(dataStream);
                String typePrefix = StubStreamExtensions.readNullableUTFFast(dataStream);
                result.add(new ParamType(name, typeValue != null ? new ShortType(typeValue, typePrefix) : null));
            }
            return result;
        }

        public void serialize(@NotNull StubOutputStream dataStream, @NotNull List<ParamType> parameterTypes) throws IOException
        {
            dataStream.writeVarInt(parameterTypes.size());
            for (ParamType parameterType : parameterTypes)
            {
                dataStream.writeUTFFast(parameterType.name);
                StubStreamExtensions.writeNullableUTFFast(dataStream, parameterType.type != null ? parameterType.type.getValue() : null);
                StubStreamExtensions.writeNullableUTFFast(dataStream, parameterType.type != null ? parameterType.type.getPrefix() : null);
            }
        }
    }
}
