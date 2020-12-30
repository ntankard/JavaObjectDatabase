package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.dataField.ListDataField;
import com.ntankard.javaObjectDatabase.dataField.ListDataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore.Calculator;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore.Derived_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.end.EndSource_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.DatabaseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.SourceTest.SourceTestC.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
public class SourceTest {

    // TODO rework into listTest

    @Test
    @Execution(CONCURRENT)
    void textB() {
        List<Class<? extends DataObject>> knownTypes = Collections.singletonList(SourceTestC.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        List<String> objects = Arrays.asList("1", "2", "3", "4", "5", "6", "7");

        SourceTestC c1 = new SourceTestC(new ArrayList<>(), new ArrayList<>(), database);

        ((ListDataField<String>) c1.<List<String>>getField(SourceTestC_StringListFirst)).add(objects.get(0));
        assertEquals(1, c1.<List<String>>get(SourceTestC_StringListFirst).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSecond).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedA).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedB).size());

        ((ListDataField<String>) c1.<List<String>>getField(SourceTestC_StringListFirst)).add(objects.get(1));
        assertEquals(2, c1.<List<String>>get(SourceTestC_StringListFirst).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSecond).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedA).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedB).size());

        ((ListDataField<String>) c1.<List<String>>getField(SourceTestC_StringListFirst)).add(objects.get(2));
        assertEquals(3, c1.<List<String>>get(SourceTestC_StringListFirst).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSecond).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedA).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedB).size());

        ((ListDataField<String>) c1.<List<String>>getField(SourceTestC_StringListSecond)).add(objects.get(3));
        assertEquals(3, c1.<List<String>>get(SourceTestC_StringListFirst).size());
        assertEquals(1, c1.<List<String>>get(SourceTestC_StringListSecond).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedA).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedB).size());

        ((ListDataField<String>) c1.<List<String>>getField(SourceTestC_StringListSecond)).add(objects.get(4));
        assertEquals(3, c1.<List<String>>get(SourceTestC_StringListFirst).size());
        assertEquals(2, c1.<List<String>>get(SourceTestC_StringListSecond).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedA).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedB).size());

        ((ListDataField<String>) c1.<List<String>>getField(SourceTestC_StringListSecond)).add(objects.get(5));
        assertEquals(3, c1.<List<String>>get(SourceTestC_StringListFirst).size());
        assertEquals(3, c1.<List<String>>get(SourceTestC_StringListSecond).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedA).size());
        assertEquals(0, c1.<List<String>>get(SourceTestC_StringListSharedB).size());

        ((ListDataField<String>) c1.<List<String>>getField(SourceTestC_StringListSecond)).add(objects.get(2));
        assertEquals(3, c1.<List<String>>get(SourceTestC_StringListFirst).size());
        assertEquals(4, c1.<List<String>>get(SourceTestC_StringListSecond).size());
        assertEquals(1, c1.<List<String>>get(SourceTestC_StringListSharedA).size());
        assertEquals(1, c1.<List<String>>get(SourceTestC_StringListSharedB).size());

        ((ListDataField<String>) c1.<List<String>>getField(SourceTestC_StringListSecond)).add(objects.get(1));
        assertEquals(3, c1.<List<String>>get(SourceTestC_StringListFirst).size());
        assertEquals(5, c1.<List<String>>get(SourceTestC_StringListSecond).size());
        assertEquals(2, c1.<List<String>>get(SourceTestC_StringListSharedA).size());
        assertEquals(2, c1.<List<String>>get(SourceTestC_StringListSharedB).size());
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## SourceTestC ###################################################
    //------------------------------------------------------------------------------------------------------------------

    public static class SourceTestC extends DataObject {

        public interface StringList extends List<String> {
        }

        public final static String SourceTestC_StringListFirst = "getSourceTestC_StringListFirst";
        public final static String SourceTestC_StringListSecond = "getSourceTestC_StringListSecond";
        public final static String SourceTestC_StringListSharedA = "getSourceTestC_StringListSharedA";
        public final static String SourceTestC_StringListSharedB = "getSourceTestC_StringListSharedB";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new ListDataField_Schema<>(SourceTestC_StringListFirst, StringList.class));
            dataObjectSchema.get(SourceTestC_StringListFirst).setManualCanEdit(true);
            dataObjectSchema.add(new ListDataField_Schema<>(SourceTestC_StringListSecond, StringList.class));
            dataObjectSchema.get(SourceTestC_StringListSecond).setManualCanEdit(true);

            dataObjectSchema.add(new ListDataField_Schema<>(SourceTestC_StringListSharedA, StringList.class));
            dataObjectSchema.<List<String>>get(SourceTestC_StringListSharedA).setDataCore_factory(
                    new Derived_DataCore_Schema<>(
                            (Calculator<List<String>, SourceTestC>)
                                    container -> {
                                        List<String> list1 = container.get(SourceTestC_StringListFirst);
                                        List<String> list2 = container.get(SourceTestC_StringListSecond);

                                        List<String> finalList = new ArrayList<>();
                                        for (String string : list1) {
                                            if (list2.contains(string)) {
                                                finalList.add(string);
                                            }
                                        }
                                        return finalList;
                                    },
                            new EndSource_Schema<>(SourceTestC_StringListFirst),
                            new EndSource_Schema<>(SourceTestC_StringListSecond)));

            dataObjectSchema.add(new ListDataField_Schema<>(SourceTestC_StringListSharedB, StringList.class));
            dataObjectSchema.<List<String>>get(SourceTestC_StringListSharedB).setDataCore_factory(
                    new Derived_DataCore_Schema<>(
                            (Calculator<List<String>, SourceTestC>)
                                    container -> {
                                        List<String> list1 = container.get(SourceTestC_StringListFirst);
                                        List<String> list2 = container.get(SourceTestC_StringListSecond);

                                        List<String> finalList = new ArrayList<>();
                                        for (String string : list1) {
                                            if (list2.contains(string)) {
                                                finalList.add(string);
                                            }
                                        }
                                        return finalList;
                                    },
                            new EndSource_Schema<>(SourceTestC_StringListFirst, new Source_Schema.IndividualCalculator<List<String>>() {
                                @Override
                                public void doIndividualRecalculate(Derived_DataCore<List<String>, ?> parent, List<String> oldValue, List<String> newValue) {
                                    SourceTestC castContainer = ((SourceTestC) parent.getDataField().getContainer());
                                    if (oldValue != null) {
                                        for (String removed : oldValue) {
                                            ((ListDataField) castContainer.getField(SourceTestC_StringListSharedB)).remove(removed);
                                        }
                                    }
                                    if (newValue != null) {
                                        for (String added : newValue) {
                                            if (((ListDataField) castContainer.getField(SourceTestC_StringListSecond)).get().contains(added)) {
                                                if (!((ListDataField) castContainer.getField(SourceTestC_StringListSharedB)).get().contains(added)) {
                                                    ((ListDataField) castContainer.getField(SourceTestC_StringListSharedB)).add(added);
                                                }
                                            }
                                        }
                                    }
                                }
                            }),
                            new EndSource_Schema<>(SourceTestC_StringListSecond, new Source_Schema.IndividualCalculator<List<String>>() {
                                @Override
                                public void doIndividualRecalculate(Derived_DataCore<List<String>, ?> parent, List<String> oldValue, List<String> newValue) {
                                    SourceTestC castContainer = ((SourceTestC) parent.getDataField().getContainer());
                                    if (oldValue != null) {
                                        for (String removed : oldValue) {
                                            ((ListDataField) castContainer.getField(SourceTestC_StringListSharedB)).removeFromDataCore(removed);
                                        }
                                    }
                                    if (newValue != null) {
                                        for (String added : newValue) {
                                            if (((ListDataField) castContainer.getField(SourceTestC_StringListFirst)).get().contains(added)) {
                                                if (!((ListDataField) castContainer.getField(SourceTestC_StringListSharedB)).get().contains(added)) {
                                                    ((ListDataField) castContainer.getField(SourceTestC_StringListSharedB)).addFromDataCore(added);
                                                }
                                            }
                                        }
                                    }
                                }
                            })));

            return dataObjectSchema.finaliseContainer(SourceTestC.class);
        }

        public SourceTestC(List<String> stringListFirst, List<String> stringListSecond, Database database) {
            this(database);
            setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                    , SourceTestC_StringListFirst, stringListFirst
                    , SourceTestC_StringListSecond, stringListSecond
            );
        }

        public SourceTestC(Database database) {
            super(database);
        }
    }
}
