/*
 * Copyright (c) 2013 Wisen Tanasa
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.apache.maven.plugins.enforcer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This rule verifies if the files listed contains the specified content. It
 * doesn't extends <tt>AbstractRequireFiles</tt> because it doesn't handle
 * multiple file's error messages as intended.
 * 
 * @author ceilfors
 * @author qbaze
 */
public final class RequireFilesContent extends AbstractFileContentRule {
	private String errorMsg = "Required content not found: \"%s\"";

	@Override
	protected Result scanFile(File file, String[] contents) throws IOException {
		Set<String> contentSet = new HashSet<String>(Arrays.asList(contents));

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				Iterator<String> iterator = contentSet.iterator();

				while (iterator.hasNext()) {
					String string = iterator.next();

					if (line.contains(string)) {
						iterator.remove();
					} else if (isFailFast()) {
						return Result.fail(file, String.format(errorMsg, string));
					}
				}
			}

			if (contentSet.isEmpty()) {
				return Result.success(file);
			}

			return Result.fail(file, String.format(errorMsg, contentSet.toString()));
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}
