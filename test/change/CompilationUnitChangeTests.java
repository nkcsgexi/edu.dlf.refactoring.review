package change;

import junit.framework.TestSuite;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Before;
import org.junit.Test;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.FileUtils;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.calculator.CompilationUnitChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.ChangeCriteriaBuilder;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import fj.data.List;

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
	public void printChangeTest() throws Exception
	{
		ASTNode cuBefore = ASTAnalyzer.parseICompilationUnit(FileUtils.readAllFunc.f(testFileFolder + 
				"TestCUBefore1.java"));
		ASTNode cuAfter = ASTAnalyzer.parseICompilationUnit(FileUtils.readAllFunc.f(testFileFolder + 
				"TestCUAfter1.java"));
		ISourceChange change = calculator.CalculateASTNodeChange(new ASTNodePair(cuBefore, cuAfter));
		change = SourceChangeUtils.pruneSourceChange(change);
		String s = SourceChangeUtils.printChangeTree(change);
	}
	
	@Test
	public void searchUtilTest() throws Exception
	{
		ASTNode cuBefore = ASTAnalyzer.parseICompilationUnit(FileUtils.readAllFunc.f(testFileFolder + 
				"TestCUBefore1.java"));
		ASTNode cuAfter = ASTAnalyzer.parseICompilationUnit(FileUtils.readAllFunc.f(testFileFolder + 
				"TestCUAfter1.java"));
		ISourceChange change = calculator.CalculateASTNodeChange(new ASTNodePair(cuBefore, cuAfter));
		change = SourceChangeUtils.pruneSourceChange(change);
		ChangeCriteriaBuilder changeBuilder = new ChangeCriteriaBuilder();
		changeBuilder.addSingleChangeCriteria("Method", SourceChangeType.PARENT).
			addSingleChangeCriteria("BlockStatement", SourceChangeType.PARENT).
				addSingleChangeCriteria("Statement", SourceChangeType.ADD);
		List<IChangeSearchResult> results = changeBuilder.getSearchCriteria().search(change);
		Assert.isTrue(results.isNotEmpty());
		Assert.isTrue(results.length() == 1);
		Assert.isTrue(results.head().getSourceChanges().length() == 3);
		
			
	}
	
}
