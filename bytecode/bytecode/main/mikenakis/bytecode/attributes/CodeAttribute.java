package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.InstructionModels;
import mikenakis.bytecode.attributes.code.InstructionReference;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents the "Code" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class CodeAttribute extends Attribute
{
	public static final String NAME = "Code";

	public static Optional<CodeAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asCodeAttribute() );
	}

	public final ByteCodeMethod method;
	public int maxStack;
	public final int maxLocals;
	public final List<ExceptionInfo> exceptionInfos;
	public final Attributes attributes;
	public final ArrayList<Instruction> instructions = new ArrayList<>();
	private int endPc;

	public CodeAttribute( Runnable observer, ByteCodeMethod method )
	{
		super( observer, NAME );
		this.method = method;
		maxStack = 0;
		maxLocals = 0;
		exceptionInfos = new ArrayList<>();
		attributes = new Attributes( this::markAsDirty );
		endPc = 0;
	}

	public CodeAttribute( Runnable observer, ByteCodeMethod method, BufferReader bufferReader )
	{
		super( observer, NAME );
		this.method = method;
		maxStack = bufferReader.readUnsignedShort();
		maxLocals = bufferReader.readUnsignedShort();

		int codeLength = bufferReader.readInt();
		endPc = codeLength;
		Buffer codeBuffer = bufferReader.readBuffer( codeLength );
		readInstructions( codeBuffer );

		int count = bufferReader.readUnsignedShort();
		exceptionInfos = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ExceptionInfo exceptionInfo = new ExceptionInfo( this, bufferReader );
			exceptionInfos.add( exceptionInfo );
		}
		attributes = new Attributes( this::markAsDirty, method.declaringType.constantPool, this::newAttribute, bufferReader );
	}

	private void readInstructions( Buffer codeBuffer )
	{
		BufferReader codeBufferReader = new BufferReader( codeBuffer );
		Collection<Runnable> fixUps = new ArrayList<>();
		while( !codeBufferReader.isAtEnd() )
		{
			int pc = codeBufferReader.getPosition();
			boolean wide = false;
			int opCode = codeBufferReader.readUnsignedByte();
			if( opCode == OpCode.WIDE )
			{
				wide = true;
				opCode = codeBufferReader.readUnsignedByte();
			}
			InstructionModel instructionModel = InstructionModels.getInstructionModel( opCode );
			Instruction instruction = instructionModel.parseInstruction( this, pc, wide, codeBufferReader, fixUps );
			instruction.setLength( codeBufferReader.getPosition() - instruction.getPc() );
			instructions.add( instruction );
		}
		for( Runnable fixUp : fixUps )
			fixUp.run();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( ExceptionInfo exceptionInfo : exceptionInfos )
			exceptionInfo.intern( constantPool );
		attributes.intern( constantPool );

		for( Instruction instruction : instructions )
			instruction.intern( constantPool );

		BufferWriter bufferWriter = new BufferWriter();
		for( Instruction instruction : instructions )
		{
			instruction.setPc( bufferWriter.getPosition() );
			instruction.write( constantPool, bufferWriter );
			instruction.setLength( bufferWriter.getPosition() - instruction.getPc() );
		}
		endPc = bufferWriter.getPosition();

		for( Instruction instruction : instructions )
			instruction.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( maxStack );
		bufferWriter.writeUnsignedShort( maxLocals );

		BufferWriter codeBufferWriter = new BufferWriter();
		for( Instruction instruction : instructions )
			instruction.write( constantPool, codeBufferWriter );
		Buffer codeBuffer = codeBufferWriter.toBuffer();

		bufferWriter.writeInt( codeBuffer.getLength() );
		bufferWriter.writeBuffer( codeBuffer );

		bufferWriter.writeUnsignedShort( exceptionInfos.size() );
		for( ExceptionInfo exceptionInfo : exceptionInfos )
			exceptionInfo.write( constantPool, bufferWriter );
		attributes.write( constantPool, bufferWriter );
	}

	@Override public Optional<CodeAttribute> tryAsCodeAttribute()
	{
		return Optional.of( this );
	}

	private Attribute newAttribute( String attributeName, BufferReader bufferReader )
	{
		switch( attributeName )
		{
			//@formatter:off
			case LineNumberTableAttribute.NAME:        return new LineNumberTableAttribute       ( this::markAsDirty, this, bufferReader );
			case LocalVariableTableAttribute.NAME:     return new LocalVariableTableAttribute    ( this::markAsDirty, this, bufferReader );
			case LocalVariableTypeTableAttribute.NAME: return new LocalVariableTypeTableAttribute( this::markAsDirty, this, bufferReader );
			case StackMapTableAttribute.NAME:          return new StackMapTableAttribute         ( this::markAsDirty, this, bufferReader );
			case "RuntimeInvisibleTypeAnnotations": //see https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.19 TODO
			case "RuntimeVisibleTypeAnnotations": //see https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.20 TODO
			default:                                   return new UnknownAttribute               ( this::markAsDirty, attributeName, bufferReader );
			//@formatter:on
		}
	}

	public int getInstructionIndex( int pc )
	{
		assert pc >= 0;
		return Collections.binarySearch( instructions, null, ( o1, o2 ) ->
		{
			assert o1 != null;
			assert o2 == null;
			//noinspection SubtractionInCompareTo
			return o1.getPc() - pc; //these are always small non-negative values, so it is okay to just subtract instead of invoking Integer.compare()
		} );
	}

	public Instruction getInstructionByPc( int pc )
	{
		int instructionIndex = getInstructionIndex( pc );
		if( instructionIndex == -instructions.size() - 1 )
			return InstructionReference.END_INSTRUCTION;
		return instructions.get( instructionIndex );
	}

	public int size()
	{
		return instructions.size();
	}

	public int getEndPc()
	{
		return endPc;
	}

	public Optional<StackMapTableAttribute> tryGetStackMapTableAttribute()
	{
		return StackMapTableAttribute.tryFrom( attributes );
	}

	public StackMapTableAttribute getOrCreateStackMapTableAttribute()
	{
		Optional<StackMapTableAttribute> existingAttribute = tryGetStackMapTableAttribute();
		if( existingAttribute.isPresent() )
			return existingAttribute.get();
		StackMapTableAttribute stackMapTableAttribute = new StackMapTableAttribute( this::markAsDirty, this );
		attributes.addAttribute( stackMapTableAttribute );
		return stackMapTableAttribute;
	}

	public Optional<LineNumberTableAttribute> tryGetLineNumberTableAttribute()
	{
		return LineNumberTableAttribute.tryFrom( attributes );
	}

	public Optional<LocalVariableTableAttribute> tryGetLocalVariableTableAttribute()
	{
		return LocalVariableTableAttribute.tryFrom( attributes );
	}

	public Optional<LocalVariableTypeTableAttribute> tryGetLocalVariableTypeTableAttribute()
	{
		return LocalVariableTypeTableAttribute.tryFrom( attributes );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		for( Instruction instruction : instructions )
			instruction.collectTargets( targetInstructionConsumer );
		for( ExceptionInfo exceptionInfo : exceptionInfos )
			exceptionInfo.collectTargets( targetInstructionConsumer );
		for( Attribute attribute : attributes )
			attribute.collectTargets( targetInstructionConsumer );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "maxStack = " ).append( maxStack );
		builder.append( ", maxLocals = " ).append( maxLocals );
		builder.append( ", " ).append( instructions.size() ).append( " instructions" );
		builder.append( ", " ).append( exceptionInfos.size() ).append( " exceptionInfos" );
		builder.append( ", " ).append( attributes.size() ).append( " attributes" );
	}
}
