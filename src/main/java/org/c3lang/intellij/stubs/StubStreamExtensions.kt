package org.c3lang.intellij.stubs

import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.DataInputOutputUtil

fun StubOutputStream.writeNullableUTFFast(arg: String?) {
    DataInputOutputUtil.writeNullable(this, arg, this::writeUTFFast)
}

fun StubInputStream.readNullableUTFFast(): String? {
    return DataInputOutputUtil.readNullable(this, this::readUTFFast)
}