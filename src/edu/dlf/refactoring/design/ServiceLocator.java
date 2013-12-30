package edu.dlf.refactoring.design;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import edu.dlf.refactoring.analyzers.ASTSourceCodeCache;
import edu.dlf.refactoring.analyzers.DlfFileUtils;
import edu.dlf.refactoring.change.ChangeComponent;
import edu.dlf.refactoring.change.ChangeComponentInjector;
import edu.dlf.refactoring.change.HistorySavingComponent;
import edu.dlf.refactoring.checkers.RefactoringCheckerComponent;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponent;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector;
import edu.dlf.refactoring.hiding.HidingComponentInjector;
import edu.dlf.refactoring.hiding.RefactoringHidingComponent;
import edu.dlf.refactoring.implementer.ImplementerCompInjector;
import edu.dlf.refactoring.implementer.RefactoringImplementerComponent;
import edu.dlf.refactoring.study.StudyLogLevel;
import edu.dlf.refactoring.ui.CodeReviewUIComponent;
import edu.dlf.refactoring.ui.UICompInjector;
import edu.dlf.refactoring.utils.WorkQueue;

public class ServiceLocator extends AbstractModule {
	private final static AbstractModule _instance = new ServiceLocator();
	private final static Injector injector = Guice.createInjector(_instance);

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR })
	@Retention(RUNTIME)
	public @interface HistorySavingCompAnnotation {
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR })
	@Retention(RUNTIME)
	public @interface ChangeCompAnnotation {
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR })
	@Retention(RUNTIME)
	public @interface RefactoringDetectionCompAnnotation {
	}


	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR })
	@Retention(RUNTIME)
	public @interface RefactoringImplementaterCompAnnotation {
	}
	
	
	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR })
	@Retention(RUNTIME)
	public @interface RefactoringCheckerCompAnnotation {
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR })
	@Retention(RUNTIME)
	public @interface UICompAnnotation {
	}
	
	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR })
	@Retention(RUNTIME)
	public @interface HidingCompAnnotation {
	}

	private ServiceLocator() {

	}

	@Override
	protected void configure() {
		this.install(new ChangeComponentInjector());
		this.install(new RefactoringDetectionComponentInjector());
		this.install(new ImplementerCompInjector());
		this.install(new UICompInjector());
		this.install(new HidingComponentInjector());

		bind(ComponentsRepository.class).in(Singleton.class);
		bind(IFactorComponent.class).annotatedWith(HistorySavingCompAnnotation.class)
			.to(HistorySavingComponent.class).in(Singleton.class);
		bind(IFactorComponent.class).annotatedWith(ChangeCompAnnotation.class)
			.to(ChangeComponent.class).in(Singleton.class);
		bind(IFactorComponent.class).annotatedWith(RefactoringDetectionCompAnnotation.class)
			.to(RefactoringDetectionComponent.class).in(Singleton.class);
		bind(IFactorComponent.class).annotatedWith(RefactoringImplementaterCompAnnotation.class)
			.to(RefactoringImplementerComponent.class).in(Singleton.class);
		bind(IFactorComponent.class).annotatedWith(RefactoringCheckerCompAnnotation.class)
			.to(RefactoringCheckerComponent.class).in(Singleton.class);
		bind(IFactorComponent.class).annotatedWith(UICompAnnotation.class)
			.to(CodeReviewUIComponent.class).in(Singleton.class);
		bind(IFactorComponent.class).annotatedWith(HidingCompAnnotation.class)
			.to(RefactoringHidingComponent.class).in(Singleton.class);

		bind(LoadingCache.class).to(ASTSourceCodeCache.class).in(Singleton.class);
	}

	public static <T> T ResolveType(Class T) {
		return (T) injector.getInstance(T);
	}
	
	
	private static String PATTERN = "%d [%p|%c|%C{1}] %m%n";
	
	@Provides
	@Singleton
	private Logger GetLogger() throws Exception {
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().addAppender(createConsoleAppender());
		Logger.getRootLogger().addAppender(createConsoleLogFileAppender());
		Logger.getRootLogger().addAppender(createTotalLogAppender());
		Logger.getRootLogger().addAppender(createStudyLogAppender());
		Logger.getRootLogger().addAppender(createFatalLogAppender());
		return Logger.getRootLogger();
	}
	
	private ConsoleAppender createConsoleAppender() {
		ConsoleAppender console = new ConsoleAppender();
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		return console;
	}

	private RollingFileAppender createConsoleLogFileAppender() throws IOException {
		RollingFileAppender fa = new RollingFileAppender();
		fa.setImmediateFlush(true);
		fa.setMaximumFileSize(Integer.MAX_VALUE);
		fa.setName("ConsoleFileLog");
		fa.setFile(DlfFileUtils.desktop + "console.log", true, true, 1);
		fa.setLayout(new PatternLayout(PATTERN));
		fa.setThreshold(Level.INFO);
		fa.setAppend(true);
		fa.activateOptions();
		return fa;
	}
	
	private RollingFileAppender createTotalLogAppender() throws IOException {
		RollingFileAppender fa = new RollingFileAppender();
		fa.setImmediateFlush(true);
		fa.setMaximumFileSize(Integer.MAX_VALUE);
		fa.setName("FileLogger");
		fa.setFile(DlfFileUtils.desktop + "RefReviewer.log", true, true, 1);
		fa.setLayout(new PatternLayout(PATTERN));
		fa.setThreshold(Level.DEBUG);
		fa.setAppend(true);
		fa.activateOptions();
		return fa;
	}

	private RollingFileAppender createStudyLogAppender() throws Exception {
		RollingFileAppender fa = new RollingFileAppender();
		fa.setImmediateFlush(true);
		fa.setMaximumFileSize(Integer.MAX_VALUE);
		fa.setName("Study");
		fa.setFile(DlfFileUtils.desktop + "StudyInfo.log", true, true, 1);
		fa.setLayout(new PatternLayout(PATTERN));
		fa.setThreshold(StudyLogLevel.LEVEL);
		fa.setAppend(true);
		fa.activateOptions();
		return fa;
	}
	
	private RollingFileAppender createFatalLogAppender() throws Exception {
		RollingFileAppender fa = new RollingFileAppender();
		fa.setImmediateFlush(true);
		fa.setMaximumFileSize(Integer.MAX_VALUE);
		fa.setName("Fatal");
		fa.setFile(DlfFileUtils.desktop + "Fatal.log", true, true, 1);
		fa.setLayout(new PatternLayout(PATTERN));
		fa.setThreshold(Level.ERROR);
		fa.setAppend(true);
		fa.activateOptions();
		return fa;
	}
	

	@Provides
	@Singleton
	private WorkQueue getSingleThreadQueue() {
		return new WorkQueue(1);
	}
}
