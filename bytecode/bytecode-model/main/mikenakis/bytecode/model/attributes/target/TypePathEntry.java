package mikenakis.bytecode.model.attributes.target;

public final class TypePathEntry
{
	private final int pathKind;
	private final int argumentIndex;

	public TypePathEntry( int pathKind, int argumentIndex )
	{
		this.pathKind = pathKind;
		this.argumentIndex = argumentIndex;
	}

	public int pathKind() { return pathKind; }
	public int argumentIndex() { return argumentIndex; }

	@Override public String toString()
	{
		return "pathKind = " + pathKind + ", argumentIndex = " + argumentIndex;
	}
}
