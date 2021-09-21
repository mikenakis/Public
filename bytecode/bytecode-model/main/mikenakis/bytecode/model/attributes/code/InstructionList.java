package mikenakis.bytecode.model.attributes.code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a list of instructions.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InstructionList
{
	public static InstructionList of()
	{
		return of( new ArrayList<>() );
	}

	public static InstructionList of( List<Instruction> instructions )
	{
		return new InstructionList( instructions );
	}

	private final List<Instruction> instructions;

	private InstructionList( List<Instruction> instructions )
	{
		this.instructions = instructions;
	}

	public Collection<Instruction> all()
	{
		return Collections.unmodifiableCollection( instructions );
	}

	public void add( Instruction instruction )
	{
		instructions.add( instruction );
	}

	public void insert( int index, Instruction instruction )
	{
		instructions.add( index, instruction );
	}

	public boolean contains( Instruction instruction )
	{
		return instructions.contains( instruction );
	}

	public int size()
	{
		return instructions.size();
	}
}
