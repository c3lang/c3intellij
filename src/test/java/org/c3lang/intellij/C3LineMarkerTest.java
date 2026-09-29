package org.c3lang.intellij;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class C3LineMarkerTest extends BasePlatformTestCase
{
    public void testRunMarkerOnMain()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn int main()
            {
                return 0;
            }
            fn void helper()
            {
            }
            struct MyTr
            {
                int x;
            }
            fn int MyTr.main(MyTr* self)
            {
                return 0;
            }
            """);

        var gutters = myFixture.findAllGutters();
        assertNotNull(gutters);
        long runMarkers = gutters.stream()
            .filter(gutter -> gutter.getTooltipText() != null && gutter.getTooltipText().contains("Run main"))
            .count();
        assertEquals("Expected exactly one 'Run main' gutter, got: " + gutters, 1, runMarkers);
    }
}
