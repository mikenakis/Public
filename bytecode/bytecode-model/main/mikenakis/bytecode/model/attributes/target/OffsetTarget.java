package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class OffsetTarget extends Target
{
	public final int offset;

	public OffsetTarget( int offset )
	{
		this.offset = offset;
	}

	@Deprecated @Override public Optional<OffsetTarget> tryAsOffsetTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "offset = " + offset;
	}
}
