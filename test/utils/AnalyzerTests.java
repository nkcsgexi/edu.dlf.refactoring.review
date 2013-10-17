package utils;

import junit.framework.TestSuite;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Test;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;

public class AnalyzerTests extends TestSuite{

	@Test
	public void testMainTypeName() throws Exception
	{
		ASTNode node = TestUtils.getCUByFileName("TestCUBefore1.java");
		String name = ASTAnalyzer.getMainTypeName(node);
		Assert.isTrue(name.endsWith("TestCUBefore1"));
		Assert.isTrue(name.equals("dlf.test.TestCUBefore1"));
	}
	
	
}
