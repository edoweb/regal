package de.nrw.hbz.regal.mab;

public class MabException extends RuntimeException {

    public MabException(String arg0, Throwable arg1) {
	super(arg0, arg1);
    }

    public MabException(Throwable arg0) {
	super(arg0);
    }

}
