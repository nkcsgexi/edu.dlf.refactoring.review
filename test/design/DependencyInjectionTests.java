package design;

import junit.framework.TestSuite;

import org.eclipse.core.runtime.Assert;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.processors.ExtractMethodProcessor;
import edu.dlf.refactoring.processors.RenameMethodProcessor;
import edu.dlf.refactoring.processors.RenameTypeProcessor;

public class DependencyInjectionTests extends TestSuite{

	@Test
	public void CanGetRefactoringProcessorsTest()
	{
		Assert.isNotNull(ServiceLocator.ResolveType(RenameMethodProcessor.class));
		Assert.isNotNull(ServiceLocator.ResolveType(ExtractMethodProcessor.class));
		Assert.isNotNull(ServiceLocator.ResolveType(RenameTypeProcessor.class));
		Assert.isNotNull(ServiceLocator.ResolveType(EventBus.class));
	}
}
