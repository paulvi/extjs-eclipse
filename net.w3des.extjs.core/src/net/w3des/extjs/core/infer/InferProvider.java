package net.w3des.extjs.core.infer;

import net.w3des.extjs.core.ExtJSCore;
import net.w3des.extjs.core.container.Container;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainer;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.infer.IInferEngine;
import org.eclipse.wst.jsdt.core.infer.IInferenceFile;
import org.eclipse.wst.jsdt.core.infer.InferrenceProvider;
import org.eclipse.wst.jsdt.core.infer.RefactoringSupport;
import org.eclipse.wst.jsdt.core.infer.ResolutionConfiguration;

public class InferProvider implements InferrenceProvider {
	final public static String ID = "net.w3des.extjs.core.inference"; //$NON-NLS-1$

	public IInferEngine getInferEngine() {
		InferEngine eng = new InferEngine();
		eng.inferenceProvider=this;
		return eng;
	}

	public int applysTo(final IInferenceFile scriptFile) {
		final char[] fileName = scriptFile.getFileName();
		if (fileName == null) {
			return InferrenceProvider.NOT_THIS;
		}

		final IPath path = new Path(new String(fileName));
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IResource member = workspaceRoot.findMember(path);
		if (member == null) {
			return InferrenceProvider.NOT_THIS;
		}

		final IProject project = member.getProject();
		if (project == null) {
			return InferrenceProvider.NOT_THIS;
		}

		try {
			if (!project.hasNature(JavaScriptCore.NATURE_ID)) {
				return InferrenceProvider.NOT_THIS;
			}

			final IJavaScriptProject scriptProject = JavaScriptCore.create(project);
			if (!scriptProject.exists()) {
				return InferrenceProvider.NOT_THIS;
			}
			
			
			final IJsGlobalScopeContainer container = JavaScriptCore
					.getJsGlobalScopeContainer(new Path(Container.ID),
							scriptProject);

			if (container == null) {
				return InferrenceProvider.NOT_THIS;
			}
			
			final IIncludePathEntry[] raw = scriptProject.getRawIncludepath();
			boolean found = false;
			for (IIncludePathEntry entry : raw) {
				if (entry.getPath().segment(0).equals(Container.ID)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				return InferrenceProvider.NOT_THIS;
			}
			
			return InferrenceProvider.MAYBE_THIS;

		} catch (CoreException e) {
			ExtJSCore.error(e);
		}

		return NOT_THIS;
	}

	public String getID() {
		return ID;
	}

	public ResolutionConfiguration getResolutionConfiguration() {
		return new ResolutionConfiguration();
	}
	
	/**
	 * TODO create refactoring for ExtJS classes
	 */
	public RefactoringSupport getRefactoringSupport() {
		return new RefactoringSupport();
	}

}