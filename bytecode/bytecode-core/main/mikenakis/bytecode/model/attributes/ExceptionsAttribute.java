package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "Exceptions" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionsAttribute extends KnownAttribute
{
	public static ExceptionsAttribute read( AttributeReader attributeReader )
	{
		int count = attributeReader.readUnsignedShort();
		assert count > 0;
		List<ClassConstant> exceptionClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant constant = attributeReader.readIndexAndGetConstant().asClassConstant();
			exceptionClassConstants.add( constant );
		}
		return of( exceptionClassConstants );
	}

	public static ExceptionsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static ExceptionsAttribute of( List<ClassConstant> exceptionClassConstants )
	{
		return new ExceptionsAttribute( exceptionClassConstants );
	}

	private final List<ClassConstant> exceptionClassConstants;

	private ExceptionsAttribute( List<ClassConstant> exceptionClassConstants )
	{
		super( tag_Exceptions );
		this.exceptionClassConstants = exceptionClassConstants;
	}

	public List<TerminalTypeDescriptor> exceptions() { return exceptionClassConstants.stream().map( c -> c.terminalTypeDescriptor() ).toList(); }
	@Deprecated @Override public ExceptionsAttribute asExceptionsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return exceptionClassConstants.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( ClassConstant exceptionClassConstant : exceptionClassConstants )
			exceptionClassConstant.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( exceptionClassConstants.size() );
		for( ClassConstant exceptionClassConstant : exceptionClassConstants )
			constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( exceptionClassConstant ) );
	}
}
