package eu.execom.fabut.report;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import eu.execom.fabut.enums.CommentType;
import eu.execom.fabut.pair.AssertPair;
import eu.execom.fabut.property.NotNullProperty;
import eu.execom.fabut.property.NullProperty;

/**
 * Report builder used for creating clean and readable reports. Its point is to emphasize failed asserts so developer
 * can track them easy.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */

public class FabutReportBuilder {

    private static final String ARROW = ">";
    private static final String NEW_LINE = "\n";
    private static final String TAB = "    ";

    private final StringBuilder builder;
    private final List<String> messageParts;

    private Integer assertDepth;
    private int failedMessagePosition;

    /**
     * Default constructor.
     */
    public FabutReportBuilder() {
        builder = new StringBuilder();
        assertDepth = 0;
        messageParts = new ArrayList<String>();
        failedMessagePosition = 0;
    }

    /**
     * Default constructor.
     * 
     * @param message
     *            initial message
     */
    public FabutReportBuilder(final String message) {
        this();
        messageParts.add(message.length() > 0 ? "\n" + message : message);
        failedMessagePosition = 1;

    }

    /**
     * Get string text.
     * 
     * @return string
     */
    public String getMessage() {

        for (final String part : messageParts) {
            builder.append(part);
        }
        return builder.toString();
    }

    /**
     * Increase indentation in report.
     */
    public void increaseDepth() {

        assertDepth++;
    }

    /**
     * Decrease indentation in report.
     */
    public void decreaseDepth() {

        assertDepth--;
    }

    /**
     * Add new comment to specified depth.
     * 
     * @param comment
     *            to be added
     * @param type
     *            type of comment
     */
    private void addComment(final String comment, final CommentType type) {

        final StringBuilder part = new StringBuilder(builder.toString());
        builder.setLength(0);
        part.append(NEW_LINE);

        for (int i = 0; i < assertDepth; i++) {
            part.append(TAB);
        }

        part.append(type.getMark());
        part.append(ARROW);

        part.append(comment);
        if (type == CommentType.FAIL) {
            messageParts.add(failedMessagePosition, part.toString());
        } else {
            messageParts.add(part.toString());
        }

    }

