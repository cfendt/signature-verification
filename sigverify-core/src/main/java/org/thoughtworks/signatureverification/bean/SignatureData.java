/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.bean;

import java.util.LinkedList;
import java.util.List;

/**
 */
public final class SignatureData {
    private List<Double> x;
    private List<Double> y;
    private int num;
    private int penUp;

    /**
     * Constructor
     */
    public SignatureData() {
        super();
        this.x = new LinkedList<Double>();
        this.y = new LinkedList<Double>();
    }

    /**
     * Constructor
     * 
     * @param xArg Points X coordinate list.
     * @param yArg Points Y coordinate list.
     * @param penUp "pen up" event count
     */
    public SignatureData(final List<Double> xArg, final List<Double> yArg, final int penUp) {
        super();
        this.x = xArg;
        this.y = yArg;
        this.num = xArg.size();
        this.penUp = penUp;
    }

    public int getPenUp() {
        return this.penUp;
    }

    public void setPenUp(final int penUp) {
        this.penUp = penUp;
    }

    public List<Double> getX() {
        return this.x;
    }

    public void setX(final List<Double> x) {
        this.x = x;
    }

    public List<Double> getY() {
        return this.y;
    }

    public void setY(final List<Double> y) {
        this.y = y;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(final int num) {
        this.num = num;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SignatureData that = (SignatureData) o;
        if (this.num != that.num) {
            return false;
        }
        if (this.penUp != that.penUp) {
            return false;
        }
        if (this.x != null ? !this.x.equals(that.x) : that.x != null) {
            return false;
        }
        if (this.y != null ? !this.y.equals(that.y) : that.y != null) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result;
        result = this.x != null ? this.x.hashCode() : 0;
        result = 31 * result + (this.y != null ? this.y.hashCode() : 0);
        result = 31 * result + this.num;
        result = 31 * result + this.penUp;
        return result;
    }
}
