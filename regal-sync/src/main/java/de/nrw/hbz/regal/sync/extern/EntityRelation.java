package de.nrw.hbz.regal.sync.extern;

public class EntityRelation
{
	public DigitalEntity entity;
	public String relation;

	public EntityRelation(DigitalEntity entity, String relation)
	{
		this.entity = entity;
		this.relation = relation;
	}
}
