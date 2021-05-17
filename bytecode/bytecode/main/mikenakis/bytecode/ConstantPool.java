package mikenakis.bytecode;

import mikenakis.bytecode.attributes.code.InstructionModels;
import mikenakis.bytecode.attributes.code.instructions.LoadConstantInstruction;
import mikenakis.bytecode.constants.DoubleConstant;
import mikenakis.bytecode.constants.FloatConstant;
import mikenakis.bytecode.constants.IntegerConstant;
import mikenakis.bytecode.constants.LongConstant;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.kit.Kit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the constant pool of a java class file.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 */
public final class ConstantPool implements Iterable<Constant>
{
	//@formatter:off
	private static final IntegerConstant iConstM1 = new IntegerConstant( -1 );
	private static final IntegerConstant iConst0  = new IntegerConstant( 0 );
	private static final IntegerConstant iConst1  = new IntegerConstant( 1 );
	private static final IntegerConstant iConst2  = new IntegerConstant( 2 );
	private static final IntegerConstant iConst3  = new IntegerConstant( 3 );
	private static final IntegerConstant iConst4  = new IntegerConstant( 4 );
	private static final IntegerConstant iConst5  = new IntegerConstant( 5 );
	private static final LongConstant    lConst0  = new LongConstant   ( 0 );
	private static final LongConstant    lConst1  = new LongConstant   ( 1 );
	private static final FloatConstant   fConst0  = new FloatConstant  ( 0 );
	private static final FloatConstant   fConst1  = new FloatConstant  ( 1 );
	private static final FloatConstant   fConst2  = new FloatConstant  ( 2 );
	private static final DoubleConstant  dConst0  = new DoubleConstant ( 0 );
	private static final DoubleConstant  dConst1  = new DoubleConstant ( 1 );
	//@formatter:on

	//@formatter:off
	private static final Map<Constant,LoadConstantInstruction.Model> OPERANDLESS_LOAD_CONSTANT_INSTRUCTION_MODELS_FROM_CONSTANTS = Map.ofEntries(
		Map.entry( iConstM1, InstructionModels.ICONST_M1 ),
		Map.entry( iConst0 , InstructionModels.ICONST_0  ),
		Map.entry( iConst1 , InstructionModels.ICONST_1  ),
		Map.entry( iConst2 , InstructionModels.ICONST_2  ),
		Map.entry( iConst3 , InstructionModels.ICONST_3  ),
		Map.entry( iConst4 , InstructionModels.ICONST_4  ),
		Map.entry( iConst5 , InstructionModels.ICONST_5  ),
		Map.entry( lConst0 , InstructionModels.LCONST_0  ),
		Map.entry( lConst1 , InstructionModels.LCONST_1  ),
		Map.entry( fConst0 , InstructionModels.FCONST_0  ),
		Map.entry( fConst1 , InstructionModels.FCONST_1  ),
		Map.entry( fConst2 , InstructionModels.FCONST_2  ),
		Map.entry( dConst0 , InstructionModels.DCONST_0  ),
		Map.entry( dConst1 , InstructionModels.DCONST_1  ) );
	//@formatter:on

	public static Optional<LoadConstantInstruction.Model> tryGetOperandlessLoadConstantInstructionModelFromConstant( Constant constant )
	{
		return Optional.ofNullable( Kit.map.tryGet( OPERANDLESS_LOAD_CONSTANT_INSTRUCTION_MODELS_FROM_CONSTANTS, constant ) );
	}

	private final Runnable observer;
	public final ByteCodeType byteCodeType;
	private final List<ConstantEntry> constantList;

	public ConstantPool( Runnable observer, ByteCodeType byteCodeType )
	{
		this.observer = observer;
		this.byteCodeType = byteCodeType;
		constantList = new ArrayList<>( 1 );
		constantList.add( null ); // first entry is empty. (Ancient legacy bollocks.)
	}

	public ConstantPool( Runnable observer, ByteCodeType byteCodeType, BufferReader bufferReader )
	{
		this.observer = observer;
		this.byteCodeType = byteCodeType;
		int count = bufferReader.readUnsignedShort();
		assert count > 0;

		constantList = new ArrayList<>( count );
		constantList.add( null ); // first entry is empty. (Ancient legacy bollocks.)
		for( int index = 1; index < count; index++ )
		{
			int constantTag = bufferReader.readUnsignedByte();
			ConstantKind constantKind = Constant.getKindByTag( constantTag );
			Buffer buffer = constantKind.readBuffer( bufferReader );
			ConstantEntry entry = new ConstantEntry( this, constantKind, buffer );
			constantList.add( entry );
			if( constantKind.tag == LongConstant.TAG || constantKind.tag == DoubleConstant.TAG )
			{
				constantList.add( null );
				index++; //8-byte constants occupy two constant pool entries. (Ancient legacy bollocks.)
			}
		}
		assert constantList.size() == count;
	}

	private void markAsDirty()
	{
		observer.run();
	}

	public void write( BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( constantList.size() );
		for( ConstantEntry constantEntry : constantList )
		{
			if( constantEntry == null )
				continue;
			Constant constant = constantEntry.getConstant();
			bufferWriter.writeUnsignedByte( constant.kind.tag );
			constant.write( this, bufferWriter );
		}
	}

	public boolean isDefined( int constantIndex )
	{
		return constantList.get( constantIndex ) != null;
	}

	public Constant getConstant( int constantIndex )
	{
		assert constantList.get( constantIndex ) != null;
		return constantList.get( constantIndex ).getConstant();
	}

	@Override public Iterator<Constant> iterator()
	{
		return constantList.stream().filter( e -> e != null ).map( ConstantEntry::getConstant ).iterator();
	}

	private Optional<Integer> tryGetIndex( Constant constant )
	{
		if( constant == null )
			return Optional.of( 0 );
		for( int i = 0;  i < constantList.size();  i++ )
		{
			ConstantEntry constantEntry = constantList.get( i );
			if( constantEntry == null )
				continue;
			Constant existingConstant = constantEntry.getConstant();
			if( existingConstant.equals( constant ) )
				return Optional.of( i );
		}
		return Optional.empty();
	}

	public Integer getIndex( Constant constant )
	{
		return tryGetIndex( constant ).orElseThrow();
	}

	int addConstantAndGetIndex( Constant constant )
	{
		Optional<Integer> existingIndex = tryGetIndex( constant );
		if( existingIndex.isPresent() )
			return existingIndex.get();
		int index = constantList.size();
		ConstantEntry constantEntry = new ConstantEntry( this, constant );
		constantList.add( constantEntry );
		if( constant.kind.tag == LongConstant.TAG || constant.kind.tag == DoubleConstant.TAG )
			constantList.add( null ); //8-byte constants occupy two constant pool entries. (Ancient legacy bollocks.)
		markAsDirty();
		return index;
	}

	void clear()
	{
		constantList.clear();
		constantList.add( null ); // first entry is empty. (Ancient legacy bollocks.)
		markAsDirty();
	}

	public Constant readIndexAndGetConstant( BufferReader bufferReader )
	{
		int constantIndex = bufferReader.readUnsignedShort();
		return getConstant( constantIndex );
	}

	public Optional<Constant> tryReadIndexAndGetConstant( BufferReader bufferReader )
	{
		int constantIndex = bufferReader.readUnsignedShort();
		if( constantIndex == 0 )
			return Optional.empty();
		return Optional.of( getConstant( constantIndex ) );
	}

	public int size()
	{
		return constantList.size();
	}

	@Override public String toString()
	{
		return size() + " entries";
	}
}
