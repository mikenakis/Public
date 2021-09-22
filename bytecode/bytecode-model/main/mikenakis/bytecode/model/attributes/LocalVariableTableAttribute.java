package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "LocalVariableTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTableAttribute extends KnownAttribute
{
	public static LocalVariableTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LocalVariableTableAttribute of( List<LocalVariableTableEntry> entrys )
	{
		return new LocalVariableTableAttribute( entrys );
	}

	public final List<LocalVariableTableEntry> entrys;

	private LocalVariableTableAttribute( List<LocalVariableTableEntry> entrys )
	{
		super( tagLocalVariableTable );
		this.entrys = entrys;
	}

	@Deprecated @Override public LocalVariableTableAttribute asLocalVariableTableAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return entrys.size() + " entries";
	}
}
