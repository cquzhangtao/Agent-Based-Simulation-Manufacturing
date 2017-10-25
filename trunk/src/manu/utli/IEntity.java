package manu.utli;

public interface IEntity {

	public abstract String getUUID();

	public abstract String getName();

	public abstract String getDescription();

	public abstract void setGUId(String id);

	public abstract void setName(String name);

	public abstract void setDescription(String description);

	public abstract String getId();

	public abstract void setId(String id);
	public abstract Object clone();
	public abstract String toString();




}