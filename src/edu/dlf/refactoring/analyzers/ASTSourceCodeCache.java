package edu.dlf.refactoring.analyzers;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;

public class ASTSourceCodeCache extends CacheLoader<ASTNode, String> 
		implements RemovalListener<ASTNode, String>, LoadingCache<ASTNode, 
			String>{
	private final int maxSize = 2000;
	private final int timeInMinutes = 10;
	private final LoadingCache<ASTNode, String> internalCache = 
		CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterWrite(
			timeInMinutes, TimeUnit.MINUTES).removalListener(this).build(this);
	private final Logger logger;
	private final String directory = DlfFileUtils.desktop + "CachedFiles/";
	private final F2<P2<ASTNode, String>, P2<ASTNode, String>, Boolean> finder = 
		FJUtils.getReferenceEq((ASTNode)null).comap(FJUtils.
			getFirstElementInPFunc((ASTNode)null, (String)null)).eq();;
	
	private List<P2<ASTNode, String>> fileNames;

	@Inject
	public ASTSourceCodeCache(Logger logger) {
		this.logger = logger;
		this.fileNames = List.nil();
		File theDir = new File(directory);
		if (!theDir.exists()) {
			theDir.mkdir(); 
		}
		try {
			FileUtils.cleanDirectory(new File(directory));
		} catch (IOException e) {
			logger.fatal("Cannot clean directory.");
		}
	}

	@Override
	public void onRemoval(RemovalNotification<ASTNode, String> arg) {
		String time = ((Long)System.currentTimeMillis()).toString();
		try {
			File file = new File(directory + time + ".java");
			file.createNewFile();
			FileUtils.writeStringToFile(file, arg.getValue());
			fileNames = fileNames.snoc(P.p(arg.getKey(), time));
		} catch (Exception e) {
			logger.fatal(e);
		}
	}

	@Override
	public String load(ASTNode node) {
		Option<P2<ASTNode, String>> op = fileNames.find(finder.f(P.p(node, 
			"")));
		if(op.isNone()) {	
			logger.fatal("Cannot find original source.");
			return "";
		}
		try {
			return FileUtils.readFileToString(new File(directory + op.some()
				._2() + ".java"));
		} catch (Exception e) {
			logger.fatal("Cannot read cached file: " + e);
			return "";
		}
	}

	@Override
	public void cleanUp() {
		this.internalCache.cleanUp();
		this.fileNames = List.nil();
	}

	@Override
	public String get(ASTNode arg0, Callable<? extends String> arg1)
			throws ExecutionException {
		return this.internalCache.get(arg0, arg1);
	}

	@Override
	public ImmutableMap<ASTNode, String> getAllPresent(Iterable<?> arg0) {
		return this.internalCache.getAllPresent(arg0);
	}

	@Override
	public String getIfPresent(Object arg0) {
		return this.internalCache.getIfPresent(arg0);
	}

	@Override
	public void invalidate(Object arg0) {
		this.internalCache.invalidate(arg0);
	}

	@Override
	public void invalidateAll() {
		this.internalCache.invalidateAll();
	}

	@Override
	public void invalidateAll(Iterable<?> arg0) {
		this.internalCache.invalidateAll(arg0);
	}

	@Override
	public void put(ASTNode arg0, String arg1) {
		this.internalCache.put(arg0, arg1);
	}

	@Override
	public void putAll(Map<? extends ASTNode, ? extends String> arg0) {
		this.internalCache.putAll(arg0);
	}

	@Override
	public long size() {
		return this.internalCache.size();
	}

	@Override
	public CacheStats stats() {
		return this.internalCache.stats();
	}

	@Override
	public String apply(ASTNode arg0) {
		return this.internalCache.apply(arg0);
	}

	@Override
	public ConcurrentMap<ASTNode, String> asMap() {
		return this.internalCache.asMap();
	}

	@Override
	public String get(ASTNode arg0) throws ExecutionException {
		return this.internalCache.get(arg0);
	}

	@Override
	public ImmutableMap<ASTNode, String> getAll(Iterable<? extends ASTNode> arg0)
			throws ExecutionException {
		return this.internalCache.getAll(arg0);
	}

	@Override
	public String getUnchecked(ASTNode arg0) {
		return this.internalCache.getUnchecked(arg0);
	}

	@Override
	public void refresh(ASTNode arg0) {
		this.internalCache.refresh(arg0);
	}
}
