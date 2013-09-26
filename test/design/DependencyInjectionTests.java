package design;

import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.change.ChangeComponent;
import edu.dlf.refactoring.change.calculator.expression.ExpressionChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.BlockChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.IfStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.StatementChangeCalculator;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.processors.ExtractMethodProcessor;
import edu.dlf.refactoring.processors.RenameMethodProcessor;
import edu.dlf.refactoring.processors.RenameTypeProcessor;
import edu.dlf.refactoring.utils.WorkQueue;

public class DependencyInjectionTests extends TestSuite{

	@Test
	public void CanGetRefactoringProcessorsTest()
	{
		Assert.isNotNull(ServiceLocator.ResolveType(RenameMethodProcessor.class));
		Assert.isNotNull(ServiceLocator.ResolveType(ExtractMethodProcessor.class));
		Assert.isNotNull(ServiceLocator.ResolveType(RenameTypeProcessor.class));
		Assert.isNotNull(ServiceLocator.ResolveType(ChangeComponent.class));
		Assert.isNotNull(ServiceLocator.ResolveType(EventBus.class));
		Assert.isNotNull(ServiceLocator.ResolveType(Logger.class));
		Assert.isNotNull(ServiceLocator.ResolveType(BlockChangeCalculator.class));
		Assert.isNotNull(ServiceLocator.ResolveType(IfStatementChangeCalculator.class));
		Assert.isNotNull(ServiceLocator.ResolveType(StatementChangeCalculator.class));
		Assert.isNotNull(ServiceLocator.ResolveType(ExpressionChangeCalculator.class));
		Assert.isNotNull(ServiceLocator.ResolveType(WorkQueue.class));
	}
	
	@Test
	public void RetrieveLoggerTest()
	{
		Logger log = ServiceLocator.ResolveType(Logger.class);
	}
}
