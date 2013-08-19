package de.nrw.hbz.regal.fedora;

@SuppressWarnings({ "javadoc", "serial" })
public class RdfException extends RuntimeException {

    public RdfException(Throwable e) {
	super(e);
    }

    public RdfException(String message, Throwable e) {
	super(message, e);
    }
}
