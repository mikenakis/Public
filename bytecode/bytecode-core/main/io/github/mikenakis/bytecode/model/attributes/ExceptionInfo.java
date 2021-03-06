package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.constants.ClassConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the "exception table" of the "code" attribute of a java class file.
 *
 * @author michael.gr
 */
public final class ExceptionInfo
{
	public static ExceptionInfo read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap  )
	{
		Instruction startInstruction = locationMap.getInstruction( bufferReader.readUnsignedShort() ).orElseThrow();
		Optional<Instruction> endInstruction = locationMap.getInstruction( bufferReader.readUnsignedShort() );
		assert endInstruction.isPresent();
		Instruction handlerInstruction = locationMap.getInstruction( bufferReader.readUnsignedShort() ).orElseThrow();
		Optional<ClassConstant> catchTypeConstant = Kit.upCast( constantPool.tryGetConstant( bufferReader.readUnsignedShort() ) );
		return of( startInstruction, endInstruction, handlerInstruction, catchTypeConstant );
	}

	public static ExceptionInfo of( Instruction startInstruction, Optional<Instruction> endInstruction, //
		Instruction handlerInstruction, Optional<ClassConstant> catchTypeConstant )
	{
		return new ExceptionInfo( startInstruction, endInstruction, handlerInstruction, catchTypeConstant );
	}

	public final Instruction startInstruction;
	public final Optional<Instruction> endInstruction;
	public final Instruction handlerInstruction;
	private final Optional<ClassConstant> catchTypeConstant;

	private ExceptionInfo( Instruction startInstruction, Optional<Instruction> endInstruction, //
		Instruction handlerInstruction, Optional<ClassConstant> catchTypeConstant )
	{
		this.startInstruction = startInstruction;
		this.endInstruction = endInstruction;
		this.handlerInstruction = handlerInstruction;
		this.catchTypeConstant = catchTypeConstant;
	}

	public Optional<TerminalTypeDescriptor> catchType() { return catchTypeConstant.map( c -> c.terminalTypeDescriptor() ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "start = {" + startInstruction + "}, end = {" + endInstruction + "}, handler = {" + handlerInstruction + "}" + (catchTypeConstant.isPresent()? ", catchType = " + catchTypeConstant : ""); }

	public void intern( Interner interner )
	{
		catchTypeConstant.ifPresent( c -> c.intern( interner ) );
	}

	public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap )
	{
		bufferWriter.writeUnsignedShort( locationMap.getLocation( startInstruction ) );
		bufferWriter.writeUnsignedShort( locationMap.getLocation( endInstruction ) );
		bufferWriter.writeUnsignedShort( locationMap.getLocation( handlerInstruction ) );
		bufferWriter.writeUnsignedShort( catchTypeConstant.map( c -> constantPool.getConstantIndex( c ) ).orElse( 0 ) );
	}
}
