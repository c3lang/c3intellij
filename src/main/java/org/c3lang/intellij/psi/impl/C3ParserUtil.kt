package org.c3lang.intellij.psi.impl

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import org.c3lang.intellij.psi.C3Types.*

object C3ParserUtil : GeneratedParserUtilBase() {
    private val otherIdentTypes = TokenSet.create(
        CONST_IDENT, AT_IDENT, TYPE_IDENT, AT_TYPE_IDENT
    )

    @JvmStatic
    fun parsePathIdent(
        b: PsiBuilder,
        level: Int
    ): Boolean {
        val marker: PsiBuilder.Marker = b.mark()

        if (!consumeToken(b, IDENT)) {
            marker.rollbackTo()
            return false
        }

        val scope = b.tokenType == SCOPE
        while (consumeToken(b, SCOPE)) {
            if (b.lookAhead(1) != SCOPE) {
                break
            }
            consumeToken(b, IDENT)
        }

        if (otherIdentTypes.contains(b.tokenType)) {
            marker.rollbackTo()
            return false
        }

        if (scope) {
            marker.done(PATH)
        } else {
            marker.drop()
        }

        if (b.tokenType == IDENT) {
            consumeToken(b, IDENT)
        }

        return true
    }

//    @JvmStatic
//    fun parsePathIdent(
//        b: PsiBuilder,
//        level: Int
//    ): Boolean {
//        val marker: PsiBuilder.Marker = b.mark()
//
//        if (!consumeToken(b, IDENT)) {
//            marker.rollbackTo()
//            return false
//        }
//
//        while (consumeToken(b, SCOPE)) {
//            if (b.lookAhead(1) != SCOPE) {
//                break
//            }
//            if (!consumeToken(b, IDENT)) {
//                // any other legal next token with "path"
//                when (b.tokenType) {
//                    CONST_IDENT, AT_IDENT, TYPE_IDENT, AT_TYPE_IDENT -> {
//                        marker.rollbackTo()
//                        return false
//                    }
//
//                    else -> {
//                        marker.done(PATH)
//                        return true
//                    }
//                }
//            }
//        }
//
//        marker.done(PATH)
//
//        if (b.tokenType == IDENT) {
//            consumeToken(b, IDENT)
//        }
//
//        return true
//    }
}