### DataField (TBD)
#### FieldValidator
FieldValidator's are used to limit the available range of a data field above just its type. Any number of them can be 
attached to a DataField_Schema. They are check in series before any new value is applied to the field. If any of them 
fail the field will throw a NonCorruptingException.

FieldValidator's do not have a schema. They are attached directly to the DataField_Schema. They store no data about the 
field, all relevant context is only given at the time of validation. The FieldValidator should only need to be checked 
if the field its attaches to changes. If the range of the field depends on another field the Share_FieldValidator below 
should be used. The validator should never fail if it was run at any time other than the field change.

FieldValidator is an interface that can be implemented at a field by field bases or one of the common one's below can be 
used. 

FieldValidator are only run after a DataField is fully setup. As a result the field can go through invalid states during 
setup with no issues. For example all fields start as null.

@TODO 

If a FieldValidator fails and was a result of a change made in another field (the field was driven by a DataCore) 
the exception is escalated to a Corrupting Exception (https://www.wrike.com/open.htm?id=732098723)

@TODO 

If an integrity check is run, and a validator fails a Corrupting_Exception is raised. 
(https://www.wrike.com/open.htm?id=732099409)

##### Null_FieldValidator (used by all DataField_Schema)
All DataField's have a null validator attached by default. It can be enabled or disabled in DataField_Schema 
constructor. By default, DataField cannot have null values.

```
// Default, null value not allowed
DataField_Schema(NAME, TYPE)

// Directly set, can be null
DataField_Schema(NAME, TYPE, true)
```
##### NumberRange_FieldValidator
Number_FieldValidator can be used for any Type extending Number and implementing Comparable. The Min and Max values are
inclusive (Min <= value <= Max). If no min or max are provided then there is no limit in that direction.

```
// 1 or above
dataObjectSchema.<TYPE>get(KEY).addValidator(new NumberRange_FieldValidator<>(1, null));
// 1 or below
dataObjectSchema.<TYPE>get(KEY).addValidator(new NumberRange_FieldValidator<>(null, 1));
// between 1 and 10
dataObjectSchema.<TYPE>get(KEY).addValidator(new NumberRange_FieldValidator<>(1, 10));
```
##### Share_FieldValidator
Share_FieldValidator is a special kind of FieldValidator used when the range of a field is depended on another field. It 
is attached for both fields and get excused when either one changes. If it is only attached to 1 field a 
DatabaseStructureException will be thrown when the database structure is being setup.
```
Shared_FieldValidator<FIRST_TYPE, SECOND_TYPE, CONTAINER_TYPE> sharedFilter = 
    new Shared_FieldValidator<>(FIRST_KEY, SECOND_KEY,
    (firstNewValue, firstPastValue, secondNewValue, secondPastValue, container) -> {
        // check
    }, 
    "Failure description");

dataObjectSchema.<FIRST_TYPE>get(FIRST_KEY).addValidator(sharedFilter.getFirstFilter());
dataObjectSchema.<SECOND_TYPE>get(SECOND_KEY).addValidator(sharedFilter.getSecondFilter());
```

As with the normal FieldValidator you can create your own as needed or use an existing one like the 
NonEqual_Shared_FieldValidator

```
NonEqual_Shared_FieldValidator<TYPE> sharedFilter = new NonEqual_Shared_FieldValidator<>(FIRST_KEY, SECOND_KEY);

dataObjectSchema.<TYPE>get(FIRST_KEY).addValidator(sharedFilter.getFirstFilter());
dataObjectSchema.<TYPE>get(SECOND_KEY).addValidator(sharedFilter.getSecondFilter());
```

