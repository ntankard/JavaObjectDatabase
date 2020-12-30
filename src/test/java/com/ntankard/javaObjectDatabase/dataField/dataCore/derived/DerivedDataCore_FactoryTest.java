package com.ntankard.javaObjectDatabase.dataField.dataCore.derived;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class DerivedDataCore_FactoryTest {

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Test
    @Execution(CONCURRENT)
    void createDirectDerivedDataCore() {
        // TODO test that the generated source line up with the supplied fields
        // TODO black box test that the value always matches the source (this is very similar to test in End_SourceTest
    }

    @Test
    @Execution(CONCURRENT)
    void createSelfParentList() {
        // TODO test that the correct type of source is created (1 chain to self)
    }

    @Test
    @Execution(CONCURRENT)
    void createMultiParentList() {
        // TODO check that for each parent a correct source chain is created
        // TODO check that each chain ends in DataObject_Child
        // TODO check that all source have an individual calculator
        // TODO black box test that a multi parent list is generated
    }
}
