package org.apache.maven.plugins.enforcer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.codehaus.plexus.util.DirectoryScanner;

import com.google.common.base.Preconditions;

abstract class AbstractFileContentRule extends AbstractNonCacheableEnforcerRule {

	/**
	 * The content to check against.
	 */
	public String[] contents;

	/**
	 * Directory to look
	 */
	public File baseDir;

	/**
	 * Include in scanning. Supports ant wildcards.
	 */
	public String[] includes;

	/**
	 * Exclude from scanning. Supports ant wildcards.
	 */
	public String[] excludes;

	/**
	 * Files to be checked.
	 * 
	 * @deprecated use includes/excludes instead
	 */
	@Deprecated
	public File[] files = new File[] {};

	/**
	 * Content to be checked.
	 * 
	 * @deprecated use contents instead
	 */
	@Deprecated
	public String content;

	/**
	 * If null file handles should be allowed. If they are allowed, it means
	 * treat it as a success.
	 */
	public boolean allowNulls = false;

	/**
	 * If set to true the rule will fail for a file once the forbidden phrase is
	 * found in file. It will not check the rest.
	 */
	private boolean failFast;

	@Override
	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		// Preconditions.checkNotNull(includes, "includes are mandatory");
		// Preconditions.checkNotNull(contents, "contents is mandatory");
		Preconditions.checkArgument(includes != null && includes.length > 0
				|| files != null && files.length > 0,
				"at least 1 include or file must be specified");
		Preconditions.checkArgument(content != null && !content.isEmpty()
				|| contents != null && contents.length > 0,
				"at least 1 content string must be specified");

		String dir = (baseDir == null ? System.getProperty("user.dir")
				: baseDir.toString()) + File.separatorChar;

		List<File> includedFiles;

		if (includes != null && includes.length > 0) {
			includedFiles = calculateIncludedFiles(dir, includes, excludes);
		} else {
			includedFiles = new ArrayList<File>();
		}
		
		// TODO: remove after drop
		if (files != null){
			includedFiles.addAll(Arrays.asList(files));
		}

		List<Result> failures = new ArrayList<Result>();

		for (File file : includedFiles) {
			if (file == null) {
				if (!allowNulls) {
					failures.add(Result
							.fail(file,
									"Empty file name was given and allowNulls is set to false."));
				}
			} else {
				Result result = checkFile(helper, file);

				if (!result.successful) {
					failures.add(result);
				}
			}
		}

		if (!failures.isEmpty()) {
			StringBuilder sb = new StringBuilder();

			for (Result failure : failures) {
				sb.append(failure.errorMessage).append('\n');
			}

			throw new EnforcerRuleException(
					sb.toString()
							+ "Some files produce errors, please check the error message for the individual file above.");
		}
	}

	private List<File> calculateIncludedFiles(String dir, String[] includes,
			String[] excludes) {
		DirectoryScanner ds = new DirectoryScanner();

		ds.setBasedir(dir);
		ds.setIncludes(includes);
		ds.setExcludes(excludes);
		ds.scan();

		List<File> includedFiles = new ArrayList<File>(
				ds.getIncludedFiles().length);

		for (String includedFile : ds.getIncludedFiles()) {
			includedFiles.add(new File(ds.getBasedir(), includedFile));
		}

		return includedFiles;
	}

	private Result checkFile(EnforcerRuleHelper helper, File file) {
		if (file.isFile()) {
			try {
				return scanFile(file, contents);
			} catch (FileNotFoundException e) {
				return Result.fail(file, "not found");
			} catch (IOException e) {
				helper.getLog().error(e);

				return Result.fail(file,
						"IOException was thrown, please check the log.");
			}
		}

		return Result.fail(file, "Not a file");
	}

	/**
	 * The checkFile() result object.
	 */
	protected static class Result {

		/**
		 * True if the check result is successful; otherwise false.
		 */
		public boolean successful;
		/**
		 * The error message if the result is NOT successful; otherwise empty
		 * string.
		 */
		public String errorMessage = "";

		/**
		 * Creates successful result object.
		 * 
		 * @return the newly created result object
		 */
		public static Result success(File file) {
			return new Result(file, true, "");
		}

		/**
		 * Creates successful result object.
		 * 
		 * @param file
		 * 
		 * @return the newly created result object
		 */
		public static Result fail(File file, String errorMessage) {
			return new Result(file, false, errorMessage);
		}

		/**
		 * @param successful
		 *            true if the check result is successful; otherwise false
		 * @param errorMessage
		 *            the error message if the result is NOT successful;
		 *            otherwise empty string
		 */
		private Result(final File file, final boolean successful,
				final String errorMessage) {
			this.successful = successful;
			this.errorMessage = file.getAbsolutePath() + " : " + errorMessage;
		}
	}

	public AbstractFileContentRule() {
		super();
	}

	protected abstract Result scanFile(File file, String[] contents)
			throws IOException;

	public boolean isFailFast() {
		return failFast;
	}

	public void setFailFast(boolean failFast) {
		this.failFast = failFast;
	}

}