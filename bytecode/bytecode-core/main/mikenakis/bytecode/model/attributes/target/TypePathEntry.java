package mikenakis.bytecode.model.attributes.target;

public final class TypePathEntry
{
	public final int pathKind;
	public final int argumentIndex;

	public TypePathEntry( int pathKind, int argumentIndex )
	{
		this.pathKind = pathKind;
		this.argumentIndex = argumentIndex;
	}

	@Override public String toString() { return "pathKind = " + pathKind + ", argumentIndex = " + argumentIndex; }
}
