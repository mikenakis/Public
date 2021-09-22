package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class LocalVariableTargetEntry
{
	private final int startPc;
	private final int length;
	private final int index;

	public LocalVariableTargetEntry( int startPc, int length, int index )
	{
		this.startPc = startPc;
		this.length = length;
		this.index = index;
	}

	public int startPc() { return startPc; }
	public int length() { return length; }
	public int index() { return index; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "startPc = " + startPc + ", length = " + length + ", index = " + index;
	}
}
