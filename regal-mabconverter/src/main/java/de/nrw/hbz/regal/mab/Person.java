package de.nrw.hbz.regal.mab;

public class Person {
    public enum PersonRole {

    }

    public enum PersonType {
	creator, corporateBody
    }

    public Person(String id) {
	this.id = id;
    }

    public String id;
    public String name;
    public String dateOfBirth;
    public PersonType type;
    public PersonRole role;
}
