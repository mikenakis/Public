package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.StackMapFrame;

import java.util.Collection;
import java.util.Optional;

public class CodeConstantWriter extends ConstantWriter
{
	private final LocationMap locationMap;
	public final RealInstructionWriter instructionWriter;

	public CodeConstantWriter( ConstantWriter constantWriter, Collection<Instruction> instructions )
	{
		super( constantWriter.bufferWriter, constantWriter.constantPool, constantWriter.bootstrapPool );
		locationMap = getLocationMap( instructions, constantWriter.constantPool );
		instructionWriter = new RealInstructionWriter( locationMap, constantWriter.constantPool );
	}

	public int getLocation( Instruction instruction ) { return locationMap.getLocation( instruction ); }
	public int getLocation( Optional<Instruction> instruction ) { return locationMap.getLocation( instruction ); }

	@Override public CodeConstantWriter asCodeConstantWriter() { return this; }


	private static LocationMap getLocationMap( Iterable<Instruction> instructions, ConstantPool constantPool )
	{
		WritingLocationMap writingLocationMap = new WritingLocationMap();
		FakeInstructionWriter instructionWriter = new FakeInstructionWriter( constantPool, writingLocationMap );
		for( Instruction instruction : instructions )
		{
			int startLocation = instructionWriter.location;
			writingLocationMap.add( instruction );
			instruction.write( instructionWriter );
			int length = instructionWriter.location - startLocation;
			writingLocationMap.setLength( instruction, length );
		}

		for( ; ; )
		{
			boolean anyWorkDone = false;
			for( Instruction instruction : instructionWriter.sourceInstructions )
			{
				int location = writingLocationMap.getLocation( instruction );
				instructionWriter.location = location;
				int oldLength = writingLocationMap.getLength( instruction );
				instruction.write( instructionWriter );
				int newLength = instructionWriter.location - location;
				assert newLength <= oldLength;
				if( newLength == oldLength )
					continue;
				writingLocationMap.removeBytes( location + newLength, oldLength - newLength );
				anyWorkDone = true;
			}
			if( !anyWorkDone )
				break;
		}

		return writingLocationMap;
	}

	public int getOffsetDelta( StackMapFrame stackMapFrame, Optional<StackMapFrame> previousFrame )
	{
		int offset = getLocation( Optional.of( stackMapFrame.getTargetInstruction() ) );
		if( previousFrame.isEmpty() )
			return offset;
		int previousLocation = getLocation( Optional.of( previousFrame.get().getTargetInstruction() ) );
		return offset - previousLocation - 1;
	}
}
