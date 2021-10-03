package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class EmptyTarget extends Target // "empty_target" in jvms-4.7.20.1
{
	public EmptyTarget( int tag )
	{
		super( tag );
		assert tag == tag_FieldType || //
			tag == tag_ReturnType || //
			tag == tag_ReceiverType;
	}

	@Deprecated @Override public EmptyTarget asEmptyTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "(empty)"; }
}
