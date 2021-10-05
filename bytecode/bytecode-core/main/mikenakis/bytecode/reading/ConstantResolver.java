package mikenakis.bytecode.reading;

import mikenakis.bytecode.model.Constant;

public interface ConstantResolver
{
	Constant getConstant( int constantIndex );
}
