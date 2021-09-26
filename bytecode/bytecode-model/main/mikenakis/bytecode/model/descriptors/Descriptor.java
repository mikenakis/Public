package mikenakis.bytecode.model.descriptors;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public abstract class Descriptor
{
	public abstract String name();

	@ExcludeFromJacocoGeneratedReport @Override public final String toString()
	{
		return name();
	}
}
