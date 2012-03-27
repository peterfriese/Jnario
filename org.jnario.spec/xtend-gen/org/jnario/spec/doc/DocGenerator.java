package org.jnario.spec.doc;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.core.xtend.XtendClass;
import org.eclipse.xtend.core.xtend.XtendMember;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.jnario.ExampleColumn;
import org.jnario.ExampleRow;
import org.jnario.ExampleTable;
import org.jnario.doc.DocumentationSupport;
import org.jnario.jvmmodel.ExtendedJvmTypesBuilder;
import org.jnario.spec.doc.DocOutputConfigurationProvider;
import org.jnario.spec.doc.Filter;
import org.jnario.spec.doc.FilterExtractor;
import org.jnario.spec.doc.FilteringResult;
import org.jnario.spec.doc.WhiteSpaceNormalizer;
import org.jnario.spec.naming.ExampleNameProvider;
import org.jnario.spec.spec.Example;
import org.jnario.spec.spec.ExampleGroup;
import org.jnario.spec.spec.SpecFile;
import org.jnario.spec.util.Strings;
import org.pegdown.PegDownProcessor;

@SuppressWarnings("all")
public class DocGenerator implements IGenerator {
  @Inject
  private ExampleNameProvider _exampleNameProvider;
  
  @Inject
  private ExtendedJvmTypesBuilder _extendedJvmTypesBuilder;
  
  @Inject
  private WhiteSpaceNormalizer _whiteSpaceNormalizer;
  
  @Inject
  private PegDownProcessor _pegDownProcessor;
  
  @Inject
  private FilterExtractor _filterExtractor;
  
  private List<String> cssFiles = new Function0<List<String>>() {
    public List<String> apply() {
      ArrayList<String> _newArrayList = CollectionLiterals.<String>newArrayList("bootstrap.min.css", "bootstrap-responsive.min.css", "custom.css", "prettify.css");
      return _newArrayList;
    }
  }.apply();
  
  private List<String> jsFiles = new Function0<List<String>>() {
    public List<String> apply() {
      ArrayList<String> _newArrayList = CollectionLiterals.<String>newArrayList("prettify.js", "lang-jnario.js");
      return _newArrayList;
    }
  }.apply();
  
  public void doGenerate(final Resource input, final IFileSystemAccess fsa) {
    this.copy(fsa, "css", this.cssFiles);
    this.copy(fsa, "js", this.jsFiles);
    EList<EObject> _contents = input.getContents();
    Iterable<SpecFile> _filter = Iterables.<SpecFile>filter(_contents, SpecFile.class);
    for (final SpecFile spec : _filter) {
      {
        XtendClass _xtendClass = spec.getXtendClass();
        final ExampleGroup exampleGroup = ((ExampleGroup) _xtendClass);
        boolean _notEquals = (!Objects.equal(exampleGroup, null));
        if (_notEquals) {
          String _fileName = this.fileName(exampleGroup);
          CharSequence _generate = this.generate(exampleGroup);
          fsa.generateFile(_fileName, DocOutputConfigurationProvider.DOC_OUTPUT, _generate);
        }
      }
    }
  }
  
  public String fileName(final ExampleGroup exampleGroup) {
    String _javaClassName = this._exampleNameProvider.toJavaClassName(exampleGroup);
    String _plus = ("/" + _javaClassName);
    final String fileName = (_plus + ".html");
    String _packageName = exampleGroup.getPackageName();
    boolean _equals = Objects.equal(_packageName, null);
    if (_equals) {
      return fileName;
    }
    String _packageName_1 = exampleGroup.getPackageName();
    String _replaceAll = _packageName_1.replaceAll("\\.", "/");
    String _plus_1 = ("/" + _replaceAll);
    return (_plus_1 + fileName);
  }
  
  public void copy(final IFileSystemAccess fsa, final String targetFolder, final Iterable<String> files) {
    for (final String file : files) {
      String _plus = ("/" + targetFolder);
      String _plus_1 = (_plus + "/");
      String _plus_2 = (_plus_1 + file);
      String _load = this.load(file);
      fsa.generateFile(_plus_2, DocOutputConfigurationProvider.DOC_OUTPUT, _load);
    }
  }
  
