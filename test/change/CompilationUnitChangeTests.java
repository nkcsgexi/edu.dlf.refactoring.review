package change;

import junit.framework.TestSuite;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Before;
import org.junit.Test;
import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.FileUtils;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.calculator.CompilationUnitChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ServiceLocator;

public class CompilationUnitChangeTests extends TestSuite{
	
	private final String testFileFolder = System.getProperty("user.dir") + "/TestFiles/";
	private IASTNodeChangeCalculator calculator;

	@Before
	public void prepareChangeCalculator()
	{
		this.calculator = ServiceLocator.ResolveType(CompilationUnitChangeCalculator.class);
		Assert.isNotNull(this.calculator);
	}
	
	@Test
	public void Test1() throws Exception
	{
		ASTNode cuBefore = ASTAnalyzer.parseICompilationUnit(FileUtils.readAll(testFileFolder + 
				"TestCUBefore1.java"));
		ASTNode cuAfter = ASTAnalyzer.parseICompilationUnit(FileUtils.readAll(testFileFolder + 
				"TestCUAfter1.java"));
		calculator.CalculateASTNodeChange(new ASTNodePair(cuBefore, cuAfter));
	}
	
}
