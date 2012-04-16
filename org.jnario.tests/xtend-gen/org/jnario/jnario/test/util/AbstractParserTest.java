package org.jnario.jnario.test.util;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.jnario.jnario.test.util.ClassPathUriProviderBuilder;
import org.jnario.jnario.test.util.IUriProvider;
import org.jnario.jnario.test.util.ModelStore;
import org.jnario.jnario.test.util.Resources;
import org.junit.Test;

@SuppressWarnings("all")
public class AbstractParserTest {
  @Inject
  private ModelStore _modelStore;
  
  @Inject
  private FileExtensionProvider fileExtensionProvider;
  
  @Test
  public void shouldParseAllFilesWithoutParseError() {
    Class<?> _context = this.context();
    boolean _equals = Objects.equal(_context, null);
    if (_equals) {
      return;
    }
    Class<?> _context_1 = this.context();
    ClassPathUriProviderBuilder _startingFrom = ClassPathUriProviderBuilder.startingFrom(_context_1);
    final Function1<URI,Boolean> _function = new Function1<URI,Boolean>() {
        public Boolean apply(final URI it) {
          boolean _onlySpecFiles = AbstractParserTest.this.onlySpecFiles(it);
          return _onlySpecFiles;
        }
      };
    IUriProvider _select = _startingFrom.select(new Predicate<URI>() {
        public boolean apply(URI input) {
          return _function.apply(input);
        }
    });
    this._modelStore.load(_select);
    List<Resource> _resources = this._modelStore.resources();
    final Function1<Resource,Boolean> _function_1 = new Function1<Resource,Boolean>() {
        public Boolean apply(final Resource it) {
          URI _uRI = it.getURI();
          boolean _onlySpecFiles = AbstractParserTest.this.onlySpecFiles(_uRI);
          return Boolean.valueOf(_onlySpecFiles);
        }
      };
    Iterable<Resource> _filter = IterableExtensions.<Resource>filter(_resources, _function_1);
    final ArrayList<Resource> specs = Lists.<Resource>newArrayList(_filter);
    final Procedure1<Resource> _function_2 = new Procedure1<Resource>() {
        public void apply(final Resource resource) {
          Resources.checkForParseErrors(resource);
        }
      };
    IterableExtensions.<Resource>forEach(specs, _function_2);
  }
  
  public Class<?> context() {
    return null;
  }
  
  public boolean onlySpecFiles(final URI uri) {
    String _fileExtension = uri.fileExtension();
    return this.fileExtensionProvider.isValid(_fileExtension);
  }
  
  public String errorMessage(final Iterable<Issue> issues) {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder result = _stringBuilder;
    for (final Issue issue : issues) {
      {
        final StringBuilder issueBuilder = this.createIssueMessage(issue);
        result.append(issueBuilder);
      }
    }
    return result.toString();
  }
  
  public StringBuilder createIssueMessage(final Issue issue) {
    URI _uriToProblem = issue.getUriToProblem();
    final URI resourceUri = _uriToProblem.trimFragment();
    StringBuilder _stringBuilder = new StringBuilder("\n");
    final StringBuilder issueBuilder = _stringBuilder;
    Severity _severity = issue.getSeverity();
    StringBuilder _append = issueBuilder.append(_severity);
    _append.append(": \t");
    String _lastSegment = resourceUri.lastSegment();
    StringBuilder _append_1 = issueBuilder.append(_lastSegment);
    _append_1.append(" - ");
    boolean _isFile = resourceUri.isFile();
    if (_isFile) {
      String _fileString = resourceUri.toFileString();
      issueBuilder.append(_fileString);
    }
    StringBuilder _append_2 = issueBuilder.append("\n");
    Integer _lineNumber = issue.getLineNumber();
    StringBuilder _append_3 = _append_2.append(_lineNumber);
    StringBuilder _append_4 = _append_3.append(": ");
    String _message = issue.getMessage();
    _append_4.append(_message);
    return issueBuilder;
  }
}