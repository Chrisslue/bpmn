package de.monticore.expressions.timeexpressions._ast;

import de.monticore.expressions.timeexpressions.AbstractTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ISO8601 extends AbstractTest {

    @Test
    void testDate() throws IOException {
        final Optional<ASTDateExpr> date = parser.parse_StringDateExpr("on 2020-02-04");

        assertEquals("2020-02-04", date.get().printISO8601());
    }

    @Test
    void testTime() throws IOException {
        final Optional<ASTTimeExpr> time = parser.parse_StringTimeExpr("at 13:37");

        assertEquals("13:37:00.000", time.get().printISO8601());
    }

    @Test
    void testAfter() throws IOException {
        final Optional<ASTAfterExpr> after = parser.parse_StringAfterExpr("after P3DT27H13M");

        assertEquals("P4DT3H13M", after.get().printISO8601());
    }

    @Test
    void testEvery() throws IOException {
        final Optional<ASTEveryExpr> after = parser.parse_StringEveryExpr("every PT5M");

        assertEquals("R/PT5M", after.get().printISO8601());
    }

    @Test
    void testEveryWithLimit() throws IOException {
        final Optional<ASTEveryExpr> after = parser.parse_StringEveryExpr("3 times every PT5M");

        assertEquals("R3/PT5M", after.get().printISO8601());
    }

    @Test
    void testEveryWitStart() throws IOException {
        final Optional<ASTEveryExpr> after = parser.parse_StringEveryExpr("start on 2020-02-04 at 13:37, every PT5M");

        assertEquals("R/2020-02-04T13:37:00.000/PT5M", after.get().printISO8601());
    }

}