    /**
     * Reports fail due to different list sizes.
     * <p>
     * Example: <i>■> Expected size for list: users is: 2, but was: 1</i>
     * </p>
     * 
     * @param propertyName
     *            the property name
     * @param expectedSize
     *            the expected size
     * @param actualSize
     *            the actual size
     */
    public void listDifferentSizeComment(final String propertyName, final int expectedSize, final int actualSize) {
        final String comment = String.format("Expected size for list: %s is: %d, but was: %d", propertyName,
                expectedSize, actualSize);
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports fail due to field not having matching property.
     * 
     * <p>
     * Example: <i>There was no property for field: name of class: User, with value: John</i>
     * </p>
     * 
     * @param fieldName
     *            name of the field
     * @param field
     *            class of the field
     */
    public void noPropertyForField(final String fieldName, final Object field) {
        final String comment = String.format("There was no property for field:  %s of class:  %s, with value: %s",
                fieldName, field.getClass(), field);
        addComment(comment, CommentType.FAIL);

    }

    /**
     * Reports result of assertion with {@link NotNullProperty}.
     * <p>
     * Example(asserted): <i>∞> id: expected not null property and field was not null</i>
     * </p>
     * <p>
     * Example(assert fail): <i>■> id: expected not null property, but field was null</i>
     * </p>
     * 
     * @param fieldName
     *            - name of the field
     * @param assertResult
     *            - assert result
     */
    public void notNullProperty(final String fieldName, final boolean assertResult) {
        if (assertResult) {
            final String comment = String.format("%s: expected not null property and field was not null", fieldName);
            addComment(comment, CommentType.SUCCESS);
        } else {
            final String comment = String.format("%s: expected not null property, but field was null", fieldName);
            addComment(comment, CommentType.SUCCESS);
        }
    }

    /**
     * Reports result of of assertion with {@link NullProperty}.
     * <p>
     * Example(asserted): <i>∞> endDate: expected null and field was null</i>
     * </p>
     * <p>
     * Example(asserted): <i>∞> endDate: expected null, but field was not null</i>
     * </p>
     * 
     * 
     * @param fieldName
     *            - name of the field
     * @param assertResult
     *            - assert result
     */
    public void nullProperty(final String fieldName, final boolean assertResult) {
        if (assertResult) {
            final String comment = String.format("%s: expected null property and field was null", fieldName);
            addComment(comment, CommentType.SUCCESS);
        } else {
            final String comment = String.format("%s: expected null property, but field was not null", fieldName);
            addComment(comment, CommentType.FAIL);
        }
    }

    /**
     * Reports ignore property.
     * <p>
     * Example: <i>∞> name: is ignored field</i>
     * </p>
     * 
     * @param fieldName
     *            - name of the field
     */
    public void reportIgnoreProperty(final String fieldName) {
        final String comment = String.format("%s: is ignored field", fieldName);
        addComment(comment, CommentType.SUCCESS);
    }

    /**
     * Reports result of reference assert.
     * <p>
     * Example(asserted): <i>∞> Property: address of class: User has good reference.</i>
     * </p>
     * <p>
     * Example(assert fail): <i>■> Property: address of class: User has wrong reference.</i>
     * </p>
     * 
     * @param fieldName
     *            name of the field
     * @param fieldClass
     *            field class
     * @param asserted
     *            assert result
     */
    public void checkByReference(final String fieldName, final Object object, final boolean asserted) {

        if (asserted) {
            final String comment = String.format("Property:  %s of class:  %s has good reference.", fieldName, object
                    .getClass().getSimpleName());
            addComment(comment, CommentType.SUCCESS);
        } else {
            final String comment = String.format("Property:  %s of class:  %s has wrong reference.", fieldName, object
                    .getClass().getSimpleName());
            addComment(comment, CommentType.FAIL);
        }
    }

    /**
     * Reports ignore type.
     * <p>
     * Example: <i>∞> Type Date is ignored type.</i>
     * </p>
     * 
     * @param expected
     *            object
     * @param actual
     *            object
     * 
     * @param generic
     *            type
     */
    public void ignoredType(final AssertPair assertPair) {
        final String comment = String.format("Type  %s is ignored type.", assertPair.getExpected().getClass()
                .getSimpleName());
        addComment(comment, CommentType.SUCCESS);
    }

    /**
     * Reports asserting list elements by index.
     * <p>
     * Example: <i>#> Asserting object at index [1] of list shoppingCarts.</i>
     * </p>
     * 
     * @param listName
     *            - name of the list
     * @param index
     *            - index of the element in the list beeing asserted
     */
    public void assertingListElement(final String listName, final int index) {
        final String comment = String.format("Asserting object at index [%d] of list %s.", index, listName);
        addComment(comment, CommentType.LIST);
    }

    /**
     * Reports failure when there is no matching entity in current snapshot for entity in before snapshot.
     * <p>
     * Example: <i>■> Entity User [id=1] doesn't exist in DB any more but is not asserted in test.</i>
     * </p>
     * 
     * @param propertyName
     *            - property name
     */
    public void noEntityInSnapshot(final Object entity) {
        final String comment = String.format("Entity %s doesn't exist in DB any more but is not asserted in test.",
                entity);
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports failure when entity in current snapshot doesn't have matching entity in before snapshot and isn't
     * asserted in test.
     * <p>
     * Example: <i>■> Entity User [id=100] is created in system after last snapshot but hasnt been asserted in
     * test..</i>
     * </p>
     * 
     * @param entity
     *            the entity
     */
    public void entityNotAssertedInAfterState(final Object entity) {
        final String comment = String.format(
                "Entity %s is created in system after last snapshot but hasnt been asserted in test.", entity);
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports uncallable method.
     * <p>
     * Example: <i>■> There is no method: getName in actual object class: ShoppingCart (expected object class was:
     * User).</i>
     * </p>
     * 
     * 
     * @param methodName
     *            - name of the method
     */
    public void uncallableMethod(final Method method, final Object actual) {
        final String comment = String.format(
                "There is no method: %s in actual object class: %s (expected object class was: %s).", method.getName(),
                actual.getClass(), method.getDeclaringClass().getSimpleName());
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports fail due to passing null object reference for asserting.
     * <p>
     * Example: <i>■> Asserting cannot be null!</i>
     * </p>
     * 
     * @param actual
     *            object reference for asserting
     */
    public void nullReference() {
        final String comment = "Asserting object cannot null!";
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports asserted objects comment.
     * <p>
     * Example: <i>∞> id: expected: 11 and was: 11.</i>
     * </p>
     * 
     * @param pair
     *            the pair
     * @param propertyName
     *            the property name
     */
    public void asserted(final AssertPair pair, final String propertyName) {
        final String comment = String.format("%s: expected: %s and was: %s", propertyName, pair.getExpected(),
                pair.getActual());
        addComment(comment, CommentType.SUCCESS);
    }

    /**
     * Reports assert fail comment.
     * <p>
     * Example: <i>■> name: expected: John, but was: Mike.</i>
     * </p>
     * 
     * @param pair
     *            the pair
     * @param propertyName
     *            the property name
     */
    public void assertFail(final AssertPair pair, final String propertyName) {
        final String comment = String.format("%s: expected: %s, but was: %s", propertyName, pair.getExpected(),
                pair.getActual());
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports that entity of specified type cannot have null id.
     * <p>
     * Example: <i>■> Id of User cannot be null</i>
     * </p>
     * 
     * @param clazz
     *            the clazz
     */
    public void idNull(final Class<?> clazz) {
        final String comment = String.format("Id of %s cannot be null", clazz.getSimpleName());
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports that specified type of entity doesn't exist in snapshot.
     * <p>
     * Example: <i>■> Entity: User [id=100] cannot be found in snapshot</i>
     * </p>
     * 
     * @param entity
     *            the entity
     */
    public void notExistingInSnapshot(final Object entity) {
        final String comment = String.format("Entity: %s cannot be found in snapshot", entity);
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports that entity is not deleted in repository
     * <p>
     * Example: <i>■> Entity: User [id=100] was not deleted in repository</i>
     * </p>
     * 
     * @param entity
     *            the entity
     */
    public void notDeletedInRepositoy(final Object entity) {
        final String comment = String.format("Entity: %s was not deleted in repository", entity);
        addComment(comment, CommentType.FAIL);
    }

    /**
     * Reports that entity cannot be copied.
     * <p>
     * Example: <i>■> Entity: User [id=100] cannot be copied into snapshot</i>
     * </p>
     * 
     * @param entity
     *            the entity
     */
    public void noCopy(final Object entity) {
        final String comment = String.format("Entity: %s cannot be copied into snapshot", entity);
        addComment(comment, CommentType.FAIL);
    }

}
