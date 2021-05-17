package mikenakis.bytecode.attributes.code;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.attributes.code.instructions.ImmediateLoadConstantInstruction;
import mikenakis.bytecode.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.attributes.code.instructions.OperandlessLoadConstantInstruction;
import mikenakis.bytecode.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents a virtual machine instruction.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Instruction extends Printable
{
	private Optional<Integer> pc;
	private int length; // 0 = unknown

	protected Instruction( Optional<Integer> pc )
	{
		assert pc.isEmpty() || pc.get() >= 0;
		this.pc = pc;
		length = 0;
	}

	public abstract void intern( ConstantPool constantPool );

	public abstract void write( ConstantPool constantPool, BufferWriter bufferWriter );

	public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		/* nothing to do */
	}

	public abstract InstructionModel getModel();

	//@formatter:off
	public Optional<BranchInstruction>                  tryAsBranchInstruction                 () { return Optional.empty(); }
	public Optional<ConditionalBranchInstruction>       tryAsConditionalBranchInstruction      () { return Optional.empty(); }
	public Optional<ConstantReferencingInstruction>     tryAsConstantReferencingInstruction    () { return Optional.empty(); }
	public Optional<IIncInstruction>                    tryAsIIncInstruction                   () { return Optional.empty(); }
	public Optional<ImmediateLoadConstantInstruction>   tryAsImmediateLoadConstantInstruction  () { return Optional.empty(); }
	public Optional<IndirectLoadConstantInstruction>    tryAsIndirectLoadConstantInstruction   () { return Optional.empty(); }
	public Optional<InvokeDynamicInstruction>           tryAsInvokeDynamicInstruction          () { return Optional.empty(); }
	public Optional<InvokeInterfaceInstruction>         tryAsInvokeInterfaceInstruction        () { return Optional.empty(); }
	public Optional<LocalVariableInstruction>           tryAsLocalVariableInstruction          () { return Optional.empty(); }
	public Optional<LookupSwitchInstruction>            tryAsLookupSwitchInstruction           () { return Optional.empty(); }
	public Optional<MultiANewArrayInstruction>          tryAsMultiANewArrayInstruction         () { return Optional.empty(); }
	public Optional<NewPrimitiveArrayInstruction>       tryAsNewPrimitiveArrayInstruction      () { return Optional.empty(); }
	public Optional<OperandlessInstruction>             tryAsOperandlessInstruction            () { return Optional.empty(); }
	public Optional<OperandlessLoadConstantInstruction> tryAsOperandlessLoadConstantInstruction() { return Optional.empty(); }
	public Optional<TableSwitchInstruction>             tryAsTableSwitchInstruction            () { return Optional.empty(); }

	public final BranchInstruction                  asBranchInstruction                 () { return tryAsBranchInstruction                 ().orElseThrow(); }
	public final ConditionalBranchInstruction       asConditionalBranchInstruction      () { return tryAsConditionalBranchInstruction      ().orElseThrow(); }
	public final ConstantReferencingInstruction     asConstantReferencingInstruction    () { return tryAsConstantReferencingInstruction    ().orElseThrow(); }
	public final IIncInstruction                    asIIncInstruction                   () { return tryAsIIncInstruction                   ().orElseThrow(); }
	public final ImmediateLoadConstantInstruction   asImmediateLoadConstantInstruction  () { return tryAsImmediateLoadConstantInstruction  ().orElseThrow(); }
	public final IndirectLoadConstantInstruction    asIndirectLoadConstantInstruction   () { return tryAsIndirectLoadConstantInstruction   ().orElseThrow(); }
	public final InvokeDynamicInstruction           asInvokeDynamicInstruction          () { return tryAsInvokeDynamicInstruction          ().orElseThrow(); }
	public final InvokeInterfaceInstruction         asInvokeInterfaceInstruction        () { return tryAsInvokeInterfaceInstruction        ().orElseThrow(); }
	public final LocalVariableInstruction           asLocalVariableInstruction          () { return tryAsLocalVariableInstruction          ().orElseThrow(); }
	public final LookupSwitchInstruction            asLookupSwitchInstruction           () { return tryAsLookupSwitchInstruction           ().orElseThrow(); }
	public final MultiANewArrayInstruction          asMultiANewArrayInstruction         () { return tryAsMultiANewArrayInstruction         ().orElseThrow(); }
	public final NewPrimitiveArrayInstruction       asNewPrimitiveArrayInstruction      () { return tryAsNewPrimitiveArrayInstruction      ().orElseThrow(); }
	public final OperandlessInstruction             asOperandlessInstruction            () { return tryAsOperandlessInstruction            ().orElseThrow(); }
	public final OperandlessLoadConstantInstruction asOperandlessLoadConstantInstruction() { return tryAsOperandlessLoadConstantInstruction().orElseThrow(); }
	public final TableSwitchInstruction             asTableSwitchInstruction            () { return tryAsTableSwitchInstruction            ().orElseThrow(); }

	public final boolean isBranchInstruction                 () { return tryAsBranchInstruction                 ().isPresent(); }
	public final boolean isConditionalBranchInstruction      () { return tryAsConditionalBranchInstruction      ().isPresent(); }
	public final boolean isConstantReferencingInstruction    () { return tryAsConstantReferencingInstruction    ().isPresent(); }
	public final boolean isIIncInstruction                   () { return tryAsIIncInstruction                   ().isPresent(); }
	public final boolean isImmediateLoadConstantInstruction  () { return tryAsImmediateLoadConstantInstruction  ().isPresent(); }
	public final boolean isIndirectLoadConstantInstruction   () { return tryAsIndirectLoadConstantInstruction   ().isPresent(); }
	public final boolean isInvokeDynamicInstruction          () { return tryAsInvokeDynamicInstruction          ().isPresent(); }
	public final boolean isInvokeInterfaceInstruction        () { return tryAsInvokeInterfaceInstruction        ().isPresent(); }
	public final boolean isLocalVariableInstruction          () { return tryAsLocalVariableInstruction          ().isPresent(); }
	public final boolean isLookupSwitchInstruction           () { return tryAsLookupSwitchInstruction           ().isPresent(); }
	public final boolean isMultiANewArrayInstruction         () { return tryAsMultiANewArrayInstruction         ().isPresent(); }
	public final boolean isNewPrimitiveArrayInstruction      () { return tryAsNewPrimitiveArrayInstruction      ().isPresent(); }
	public final boolean isOperandlessInstruction            () { return tryAsOperandlessInstruction            ().isPresent(); }
	public final boolean isOperandlessLoadConstantInstruction() { return tryAsOperandlessLoadConstantInstruction().isPresent(); }
	public final boolean isTableSwitchInstruction            () { return tryAsTableSwitchInstruction            ().isPresent(); }
	//@formatter:on

	public final boolean isPcEstablished()
	{
		return pc.isPresent();
	}

	public final int getPc()
	{
		return pc.orElseThrow();
	}

	public final void setPc( int pc )
	{
		this.pc = Optional.of( pc );
	}

	public final void clearPc()
	{
		pc = Optional.empty();
	}

	public final int getLength()
	{
		return length;
	}

	public final void setLength( int length )
	{
		this.length = length;
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "pc = " ).append( pc );
		builder.append( ", length = " ).append( length );
		builder.append( ", opCode = " ).append( OpCode.getOpCodeName( getModel().opCode ) );
	}
}
