package detectors;

import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.TestUtils;

import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.change.ChangeComponent;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.calculator.CompilationUnitChangeCalculator;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.ExtractMethodDetector;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponent;
import edu.dlf.refactoring.refactorings.ExtractMethodRefactoring;
import fj.data.List;

public class ExtractMethodDetectorTests extends TestSuite{

	private static IFactorComponent detectorComp;
	private static IFactorComponent changeComp;
	private static EventBus bus;
	private static IASTNodeChangeCalculator cuCalculator;
	private static IRefactoringDetector emDetector;
	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	@BeforeClass
	public static void setUp()
	{
		changeComp = ServiceLocator.ResolveType(
				ChangeComponent.class);
		detectorComp = ServiceLocator.ResolveType(
				RefactoringDetectionComponent.class);
		bus = ServiceLocator.ResolveType(EventBus.class);
		cuCalculator = ServiceLocator.ResolveType(CompilationUnitChangeCalculator.class);
		emDetector = ServiceLocator.ResolveType(ExtractMethodDetector.class);
	}
	
	@Test
	public void getInstanceTest()
	{
		Assert.isNotNull(detectorComp);
		Assert.isNotNull(changeComp);
	}
	
	@Test
	public void detectEMTest() throws Exception
	{
		ISourceChange change = cuCalculator.CalculateASTNodeChange(TestUtils.
				getNodePairByFileNames("TestCUBefore1.java", "TestCUAfter1.java"));
		change = SourceChangeUtils.pruneSourceChange(change);
		List<IRefactoring> refactorings = emDetector.detectRefactoring(change);
		Assert.isTrue(refactorings.length() == 1);
		IRefactoring refactoring = refactorings.head();
		System.out.println(refactoring);
		Assert.isTrue(refactoring.getRefactoringType() == RefactoringType.ExtractMethod);
		Assert.isTrue(refactoring.getEffectedNodeList(ExtractMethodRefactoring.ExtractedStatements).length() == 3);
		Assert.isLegal(refactoring.getEffectedNode(ExtractMethodRefactoring.DeclaredMethod).
				getStructuralProperty(MethodDeclaration.NAME_PROPERTY).toString().equals("barExtracted"));
	}
	

	
	
	
	
	
}
