package mikenakis.testana.test_engines.junit;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class NaturalOrderMethodComparator implements Comparator<Method>
{
	private final Function<Method,Integer> methodIndexResolver;
	private final Map<Method,Integer> indexFromJavaMethodMap = new HashMap<>();

	NaturalOrderMethodComparator( Function<Method,Integer> methodIndexResolver )
	{
		this.methodIndexResolver = methodIndexResolver;
	}

	@Override public int compare( Method javaMethod1, Method javaMethod2 )
	{
		int index1 = getMethodIndex( javaMethod1 );
		int index2 = getMethodIndex( javaMethod2 );
		return Integer.compare( index1, index2 );
	}

	private int getMethodIndex( Method javaMethod )
	{
		return indexFromJavaMethodMap.computeIfAbsent( javaMethod, methodIndexResolver );
	}
}