  public String load(final String file) {
    final InputStream inputStream = DocumentationSupport.class.getResourceAsStream(file);
    return Strings.convertStreamToString(inputStream);
  }
  
  public String folder(final String name, final ExampleGroup context) {
    String _root = this.root(context);
    return (_root + name);
  }
  
  public String root(final ExampleGroup exampleGroup) {
    final SpecFile specFile = EcoreUtil2.<SpecFile>getContainerOfType(exampleGroup, SpecFile.class);
    XtendClass _xtendClass = specFile.getXtendClass();
    final String packageName = _xtendClass.getPackageName();
    boolean _equals = Objects.equal(packageName, null);
    if (_equals) {
      return "";
    }
    final String[] fragments = packageName.split("\\.");
    final Function1<String,String> _function = new Function1<String,String>() {
        public String apply(final String s) {
          return "../";
        }
      };
    final List<String> path = ListExtensions.<String, String>map(((List<String>)Conversions.doWrapArray(fragments)), _function);
    return IterableExtensions.join(path, "");
  }
  
  public CharSequence generate(final ExampleGroup exampleGroup) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<!DOCTYPE html>");
    _builder.newLine();
    _builder.append("<html lang=\"en\">");
    _builder.newLine();
    _builder.append("<head>");
    _builder.newLine();
    _builder.append("<meta charset=\"utf-8\">");
    _builder.newLine();
    _builder.append("<title>");
    String _asTitle = this.asTitle(exampleGroup);
    _builder.append(_asTitle, "");
    _builder.append("</title>");
    _builder.newLineIfNotEmpty();
    _builder.append("<meta name=\"description\" content=\"\">");
    _builder.newLine();
    _builder.append("<meta name=\"author\" content=\"Jnario\">");
    _builder.newLine();
    _builder.newLine();
    _builder.append("<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->");
    _builder.newLine();
    _builder.append("<!--[if lt IE 9]>");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("<script src=\"http://html5shim.googlecode.com/svn/trunk/html5.js\"></script>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("<![endif]-->");
    _builder.newLine();
    _builder.newLine();
    _builder.append("<!-- Le styles -->");
    _builder.newLine();
    {
      for(final String cssFile : this.cssFiles) {
        _builder.append("<link rel=\"stylesheet\" href=\"");
        String _folder = this.folder("css", exampleGroup);
        _builder.append(_folder, "");
        _builder.append("/");
        _builder.append(cssFile, "");
        _builder.append("\">");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      for(final String jsFile : this.jsFiles) {
        _builder.append("<script type=\"text/javascript\" src=\"");
        String _folder_1 = this.folder("js", exampleGroup);
        _builder.append(_folder_1, "");
        _builder.append("/");
        _builder.append(jsFile, "");
        _builder.append("\"></script>");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</head>");
    _builder.newLine();
    _builder.newLine();
    _builder.append("<body onload=\"prettyPrint()\">");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("<div class=\"container\">");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("<div class=\"content\">");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("<div class=\"page-header\">");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("<h1>");
    String _asTitle_1 = this.asTitle(exampleGroup);
    _builder.append(_asTitle_1, "				");
    _builder.append("</h1>");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("</div>");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("<div class=\"row\">");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("<div class=\"span12\">");
    _builder.newLine();
    CharSequence _generateDoc = this.generateDoc(exampleGroup);
    _builder.append(_generateDoc, "");
    _builder.newLineIfNotEmpty();
    StringConcatenation _generateMembers = this.generateMembers(exampleGroup, 1);
    _builder.append(_generateMembers, "");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t\t");
    _builder.append("</div>");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("</div>");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("</div> <!-- /content -->");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("<footer>");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("<p><small>Generated by <a href=\"http://www.jnario.org\">Jnario</a>.</small></p>");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("</footer>");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("</div>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("<!-- /container -->");
    _builder.newLine();
    _builder.newLine();
    _builder.append("</body>");
    _builder.newLine();
    _builder.append("</html>");
    _builder.newLine();
    return _builder;
  }
  
  public StringConcatenation generateMembers(final ExampleGroup exampleGroup, final int level) {
    StringConcatenation _stringConcatenation = new StringConcatenation();
    final StringConcatenation result = _stringConcatenation;
    boolean inList = false;
    EList<XtendMember> _members = exampleGroup.getMembers();
    final Function1<XtendMember,Boolean> _function = new Function1<XtendMember,Boolean>() {
        public Boolean apply(final XtendMember it) {
          boolean _or = false;
          boolean _or_1 = false;
          if ((it instanceof Example)) {
            _or_1 = true;
          } else {
            _or_1 = ((it instanceof Example) || (it instanceof ExampleGroup));
          }
          if (_or_1) {
            _or = true;
          } else {
            _or = (_or_1 || (it instanceof ExampleTable));
          }
          return Boolean.valueOf(_or);
        }
      };
    final Iterable<XtendMember> members = IterableExtensions.<XtendMember>filter(_members, _function);
    for (final XtendMember member : members) {
      {
        final boolean isExampleGroup = (member instanceof ExampleGroup);
        boolean _and = false;
        if (!inList) {
          _and = false;
        } else {
          boolean _not = (!isExampleGroup);
          _and = (inList && _not);
        }
        if (_and) {
          result.append("<li>");
          CharSequence _generate = this.generate(member, level);
          result.append(_generate);
          result.append("</li>");
        } else {
          boolean _and_1 = false;
          boolean _not_1 = (!inList);
          if (!_not_1) {
            _and_1 = false;
          } else {
            boolean _not_2 = (!isExampleGroup);
            _and_1 = (_not_1 && _not_2);
          }
          if (_and_1) {
            result.append("<ul>");
            result.append("<li>");
            CharSequence _generate_1 = this.generate(member, level);
            result.append(_generate_1);
            result.append("</li>");
            inList = true;
          } else {
            boolean _and_2 = false;
            if (!inList) {
              _and_2 = false;
            } else {
              _and_2 = (inList && isExampleGroup);
            }
            if (_and_2) {
              result.append("</ul>");
              CharSequence _generate_2 = this.generate(member, level);
              result.append(_generate_2);
              inList = false;
            } else {
              boolean _and_3 = false;
              boolean _not_3 = (!inList);
              if (!_not_3) {
                _and_3 = false;
              } else {
                _and_3 = (_not_3 && isExampleGroup);
              }
              if (_and_3) {
                CharSequence _generate_3 = this.generate(member, level);
                result.append(_generate_3);
              }
            }
          }
        }
      }
    }
    if (inList) {
      result.append("</ul>");
    }
    return result;
  }
  
  public CharSequence generateDoc(final EObject eObject) {
    StringConcatenation _builder = new StringConcatenation();
    {
      String _documentation = this._extendedJvmTypesBuilder.getDocumentation(eObject);
      boolean _notEquals = (!Objects.equal(_documentation, null));
      if (_notEquals) {
        String _documentation_1 = this._extendedJvmTypesBuilder.getDocumentation(eObject);
        String _markdown2Html = this.markdown2Html(_documentation_1);
        _builder.append(_markdown2Html, "");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  protected CharSequence _generate(final XtendMember member, final int level) {
    StringConcatenation _builder = new StringConcatenation();
    return _builder;
  }
  
  protected CharSequence _generate(final Example example, final int level) {
    CharSequence _xblockexpression = null;
    {
      String docString = this._extendedJvmTypesBuilder.getDocumentation(example);
      List<Filter> filters = CollectionLiterals.<Filter>emptyList();
      boolean _notEquals = (!Objects.equal(docString, null));
      if (_notEquals) {
        final FilteringResult filterResult = this._filterExtractor.apply(docString);
        List<Filter> _filters = filterResult.getFilters();
        filters = _filters;
        String _string = filterResult.getString();
        docString = _string;
        String _markdown2Html = this.markdown2Html(docString);
        docString = _markdown2Html;
      }
      StringConcatenation _builder = new StringConcatenation();
      {
        String _name = example.getName();
        boolean _notEquals_1 = (!Objects.equal(_name, null));
        if (_notEquals_1) {
          _builder.append("<p ");
          String _name_1 = example.getName();
          String _generateId = this.generateId(_name_1);
          _builder.append(_generateId, "");
          _builder.append(">");
          _builder.newLineIfNotEmpty();
          _builder.append("<strong>");
          String _describe = this._exampleNameProvider.describe(example);
          String _convertToText = this.convertToText(_describe);
          _builder.append(_convertToText, "");
          _builder.append("</strong>");
          _builder.newLineIfNotEmpty();
        } else {
          _builder.append("<p>");
          _builder.newLine();
        }
      }
      _builder.append(docString, "");
      _builder.newLineIfNotEmpty();
      {
        boolean _and = false;
        boolean _isPending = example.isPending();
        boolean _not = (!_isPending);
        if (!_not) {
          _and = false;
        } else {
          XExpression _body = example.getBody();
          boolean _notEquals_2 = (!Objects.equal(_body, null));
          _and = (_not && _notEquals_2);
        }
        if (_and) {
          CharSequence _codeBlock = this.toCodeBlock(example, filters);
          _builder.append(_codeBlock, "");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("</p>");
      _builder.newLineIfNotEmpty();
      _xblockexpression = (_builder);
    }
    return _xblockexpression;
  }
  
  public String generateId(final String id) {
    String _replaceAll = id==null?(String)null:id.replaceAll("\\W", "_");
    String _plus = ("id=\"" + _replaceAll);
    return (_plus + "\"");
  }
  
  public CharSequence toCodeBlock(final Example example, final List<Filter> filters) {
    CharSequence _xblockexpression = null;
    {
      String prefix = "<pre class=\"prettyprint lang-spec\">";
      for (final Filter filter : filters) {
        String _apply = filter.apply(prefix);
        prefix = _apply;
      }
      XExpression _implementation = example.getImplementation();
      final String code = this.toXtendCode(_implementation, filters);
      int _length = code.length();
      boolean _equals = (_length == 0);
      if (_equals) {
        StringConcatenation _builder = new StringConcatenation();
        return _builder;
      }
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append(prefix, "");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append(code, "");
      _builder_1.append("</pre>");
      _xblockexpression = (_builder_1);
    }
    return _xblockexpression;
  }
  
  protected CharSequence _generate(final ExampleTable table, final int level) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<h4 ");
    String _fieldName = this._exampleNameProvider.toFieldName(table);
    String _generateId = this.generateId(_fieldName);
    _builder.append(_generateId, "");
    _builder.append(">");
    String _fieldName_1 = this._exampleNameProvider.toFieldName(table);
    String _convertToTitle = this.convertToTitle(_fieldName_1);
    _builder.append(_convertToTitle, "");
    _builder.append("</h4>");
    _builder.newLineIfNotEmpty();
    _builder.append("<p>");
    CharSequence _generateDoc = this.generateDoc(table);
    _builder.append(_generateDoc, "");
    _builder.append("</p>");
    _builder.newLineIfNotEmpty();
    _builder.append("<table class=\"table table-striped table-bordered table-condensed\">");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("<thead>");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("<tr>");
    _builder.newLine();
    {
      EList<ExampleColumn> _columns = table.getColumns();
      for(final ExampleColumn headingCell : _columns) {
        _builder.append("\t\t");
        _builder.append("<th>");
        String _name = headingCell.getName();
        _builder.append(_name, "		");
        _builder.append("</th>");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t\t");
    _builder.append("</tr>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("</thead>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("<tbody>");
    _builder.newLine();
    {
      EList<ExampleRow> _rows = table.getRows();
      for(final ExampleRow row : _rows) {
        _builder.append("\t");
        _builder.append("<tr>");
        _builder.newLine();
        {
          EList<XExpression> _cells = row.getCells();
          for(final XExpression cell : _cells) {
            _builder.append("\t");
            _builder.append("\t");
            _builder.append("<td>");
            List<Filter> _emptyList = CollectionLiterals.<Filter>emptyList();
            String _xtendCode = this.toXtendCode(cell, _emptyList);
            _builder.append(_xtendCode, "		");
            _builder.append("</td>");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("\t");
        _builder.append("</tr>");
        _builder.newLine();
      }
    }
    _builder.append("\t");
    _builder.append("</tbody>");
    _builder.newLine();
    _builder.append("</table>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final ExampleGroup exampleGroup, final int level) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _heading = this.heading(level);
    _builder.append(_heading, "");
    _builder.append(" ");
    String _name = exampleGroup.getName();
    String _generateId = this.generateId(_name);
    _builder.append(_generateId, "");
    _builder.append(">");
    String _asTitle = this.asTitle(exampleGroup);
    _builder.append(_asTitle, "");
    _builder.append("</");
    String _heading_1 = this.heading(level);
    _builder.append(_heading_1, "");
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    _builder.append("<p>");
    CharSequence _generateDoc = this.generateDoc(exampleGroup);
    _builder.append(_generateDoc, "");
    _builder.append("</p>");
    _builder.newLineIfNotEmpty();
    int _plus = (level + 1);
    StringConcatenation _generateMembers = this.generateMembers(exampleGroup, _plus);
    _builder.append(_generateMembers, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected String _toXtendCode(final XExpression expr, final List<Filter> filters) {
    String _serialize = this.serialize(expr);
    String _normalize = this._whiteSpaceNormalizer.normalize(_serialize);
    String _html = this.toHtml(_normalize);
    return _html.trim();
  }
  
  protected String _toXtendCode(final XBlockExpression expr, final List<Filter> filters) {
    String _serialize = this.serialize(expr);
    String code = _serialize.trim();
    for (final Filter filter : filters) {
      String _apply = filter.apply(code);
      code = _apply;
    }
    int _length = code.length();
    boolean _equals = (_length == 0);
    if (_equals) {
      return "";
    }
    int _length_1 = code.length();
    int _minus = (_length_1 - 1);
    String _substring = code.substring(1, _minus);
    code = _substring;
    String _normalize = this._whiteSpaceNormalizer.normalize(code);
    return this.toHtml(_normalize);
  }
  
  public String toHtml(final String input) {
    String _replaceAll = input.replaceAll("<", "&lt;");
    String _replaceAll_1 = _replaceAll.replaceAll(">", "&gt;");
    return _replaceAll_1;
  }
  
  public String serialize(final EObject obj) {
    final ICompositeNode node = NodeModelUtils.getNode(obj);
    return node.getText();
  }
  
  public String heading(final int level) {
    return "h3";
  }
  
  protected String _asTitle(final ExampleGroup exampleGroup) {
    String _describe = this._exampleNameProvider.describe(exampleGroup);
    String _convertToTitle = this.convertToTitle(_describe);
    return _convertToTitle;
  }
  
  protected String _asTitle(final Example example) {
    String _describe = this._exampleNameProvider.describe(example);
    String _convertToTitle = this.convertToTitle(_describe);
    return _convertToTitle;
  }
  
  public String convertToTitle(final String string) {
    String _convertToText = this.convertToText(string);
    String _firstUpper = org.eclipse.xtext.util.Strings.toFirstUpper(_convertToText);
    return _firstUpper;
  }
  
  public String convertToText(final String string) {
    String _convertFromJavaString = org.eclipse.xtext.util.Strings.convertFromJavaString(string, true);
    return _convertFromJavaString;
  }
  
  public String markdown2Html(final String string) {
    String _markdownToHtml = this._pegDownProcessor.markdownToHtml(string);
    String _replaceAll = _markdownToHtml.replaceAll("<pre><code>", "<pre class=\"prettyprint\">");
    String _replaceAll_1 = _replaceAll.replaceAll("</pre></code>", "</pre>");
    return _replaceAll_1;
  }
  
  public CharSequence generate(final XtendMember example, final int level) {
    if (example instanceof Example) {
      return _generate((Example)example, level);
    } else if (example instanceof ExampleTable) {
      return _generate((ExampleTable)example, level);
    } else if (example instanceof ExampleGroup) {
      return _generate((ExampleGroup)example, level);
    } else if (example != null) {
      return _generate(example, level);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(example, level).toString());
    }
  }
  
  public String toXtendCode(final XExpression expr, final List<Filter> filters) {
    if (expr instanceof XBlockExpression) {
      return _toXtendCode((XBlockExpression)expr, filters);
    } else if (expr != null) {
      return _toXtendCode(expr, filters);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(expr, filters).toString());
    }
  }
  
  public String asTitle(final XtendMember example) {
    if (example instanceof Example) {
      return _asTitle((Example)example);
    } else if (example instanceof ExampleGroup) {
      return _asTitle((ExampleGroup)example);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(example).toString());
    }
  }
}