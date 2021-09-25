package mikenakis.bytecode.reading;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.functional.Procedure1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

final class InstructionReader
{
	public static void run( BufferReader bufferReader, ReadingLocationMap locationMap, ConstantPool constantPool, Procedure1<InstructionReader> consumer )
	{
		var concreteInstructionReader = new InstructionReader( bufferReader, locationMap, constantPool );
		consumer.invoke( concreteInstructionReader );
		for( Runnable fixUp : concreteInstructionReader.fixUps )
			fixUp.run();
	}

	private final Collection<Runnable> fixUps = new ArrayList<>();
	private final ReadingLocationMap locationMap;
	private final BufferReader bufferReader;
	private final ConstantPool constantPool;

	private InstructionReader( BufferReader bufferReader, ReadingLocationMap locationMap, ConstantPool constantPool )
	{
		this.bufferReader = bufferReader;
		this.locationMap = locationMap;
		this.constantPool = constantPool;
	}

	int readInt() { return bufferReader.readInt(); }
	int readSignedByte() { return bufferReader.readSignedByte(); }
	int readSignedShort() { return bufferReader.readSignedShort(); }
	int readUnsignedByte() { return bufferReader.readUnsignedByte(); }
	int readUnsignedShort() { return bufferReader.readUnsignedShort(); }
	void skipToAlign() { bufferReader.skip( Helpers.padding( bufferReader.getPosition() ) ); }

	void setRelativeTargetInstruction( Instruction sourceInstruction, int targetInstructionOffset, Procedure1<Instruction> setter )
	{
		assert !locationMap.contains( sourceInstruction );
		int sourceInstructionLocation = locationMap.getPosition();
		assert sourceInstructionLocation + targetInstructionOffset < locationMap.getLocation( Optional.empty() );
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

	Constant getConstant( int index )
	{
		return constantPool.getConstant( index );
	}
}
