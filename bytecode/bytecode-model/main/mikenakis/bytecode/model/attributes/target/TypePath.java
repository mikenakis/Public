package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collections;
import java.util.List;

public final class TypePath // "type_path" in jvms-4.7.20.2
{
	private final List<TypePathEntry> entrys;

	public TypePath( List<TypePathEntry> entrys )
	{
		this.entrys = entrys;
	}

	public List<TypePathEntry> entries()
	{
		return Collections.unmodifiableList( entrys );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return entrys.size() + " entries";
	}
}
