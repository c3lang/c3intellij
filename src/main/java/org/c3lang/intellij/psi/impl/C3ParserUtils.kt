@file:JvmName("C3ParserUtils")

package org.c3lang.intellij.psi.impl

import ai.grazie.utils.dropPostfix
import com.intellij.util.text.nullize
import org.c3lang.intellij.psi.C3Path
import org.c3lang.intellij.psi.C3PathIdent
import org.c3lang.intellij.psi.C3Types

/* C3PathIdent - emulation of original rule */
fun getPath(psi: C3PathIdent): C3Path? {
    return psi.node.findChildByType(C3Types.PATH)?.let(::C3PathImpl)
}

/* C3Path */
fun getImportIntention(psi: C3Path): String? {
    val text = psi.text
    // delete last ::
    return text.dropPostfix("::").nullize()
}
