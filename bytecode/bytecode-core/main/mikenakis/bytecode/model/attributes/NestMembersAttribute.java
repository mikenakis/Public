package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "NestMembers" {@link Attribute} of a java class file.
 * <p>
 * See JVMS 4.7.29 "The NestMembers Attribute", https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7.29
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author michael.gr
 */
public final class NestMembersAttribute extends KnownAttribute
{
	public static NestMembersAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<ClassConstant> memberClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
			memberClassConstants.add( constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant() );
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

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( memberClassConstants.size() );
		for( ClassConstant memberClassConstant : memberClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( memberClassConstant ) );
	}
}
