package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "LineNumberTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LineNumberTableAttribute extends KnownAttribute
{
	public static LineNumberTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LineNumberTableAttribute of( List<LineNumberTableEntry> entrys )
	{
		return new LineNumberTableAttribute( entrys );
	}

	public final List<LineNumberTableEntry> entrys;

	private LineNumberTableAttribute( List<LineNumberTableEntry> entrys )
	{
		super( tag_LineNumberTable );
		this.entrys = entrys;
	}

	@Deprecated @Override public LineNumberTableAttribute asLineNumberTableAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return entrys.size() + " entries";
	}
}
