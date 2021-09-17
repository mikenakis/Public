package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collections;
import java.util.List;

public final class TypePath // "type_path" in jvms-4.7.20.2
{
	public static final class Entry
	{
		private final int pathKind;
		private final int argumentIndex;

		public Entry( int pathKind, int argumentIndex )
		{
			this.pathKind = pathKind;
			this.argumentIndex = argumentIndex;
		}

		public int pathKind() { return pathKind; }
		public int argumentIndex() { return argumentIndex; }

		@Override public String toString()
		{
			return "pathKind = " + pathKind + ", argumentIndex = " + argumentIndex;
		}
	}

	private final List<Entry> entries;

	public TypePath( List<Entry> entries )
	{
		this.entries = entries;
	}

	public List<Entry> entries()
	{
		return Collections.unmodifiableList( entries );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return entries.size() + " entries";
	}
}
