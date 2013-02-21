package eu.execom.testutil;

/**
 * Intermediate object that stores entity and info is the entity asserted or not.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 * @param <T>
 *            entity type.
 */
// TODO make this class protected
public class CopyAssert<T> {

    private T entity;
    private boolean asserted;

    /**
     * Copy assert default constructor.
     * 
     * @param entity
     *            is content of copy assert.
     */
    public CopyAssert(final T entity) {
        this.entity = entity;
        this.asserted = false;
    }

    /**
     * Get entity.
     * 
     * @return entity.
     */
    public T getEntity() {
        return entity;
    }

    /**
     * Set entity.
     * 
     * @param entity
     *            entity to be set
     */
    public void setEntity(final T entity) {
        this.entity = entity;
    }

    /**
     * Get is entity asserted.
     * 
     * @return <code>true</code> if is else return <code>false</code> .
     */
    public boolean isAsserted() {
        return asserted;
    }

    /**
     * Set is entity asserted.
     * 
     * @param asserted
     *            is entity asserted.
     */
    public void setAsserted(final boolean asserted) {
        this.asserted = asserted;
    }

}
