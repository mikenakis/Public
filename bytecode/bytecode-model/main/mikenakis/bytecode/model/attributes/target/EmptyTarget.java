package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class EmptyTarget extends Target // "empty_target" in jvms-4.7.20.1
{
	public EmptyTarget()
	{
	}

	@Deprecated @Override public Optional<EmptyTarget> tryAsEmptyTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "(empty)";
	}
}
