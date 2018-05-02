package org.apache.maven.plugins.enforcer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This rule verifies if the files listed contains the specified content. It
 * doesn't extends <tt>AbstractRequireFiles</tt> because it doesn't handle
 * multiple file's error messages as intended.
 * 
 * @author qbaze
 */
public final class ForbidFilesContent extends AbstractFileContentRule {
	private String errorMsg = "Forbidden content found: \"%s\"";

	@Override
	protected Result scanFile(File file, String[] contents) throws IOException {
		Set<String> contentSet = new HashSet<String>(Arrays.asList(contents));
		List<String> contentFoundList = new ArrayList<String>();

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				Iterator<String> iterator = contentSet.iterator();

				while (iterator.hasNext()) {
					String string = iterator.next();

					if (line.contains(string)) {
						if (isFailFast()) {
							return Result.fail(file, String.format(
									errorMsg, string));
						}

						iterator.remove();
						contentFoundList.add(string);
					}
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		if (contentFoundList.isEmpty()) {
			return Result.success(file);
		}

		return Result.fail(file, String.format(errorMsg,
				contentFoundList.toString()));

	}

}
