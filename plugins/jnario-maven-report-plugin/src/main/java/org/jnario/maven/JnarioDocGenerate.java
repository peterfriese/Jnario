package org.jnario.maven;

import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;
import static org.eclipse.xtext.util.Strings.concat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler;
import org.eclipse.xtend.maven.MavenProjectResourceSetProvider;
import org.eclipse.xtend.maven.XtendTestCompile;
import org.eclipse.xtext.ISetup;
import org.jnario.compiler.HtmlAssetsCompiler;
import org.jnario.compiler.JnarioDocCompiler;
import org.jnario.feature.FeatureStandaloneSetup;
import org.jnario.report.Executable2ResultMapping;
import org.jnario.report.HashBasedSpec2ResultMapping;
import org.jnario.report.SpecResultParser;
import org.jnario.spec.SpecStandaloneSetup;
import org.jnario.suite.SuiteStandaloneSetup;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Goal which generates Jnario documentation.
 * 
 * @author Sebastian Benz - Initial contribution and API
 * @extendsPlugin xtend-maven-plugin
 * @requiresDependencyResolution test
 * @goal generate	
 */
public class JnarioDocGenerate extends XtendTestCompile {

	private final class XmlFiles implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith("xml");
		}
	}

	/**
	 * Location of the generated documentation.
	 * 
	 * @parameter default-value="${basedir}/target/jnario-doc"
	 * @required
	 */
	private String docOutputDirectory;
	
	/**
	 * Location of the generated JUnit XML reports.
	 * 
	 * @parameter default-value="${basedir}/target/surefire-reports"
	 * @required
	 */
	private String reportsDirectory;
	
	/**
	 * Location of the generated JUnit XML reports.
	 * 
	 * @parameter 
	 */
	private String sourceDirectory;

	@Override
	protected void internalExecute() throws MojoExecutionException {
		getLog().info("Generating Jnario reports to " + docOutputDirectory);
		
		// the order is important, the suite compiler must be executed last
		List<Injector> injectors = createInjectors(new SpecStandaloneSetup(), new FeatureStandaloneSetup(), new SuiteStandaloneSetup());
		generateCssAndJsFiles(injectors);

		HashBasedSpec2ResultMapping resultMapping = createSpec2ResultMapping(injectors);
		ResourceSet resourceSet = createResourceSet();
		
		for (Injector injector : injectors) {
			generateDoc(resourceSet, injector, resultMapping);
		}
	}

	protected HashBasedSpec2ResultMapping createSpec2ResultMapping(List<Injector> injectors) throws MojoExecutionException {
		HashBasedSpec2ResultMapping resultMapping = injectors.get(2).getInstance(HashBasedSpec2ResultMapping.class);
		File reportFolder = new File(reportsDirectory);
		if(reportFolder.exists()){
			addExecutionResults(resultMapping, reportFolder);
		}else{
			throw new MojoExecutionException("Surefire Report folder does not exist");
		}
		return resultMapping;
	}

	protected void generateCssAndJsFiles(List<Injector> injectors) {
		HtmlAssetsCompiler assetsCompiler = injectors.get(0).getInstance(HtmlAssetsCompiler.class);
		assetsCompiler.setOutputPath(docOutputDirectory);
		getLog().info("Generating HTML assets to " + docOutputDirectory);
		assetsCompiler.compile();
	}

	protected void addExecutionResults(HashBasedSpec2ResultMapping resultMapping, File reportFolder) throws MojoExecutionException {
		SpecResultParser specResultParser = new SpecResultParser();
		for (File file : reportFolder.listFiles(new XmlFiles())) {
			FileInputStream is = null;
			try {
				is = new FileInputStream(file);
				specResultParser.parse(is, resultMapping);
			} catch (Exception e) {
				throw new MojoExecutionException("Exception while parsing spec for: " + file, e);
			}finally{
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			
		}
	}

	protected void compileTestSources(XtendBatchCompiler xtend2BatchCompiler) throws MojoExecutionException {
		List<String> testCompileSourceRoots = Lists.newArrayList(project.getTestCompileSourceRoots());
		String testClassPath = concat(File.pathSeparator, getTestClassPath());
		if(sourceDirectory != null){
			testCompileSourceRoots = Collections.singletonList(sourceDirectory);
		}
		getLog().debug("source folders: " + testCompileSourceRoots);
		compile(xtend2BatchCompiler, testClassPath, testCompileSourceRoots, docOutputDirectory);
	}
	
	private void generateDoc(ResourceSet resourceSet, Injector injector, Executable2ResultMapping resultMapping) throws MojoExecutionException {
		JnarioDocCompiler docCompiler = injector.getInstance(JnarioDocCompiler.class);
		docCompiler.setResourceSet(resourceSet);
		docCompiler.setExecutable2ResultMapping(resultMapping);
		compileTestSources(docCompiler);
	}

	private ResourceSet createResourceSet() {
		Provider<ResourceSet> resourceSetProvider = new MavenProjectResourceSetProvider(project);
		return resourceSetProvider.get();
	}

	private List<Injector> createInjectors(ISetup... setups) {
		return transform(asList(setups), new Function<ISetup, Injector>() {
			public Injector apply(ISetup input) {
				return input.createInjectorAndDoEMFRegistration();
			}
		});
	}
	
}