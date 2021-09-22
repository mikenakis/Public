package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "LocalVariableTypeTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTypeTableAttribute extends KnownAttribute
{
	public static LocalVariableTypeTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LocalVariableTypeTableAttribute of( List<LocalVariableTypeTableEntry> localVariableTypes )
	{
		return new LocalVariableTypeTableAttribute( localVariableTypes );
	}

	public final List<LocalVariableTypeTableEntry> localVariableTypes;

	private LocalVariableTypeTableAttribute( List<LocalVariableTypeTableEntry> localVariableTypes )
	{
		super( tagLocalVariableTypeTable );
		this.localVariableTypes = localVariableTypes;
	}

	@Deprecated @Override public LocalVariableTypeTableAttribute asLocalVariableTypeTableAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return localVariableTypes.size() + " entries";
	}
}
