package de.nrw.hbz.regal.sync.extern;

public class RelatedDigitalEntity
{
	public DigitalEntity entity;
	public String relation;

	public RelatedDigitalEntity(DigitalEntity entity, String relation)
	{
		this.entity = entity;
		this.relation = relation;
	}
}
