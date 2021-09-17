package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
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
public final class LocalVariableTableAttribute extends Attribute
{
	public static LocalVariableTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LocalVariableTableAttribute of( List<LocalVariable> localVariables )
	{
		return new LocalVariableTableAttribute( localVariables );
	}

	public static final String name = "LocalVariableTable";
	public static final Kind kind = new Kind( name );

	private final List<LocalVariable> localVariables;

	private LocalVariableTableAttribute( List<LocalVariable> localVariables )
	{
		super( kind );
		this.localVariables = localVariables;
	}

	public List<LocalVariable> localVariables()
	{
		return Collections.unmodifiableList( localVariables );
	}

	@Deprecated @Override public LocalVariableTableAttribute asLocalVariableTableAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return localVariables.size() + " entries";
	}
}
