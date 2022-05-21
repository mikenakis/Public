package io.github.mikenakis.bytecode.model.attributes.code;

import java.util.Collections;
import java.util.List;

/**
 * Represents a list of instructions.
 *
 * @author michael.gr
 */
public final class InstructionList
{
	public static InstructionList of( List<Instruction> instructions )
	{
		return new InstructionList( instructions );
	}

	private final List<Instruction> instructions;
	private int insertionPoint;

	private InstructionList( List<Instruction> instructions )
	{
		this.instructions = instructions;
		insertionPoint = instructions.size();
	}

	public int getInsertionPoint() { return insertionPoint; }

	public void setInsertionPoint( int insertionPoint )
	{
		assert insertionPoint >= 0;
		assert insertionPoint <= size();
		this.insertionPoint = insertionPoint;
	}

	public List<Instruction> all() { return Collections.unmodifiableList( instructions ); }
	public void add( Instruction instruction ) { instructions.add( insertionPoint++, instruction ); }
	public void insert( int index, Instruction instruction ) { instructions.add( index, instruction ); }
	public boolean contains( Instruction instruction ) { return instructions.contains( instruction ); }
	public int size() { return instructions.size(); }
}
