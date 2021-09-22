package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

public final class LocalVariableTarget extends Target // "localvar_target" in jvms-4.7.20.1
{
	public final List<LocalVariableTargetEntry> entries;

	public LocalVariableTarget( int tag, List<LocalVariableTargetEntry> entries )
	{
		super( tag );
		assert tag == tagTypeInLocalVariableDeclaration || tag == tagTypeInResourceVariableDeclaration;
		this.entries = entries;
	}

	@Deprecated @Override public LocalVariableTarget asLocalVariableTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return entries.size() + " entries";
	}
}
