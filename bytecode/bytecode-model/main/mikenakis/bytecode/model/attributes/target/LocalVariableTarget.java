package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

public final class LocalVariableTarget extends Target // "localvar_target" in jvms-4.7.20.1
{
	public static final class Entry
	{
		private final int startPc;
		private final int length;
		private final int index;

		public Entry( int startPc, int length, int index )
		{
			this.startPc = startPc;
			this.length = length;
			this.index = index;
		}

		public int startPc() { return startPc; }
		public int length() { return length; }
		public int index() { return index; }

		@Override public String toString()
		{
			return "startPc = " + startPc + ", length = " + length + ", index = " + index;
		}
	}

	public final List<Entry> entries;

	public LocalVariableTarget( Type type, List<Entry> entries )
	{
		super( type );
		assert type == Type.TypeInLocalVariableDeclaration || type == Type.TypeInResourceVariableDeclaration;
		this.entries = entries;
	}

	@Deprecated @Override public LocalVariableTarget asLocalVariableTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return entries.size() + " entries";
	}
}
