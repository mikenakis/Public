package mikenakis.bytecode.attributes;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.code.AbsoluteInstructionReference;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;
import mikenakis.kit.Kit;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents an entry of the "exception table" of the "code" attribute of a java class file.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionInfo extends Printable
{
	public final AbsoluteInstructionReference startPc;
	public final AbsoluteInstructionReference endPc;
	public final AbsoluteInstructionReference handlerPc;
	public final Optional<ClassConstant> catchTypeConstant;

	public ExceptionInfo( ConstantPool constantPool, AbsoluteInstructionReference startPc, AbsoluteInstructionReference endPc, AbsoluteInstructionReference handlerPc, Optional<ClassConstant> catchTypeConstant )
	{
		this.startPc = startPc;
		this.endPc = endPc;
		this.handlerPc = handlerPc;
		this.catchTypeConstant = catchTypeConstant;
	}

	public ExceptionInfo( CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		startPc = new AbsoluteInstructionReference( codeAttribute, false, bufferReader );
		endPc = new AbsoluteInstructionReference( codeAttribute, false, bufferReader );
		handlerPc = new AbsoluteInstructionReference( codeAttribute, false, bufferReader );
		catchTypeConstant = Kit.upCast( codeAttribute.constantPool.tryReadIndexAndGetConstant( bufferReader ) );
	}

	public void intern( ConstantPool constantPool )
	{
		catchTypeConstant.ifPresent( classConstant -> classConstant.intern( constantPool ) );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		startPc.write( false, bufferWriter );
		endPc.write( false, bufferWriter );
		handlerPc.write( false, bufferWriter );
		if( catchTypeConstant.isEmpty() )
			bufferWriter.writeUnsignedShort( 0 );
		else
			catchTypeConstant.get().writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "startPc = " ).append( startPc.getPc() );
		builder.append( ", length = " ).append( endPc.getPc() - startPc.getPc() );
		builder.append( " handlerPc = " ).append( handlerPc.getPc() );
		catchTypeConstant.ifPresent( classConstant -> builder.append( ", catchType = " ).append( classConstant.getClassName() ) );
	}

	public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( startPc.getTargetInstruction() );
		targetInstructionConsumer.accept( endPc.getTargetInstruction() );
		targetInstructionConsumer.accept( handlerPc.getTargetInstruction() );
	}
}
