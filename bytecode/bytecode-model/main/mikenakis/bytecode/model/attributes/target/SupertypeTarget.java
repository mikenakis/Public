package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class SupertypeTarget extends Target // "supertype_target" in jvms-4.7.20.1
{
	public final int supertypeIndex;

	public SupertypeTarget( int tag, int supertypeIndex )
	{
		super( tag );
		assert tag == tag_Supertype;
		this.supertypeIndex = supertypeIndex;
	}

	@Deprecated @Override public SupertypeTarget asSupertypeTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "superTypeIndex = " + supertypeIndex; }
}
