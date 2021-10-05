package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the constant pool of a java class file.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 */
final class ConstantPool implements Interner
{
	private final List<Constant> entries = new ArrayList<>();

	ConstantPool()
	{
		entries.add( null ); // first entry is empty. (Ancient legacy bollocks.)
	}

	private int tryGetIndex( Constant constant )
	{
		for( int i = 0; i < entries.size(); i++ )
		{
			Constant existingConstant = entries.get( i );
			if( existingConstant == null )
				continue;
			if( existingConstant.equals( constant ) )
				return i;
		}
		return -1;
	}

	int getIndex( Constant constant )
	{
		int index = tryGetIndex( constant );
		assert index != -1;
		return index;
	}

	@Override public void intern( Constant constant )
	{
		int existingIndex = tryGetIndex( constant );
		assert existingIndex != 0;
		if( existingIndex == -1 )
		{
			assert !entries.isEmpty();
			entries.add( constant );
			if( constant.tag == Constant.tag_Long || constant.tag == Constant.tag_Double )
				entries.add( null ); //8-byte constants occupy two constant pool entries. (Ancient legacy bollocks.)
		}
	}

	int size()
	{
		return entries.size();
	}

	Iterable<Constant> constants()
	{
		return Kit.iterable.filtered( entries, c -> c != null );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " entries";
	}
}
