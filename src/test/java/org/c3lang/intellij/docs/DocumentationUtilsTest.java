package org.c3lang.intellij.docs;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DocumentationUtilsTest
{
    @Test
    public void extractsOptionalReturnWithDescription()
    {
        List<DocumentationUtils.OptionalReturnDoc> returns = DocumentationUtils.extractOptionalReturnsFromDoc(
            "@return? NO_MORE_ELEMENT : \"If the queue is empty\""
        );

        assertEquals(1, returns.size());
        assertEquals(List.of("NO_MORE_ELEMENT"), returns.get(0).names());
        assertEquals("If the queue is empty", returns.get(0).description());
    }

    @Test
    public void extractsMultipleOptionalReturns()
    {
        List<DocumentationUtils.OptionalReturnDoc> returns = DocumentationUtils.extractOptionalReturnsFromDoc(
            "@return? mem::INVALID_ALLOC_SIZE, mem::OUT_OF_MEMORY"
        );

        assertEquals(1, returns.size());
        assertEquals(List.of("mem::INVALID_ALLOC_SIZE", "mem::OUT_OF_MEMORY"), returns.get(0).names());
        assertEquals("", returns.get(0).description());
    }

    @Test
    public void extractsOptionalReturnWithBangPath()
    {
        List<DocumentationUtils.OptionalReturnDoc> returns = DocumentationUtils.extractOptionalReturnsFromDoc(
            "@return? OutStream.write!, INCOMPLETE_WRITE"
        );

        assertEquals(1, returns.size());
        assertEquals(List.of("OutStream.write!", "INCOMPLETE_WRITE"), returns.get(0).names());
        assertEquals("", returns.get(0).description());
    }

    @Test
    public void extractsRequireWithColonDescription()
    {
        List<DocumentationUtils.ContractDoc> requires = DocumentationUtils.extractRequiresFromDoc(
            "@require index < self.size && index >= 0  : \"Index out of range\""
        );

        assertEquals(1, requires.size());
        assertEquals("index < self.size && index >= 0", requires.get(0).expression());
        assertEquals("Index out of range", requires.get(0).description());
    }

    @Test
    public void doesNotExtractRequireDescriptionWithoutColon()
    {
        List<DocumentationUtils.ContractDoc> requires = DocumentationUtils.extractRequiresFromDoc(
            "@require alignment > 0 `alignment must be non zero`"
        );

        assertEquals(1, requires.size());
        assertEquals("alignment > 0 `alignment must be non zero`", requires.get(0).expression());
        assertEquals("", requires.get(0).description());
    }

    @Test
    public void extractsConsecutiveRequiresAndEnsures()
    {
        String docs = """
            @require @kindof(array_ptr) == POINTER
            @require @typeis(arr1[0], $typeof(arr2[0])) : "Arrays must have the same type"
            @ensure result.len == arr1.len + arr2.len
            """;

        List<DocumentationUtils.ContractDoc> requires = DocumentationUtils.extractRequiresFromDoc(docs);
        List<DocumentationUtils.ContractDoc> ensures = DocumentationUtils.extractEnsuresFromDoc(docs);

        assertEquals(2, requires.size());
        assertEquals("@kindof(array_ptr) == POINTER", requires.get(0).expression());
        assertEquals("", requires.get(0).description());
        assertEquals("@typeis(arr1[0], $typeof(arr2[0]))", requires.get(1).expression());
        assertEquals("Arrays must have the same type", requires.get(1).description());
        assertEquals(1, ensures.size());
        assertEquals("result.len == arr1.len + arr2.len", ensures.get(0).expression());
        assertEquals("", ensures.get(0).description());
    }

    @Test
    public void keepsTrailingExpressionStringLiteralWhenItFollowsOperator()
    {
        List<DocumentationUtils.ContractDoc> requires = DocumentationUtils.extractRequiresFromDoc(
            "@require value == \"literal\""
        );

        assertEquals(1, requires.size());
        assertEquals("value == \"literal\"", requires.get(0).expression());
        assertEquals("", requires.get(0).description());
    }

    @Test
    public void ignoresRequireWithBareDescriptionSeparator()
    {
        List<DocumentationUtils.ContractDoc> requires = DocumentationUtils.extractRequiresFromDoc(
            "@require index < self.size :"
        );

        assertEquals(0, requires.size());
    }

    @Test
    public void ignoresRequireWithNonStringDescription()
    {
        List<DocumentationUtils.ContractDoc> requires = DocumentationUtils.extractRequiresFromDoc(
            "@require index < self.size : Index out of range"
        );

        assertEquals(0, requires.size());
    }

    @Test
    public void keepsNestedTernaryColonAsPartOfRequireExpression()
    {
        List<DocumentationUtils.ContractDoc> requires = DocumentationUtils.extractRequiresFromDoc(
            "@require (i ? \"a\" : \"b\") == \"a\""
        );

        assertEquals(1, requires.size());
        assertEquals("(i ? \"a\" : \"b\") == \"a\"", requires.get(0).expression());
        assertEquals("", requires.get(0).description());
    }

    @Test
    public void extractsDescriptionAfterNestedRequireExpression()
    {
        List<DocumentationUtils.ContractDoc> requires = DocumentationUtils.extractRequiresFromDoc(
            "@require (i ? \"a\" : \"b\") == \"a\" : \"Expected a\""
        );

        assertEquals(1, requires.size());
        assertEquals("(i ? \"a\" : \"b\") == \"a\"", requires.get(0).expression());
        assertEquals("Expected a", requires.get(0).description());
    }
}
