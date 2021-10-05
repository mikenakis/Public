package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the "exception table" of the "code" attribute of a java class file.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionInfo
{
	public static ExceptionInfo read( CodeAttributeReader codeAttributeReader )
	{
		Instruction startInstruction = codeAttributeReader.readAbsoluteInstruction().orElseThrow();
		Optional<Instruction> endInstruction = codeAttributeReader.readAbsoluteInstruction();
		assert endInstruction.isPresent();
		Instruction handlerInstruction = codeAttributeReader.readAbsoluteInstruction().orElseThrow();
		Optional<ClassConstant> catchTypeConstant = Kit.upCast( codeAttributeReader.tryReadIndexAndGetConstant() );
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

	public void write( CodeConstantWriter codeConstantWriter )
	{
		codeConstantWriter.writeUnsignedShort( codeConstantWriter.getLocation( startInstruction ) );
		codeConstantWriter.writeUnsignedShort( codeConstantWriter.getLocation( endInstruction ) );
		codeConstantWriter.writeUnsignedShort( codeConstantWriter.getLocation( handlerInstruction ) );
		codeConstantWriter.writeUnsignedShort( catchTypeConstant.map( c -> codeConstantWriter.getConstantIndex( c ) ).orElse( 0 ) );
	}
}
