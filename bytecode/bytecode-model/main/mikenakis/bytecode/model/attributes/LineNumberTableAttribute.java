package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
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
public final class LineNumberTableAttribute extends Attribute
{
	public static LineNumberTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LineNumberTableAttribute of( List<LineNumber> entries )
	{
		return new LineNumberTableAttribute( entries );
	}

	public static final String name = "LineNumberTable";
	public static final Kind kind = new Kind( name );

	private final List<LineNumber> lineNumbers;

	private LineNumberTableAttribute( List<LineNumber> lineNumbers )
	{
		super( kind );
		this.lineNumbers = lineNumbers;
	}

	public List<LineNumber> lineNumbers()
	{
		return Collections.unmodifiableList( lineNumbers );
	}

	@Deprecated @Override public LineNumberTableAttribute asLineNumberTableAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return lineNumbers.size() + " entries";
	}
}
