package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "NestMembers" {@link Attribute} of a java class file.
 * <p>
 * See JVMS 4.7.29 "The NestMembers Attribute", https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7.29
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NestMembersAttribute extends KnownAttribute
{
	public static NestMembersAttribute read( AttributeReader attributeReader )
	{
		int count = attributeReader.readUnsignedShort();
		assert count > 0;
		List<ClassConstant> memberClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant memberClassConstant = attributeReader.readIndexAndGetConstant().asClassConstant();
			memberClassConstants.add( memberClassConstant );
		}
		return of( memberClassConstants );
	}

	public static NestMembersAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static NestMembersAttribute of( List<ClassConstant> memberClassConstants )
	{
		return new NestMembersAttribute( memberClassConstants );
	}

	private final List<ClassConstant> memberClassConstants;

	private NestMembersAttribute( List<ClassConstant> memberClassConstants )
	{
		super( tag_NestMembers );
		this.memberClassConstants = memberClassConstants;
	}

	public List<TerminalTypeDescriptor> members() { return memberClassConstants.stream().map( c -> c.terminalTypeDescriptor() ).toList(); }
	@Deprecated @Override public NestMembersAttribute asNestMembersAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return memberClassConstants.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( ClassConstant memberClassConstant : memberClassConstants )
			memberClassConstant.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( memberClassConstants.size() );
		for( ClassConstant memberClassConstant : memberClassConstants )
			constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( memberClassConstant ) );
	}
}
