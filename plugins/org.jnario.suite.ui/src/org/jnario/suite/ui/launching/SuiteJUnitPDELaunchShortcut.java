/*******************************************************************************
 * Copyright (c) 2012 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.jnario.suite.ui.launching;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

public class SuiteJUnitPDELaunchShortcut extends org.eclipse.pde.ui.launcher.JUnitWorkbenchLaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection newSelection = SuiteLaunchShortcutUtil.replaceWithJavaElementDelegates((IStructuredSelection) selection);
			super.launch(newSelection, mode);
		}
	}
	
	@Override
	public void launch(IEditorPart editor, String mode) {
		IStructuredSelection selection = SuiteLaunchShortcutUtil.toSelectionWithJavaElementDelegates(editor);
		if (selection != null) {
			launch(selection, mode);
		} else {
			super.launch(editor, mode);
		}
	}
}
