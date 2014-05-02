package de.nrw.hbz.regal.api;

import java.util.List;
import java.util.Vector;

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
    List<String> transformer = null;
    List<String> indexes = null;

    /**
     * Default constructor
     * 
     */
    public CreateObjectBean() {
	transformer = new Vector<String>();
	indexes = new Vector<String>();
    }

    /**
     * @return all Transformer-Ids
     */
    public List<String> getTransformer() {
	return transformer;
    }

    /**
     * @param t
     *            list of Transformer-Ids
     */
    public void setTransformer(List<String> t) {
	transformer = t;
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

    /**
     * @return a list of indexes, that are updated on create/modify
     */
    public List<String> getIndexes() {
	return indexes;
    }

    /**
     * @param indexes
     *            a list of indexes, that are updated on create/modify, valid
     *            values so far: null, public, private
     */
    public void setIndexes(List<String> indexes) {
	this.indexes = indexes;
    }

}
