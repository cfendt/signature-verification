/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.bean;

import java.util.LinkedList;

public final class SignatureData {
    LinkedList<Double> x;
    LinkedList<Double> y;
    int num;
    int penUp;

    public SignatureData() {
        super();
        this.x = new LinkedList<Double>();
        this.y = new LinkedList<Double>();
    }

    public SignatureData(final LinkedList<Double> xArg, final LinkedList<Double> yArg, final int penUp) {
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

    public LinkedList<Double> getX() {
        return this.x;
    }

    public void setX(final LinkedList<Double> x) {
        this.x = x;
    }

    public LinkedList<Double> getY() {
        return this.y;
    }

    public void setY(final LinkedList<Double> y) {
        this.y = y;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(final int num) {
        this.num = num;
    }

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
