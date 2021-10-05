package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class LocalVariableTargetEntry
{
	public final int startPc; //TODO: this needs to be replaced with an instruction.
	public final int length;
	public final int index;

	public LocalVariableTargetEntry( int startPc, int length, int index )
	{
		this.startPc = startPc;
		this.length = length;
		this.index = index;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "startPc = " + startPc + ", length = " + length + ", index = " + index; }
}
