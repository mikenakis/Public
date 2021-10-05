package mikenakis.bytecode.reading;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.functional.Procedure1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class CodeAttributeReader extends AttributeReader
{
	public final ReadingLocationMap locationMap;
	private final Collection<Runnable> fixUps = new ArrayList<>();

	public CodeAttributeReader( BufferReader bufferReader, ConstantResolver constantResolver, ReadingLocationMap locationMap )
	{
		super( bufferReader, constantResolver );
		this.locationMap = locationMap;
	}

	public Optional<Instruction> readAbsoluteInstruction()
	{
		// Absolute instruction references are never used in code; they are only used in attributes.
		// Thus, by the time an absolute instruction reference is parsed, all instructions have already been parsed,
		// which means that we can directly reference the target instruction without the need for fix-ups.
		int targetLocation = bufferReader.readUnsignedShort(); //an unsigned short works here because java methods are limited to 64k.
		return locationMap.getInstruction( targetLocation );
	}

	public void skipToAlign() { bufferReader.skip( Helpers.padding( bufferReader.getPosition() ) ); }

	public Constant getConstant( int index )
	{
		return constantResolver.getConstant( index );
	}

	public List<Instruction> readInstructions()
	{
		List<Instruction> instructions = new ArrayList<>();
		while( !bufferReader.isAtEnd() )
		{
			int startLocation = bufferReader.getPosition();
			Instruction instruction = Instruction.read( this );
			int endLocation = bufferReader.getPosition();
			locationMap.add( startLocation, instruction, endLocation - startLocation );
			instructions.add( instruction );
		}
		for( Runnable fixUp : fixUps )
			fixUp.run();
		return instructions;
	}

	public void setRelativeTargetInstruction( Instruction sourceInstruction, int targetInstructionOffset, Procedure1<Instruction> setter )
	{
		assert !locationMap.contains( sourceInstruction );
		int sourceInstructionLocation = locationMap.getPosition();
		assert sourceInstructionLocation + targetInstructionOffset < locationMap.size();
		if( targetInstructionOffset < 0 )
			fix( sourceInstructionLocation, targetInstructionOffset, setter );
		else
			fixUps.add( () -> //
			{
				assert sourceInstructionLocation == locationMap.getLocation( sourceInstruction );
				fix( sourceInstructionLocation, targetInstructionOffset, setter );
			} );
	}

	private void fix( int sourceInstructionLocation, int targetInstructionOffset, Procedure1<Instruction> setter )
	{
		int targetInstructionLocation = sourceInstructionLocation + targetInstructionOffset;
		Instruction targetInstruction = locationMap.getInstruction( targetInstructionLocation ).orElseThrow();
		setter.invoke( targetInstruction );
	}

	@Override public CodeAttributeReader asCodeAttributeReader() { return this; }
}
