package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

public final class TypePath // "type_path" in jvms-4.7.20.2
{
	public final List<TypePathEntry> entries;

	public TypePath( List<TypePathEntry> entries )
	{
		this.entries = entries;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return entries.size() + " entries"; }
}
