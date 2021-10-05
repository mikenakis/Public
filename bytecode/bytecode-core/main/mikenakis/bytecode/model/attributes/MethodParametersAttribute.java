package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "MethodParameters" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodParametersAttribute extends KnownAttribute
{
	public static MethodParametersAttribute read( AttributeReader attributeReader )
	{
		int count = attributeReader.readUnsignedByte();
		assert count > 0;
		List<MethodParameter> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8ValueConstant nameConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
			FlagSet<MethodParameter.Modifier> modifiers = MethodParameter.modifierEnum.fromBits( attributeReader.readUnsignedShort() );
			MethodParameter entry = MethodParameter.of( nameConstant, modifiers );
			entries.add( entry );
		}
		return of( entries );
	}

	public static MethodParametersAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static MethodParametersAttribute of( List<MethodParameter> methodParameters )
	{
		return new MethodParametersAttribute( methodParameters );
	}

	public final List<MethodParameter> methodParameters;

	private MethodParametersAttribute( List<MethodParameter> methodParameters )
	{
		super( tag_MethodParameters );
		this.methodParameters = methodParameters;
	}

	@Deprecated @Override public MethodParametersAttribute asMethodParametersAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return methodParameters.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( MethodParameter methodParameter : methodParameters )
			methodParameter.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( methodParameters.size() );
		for( MethodParameter methodParameter : methodParameters )
			methodParameter.write( constantWriter );
	}
}
