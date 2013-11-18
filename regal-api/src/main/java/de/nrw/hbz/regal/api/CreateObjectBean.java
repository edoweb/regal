package de.nrw.hbz.regal.api;

import javax.xml.bind.annotation.XmlRootElement;

import de.nrw.hbz.regal.api.helper.ObjectType;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
@XmlRootElement
public class CreateObjectBean {
    String type = null;
    String parentPid = null;
    String[] transformer = null;

    /**
     * Default constructor
     * 
     */
    public CreateObjectBean() {

    }

    /**
     * @return all Transformer-Ids
     */
    public String[] getTransformer() {
	return transformer;
    }

    /**
     * @param transformer
     *            list of Transformer-Ids
     */
    public void setTransformer(String[] transformer) {
	this.transformer = transformer;
    }

    /**
     * @param t
     *            a valid type
     */
    public CreateObjectBean(ObjectType t) {
	type = t.toString();
    }

    /**
     * @return the type of the object
     */
    public String getType() {
	return type;
    }

    /**
     * @param type
     *            the type
     */
    public void setType(String type) {
	this.type = type;
    }

    /**
     * @return the parent
     */
    public String getParentPid() {
	return parentPid;
    }

    /**
     * @param parentPid
     *            the parent
     */
    public void setParentPid(String parentPid) {
	this.parentPid = parentPid;
    }

}
