package org.jnario.ui.contentassist;

import static java.util.Collections.emptyList;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.xtend.core.xtend.XtendClass;
import org.eclipse.xtend.core.xtend.XtendFile;
import org.eclipse.xtext.common.types.xtext.ui.JdtTypesProposalProvider;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal.IReplacementTextApplier;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.xbase.conversion.XbaseQualifiedNameValueConverter;
import org.eclipse.xtext.xtype.XImportDeclaration;

import com.google.inject.Inject;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class ImportingTypesProposalProvider extends JdtTypesProposalProvider {

	@Inject
	private XbaseQualifiedNameValueConverter qualifiedNameValueConverter;
	
	@Override
	protected IReplacementTextApplier createTextApplier(ContentAssistContext context, IScope typeScope, 
			IQualifiedNameConverter qualifiedNameConverter, IValueConverter<String> valueConverter) {
		if (context.getCurrentModel() instanceof XImportDeclaration)
			return super.createTextApplier(context, typeScope, qualifiedNameConverter, valueConverter);
		return new FQNImporter(context.getResource(), context.getViewer(), typeScope, qualifiedNameConverter, valueConverter, qualifiedNameValueConverter);
	}
	
	public static class FQNImporter extends FQNShortener {
		
		private final ITextViewer viewer;
		private final XbaseQualifiedNameValueConverter importConverter;

		public FQNImporter(Resource context, ITextViewer viewer, IScope scope, IQualifiedNameConverter qualifiedNameConverter, 
				IValueConverter<String> valueConverter, XbaseQualifiedNameValueConverter importConverter) {
			super(context, scope, qualifiedNameConverter, valueConverter);
			this.viewer = viewer;
			this.importConverter = importConverter;
		}

		@Override
		public void apply(IDocument document, ConfigurableCompletionProposal proposal) throws BadLocationException {
			String proposalReplacementString = proposal.getReplacementString();
			String typeName = proposalReplacementString;
			if (valueConverter != null)
				typeName = valueConverter.toValue(proposalReplacementString, null);
			String replacementString = getActualReplacementString(proposal);
			// there is an import statement - apply computed replacementString
			if (!proposalReplacementString.equals(replacementString)) {
				String shortTypeName = replacementString;
				if (valueConverter != null)
					shortTypeName = valueConverter.toValue(replacementString, null);
				QualifiedName shortQualifiedName = qualifiedNameConverter.toQualifiedName(shortTypeName);
				if (shortQualifiedName.getSegmentCount() == 1) {
					proposal.setCursorPosition(replacementString.length());
					document.replace(proposal.getReplacementOffset(), proposal.getReplacementLength(), replacementString);
					return;
				}
			}
			// we could create an import statement if there is no conflict
			XtendFile file = (XtendFile) context.getContents().get(0);
			XtendClass clazz = (XtendClass) (file.getXtendTypes().isEmpty() ? null : file.getXtendTypes().get(0));
			
			QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(typeName);			
			if (qualifiedName.getSegmentCount() == 1) {
				// type resides in default package - no need to hassle with imports
				proposal.setCursorPosition(proposalReplacementString.length());
				document.replace(proposal.getReplacementOffset(), proposal.getReplacementLength(), proposalReplacementString);
				return;
			}
			
			IEObjectDescription description = scope.getSingleElement(qualifiedName.skipFirst(qualifiedName.getSegmentCount() - 1));
			if (description != null) {
				// there exists a conflict - insert fully qualified name
				proposal.setCursorPosition(proposalReplacementString.length());
				document.replace(proposal.getReplacementOffset(), proposal.getReplacementLength(), proposalReplacementString);
				return;
			}
			
			// Import does not introduce ambiguities - add import and insert short name
			String shortName = qualifiedName.getLastSegment();
			int topPixel = -1;
			// store the pixel coordinates to prevent the ui from flickering
			StyledText widget = viewer.getTextWidget();
			if (widget != null)
				topPixel = widget.getTopPixel();
			ITextViewerExtension viewerExtension = null;
			if (viewer instanceof ITextViewerExtension) {
				viewerExtension = (ITextViewerExtension) viewer;
				viewerExtension.setRedraw(false);
			}
			DocumentRewriteSession rewriteSession = null;
			String lineSeparator = TextUtilities.getDefaultLineDelimiter(document);
			try {
				if (document instanceof IDocumentExtension4) {
					rewriteSession = ((IDocumentExtension4) document).startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED_SMALL);
				}
				// compute import statement's offset
				int offset = 0;
				boolean startWithLineBreak = true;
				boolean endWithLineBreak = false;
				List<XImportDeclaration> imports = emptyList();
				if(file.getImportSection() != null){
					imports =file.getImportSection().getImportDeclarations(); 
				}
				
				if (imports.isEmpty()) {
					startWithLineBreak = false;
					if (clazz == null) {
						offset = document.getLength();
					} else {
						ICompositeNode node = NodeModelUtils.getNode(clazz);
						if (node == null) {
							throw new IllegalStateException("node may not be null");
						}
						offset = node.getOffset();
						endWithLineBreak = true;
					}
				} else {
					ICompositeNode node = NodeModelUtils.getNode(imports.get(imports.size() - 1));
					if (node == null) {
						throw new IllegalStateException("node may not be null");
					}
					offset = node.getOffset() + node.getLength();
				}
				offset = Math.min(proposal.getReplacementOffset(), offset);
			
				// apply short proposal
				String escapedShortname = shortName;
				if (valueConverter != null) {
					escapedShortname = valueConverter.toString(shortName);
				}
				proposal.setCursorPosition(escapedShortname.length());
				document.replace(proposal.getReplacementOffset(), proposal.getReplacementLength(), escapedShortname);
				
				// add import statement
				String importStatement = importConverter.toString(typeName);
				for (XImportDeclaration importDeclaration : imports) {
					if(importDeclaration == null || importStatement.equals(importDeclaration.getImportedNamespace())){
						return;
					}
				}
				importStatement = (startWithLineBreak ? lineSeparator + "import " : "import ") + importStatement;
				if (endWithLineBreak) {
					importStatement += lineSeparator;
					importStatement += lineSeparator;
				}
				document.replace(offset, 0, importStatement);
				proposal.setCursorPosition(proposal.getCursorPosition() + importStatement.length());
				
				// set the pixel coordinates
				if (widget != null) {
					int additionalTopPixel = 0;
					if (startWithLineBreak)
						additionalTopPixel += widget.getLineHeight();
					if (endWithLineBreak)
						additionalTopPixel += 2 * widget.getLineHeight();
					widget.setTopPixel(topPixel + additionalTopPixel);
				}
			} finally {
				if (rewriteSession != null) {
					((IDocumentExtension4) document).stopRewriteSession(rewriteSession);
				}
				if (viewerExtension != null)
					viewerExtension.setRedraw(true);
			}
		}
		
	}

}