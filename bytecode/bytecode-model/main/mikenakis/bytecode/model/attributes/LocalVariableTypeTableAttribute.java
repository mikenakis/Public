package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
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
public final class LocalVariableTypeTableAttribute extends Attribute
{
	public static LocalVariableTypeTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LocalVariableTypeTableAttribute of( List<LocalVariableType> localVariableTypes )
	{
		return new LocalVariableTypeTableAttribute( localVariableTypes );
	}

	public static final String name = "LocalVariableTypeTable";
	public static final Kind kind = new Kind( name );

	private final List<LocalVariableType> localVariableTypes;

	private LocalVariableTypeTableAttribute( List<LocalVariableType> localVariableTypes )
	{
		super( kind );
		this.localVariableTypes = localVariableTypes;
	}

	public List<LocalVariableType> localVariableTypes()
	{
		return Collections.unmodifiableList( localVariableTypes );
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
