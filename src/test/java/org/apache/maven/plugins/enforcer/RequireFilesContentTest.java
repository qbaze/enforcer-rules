package org.apache.maven.plugins.enforcer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;


public class RequireFilesContentTest {
    @Test
    public void scanFileThatDoesntContainRequiredContent() throws Exception {
        RequireFilesContent ffc = new RequireFilesContent();

        assertFalse(ffc.scanFile(new File("src/it/RequireFilesContent/content-not-found/src/foo/bar"), new String[] { "fasola", "groch", "marchewka" }).successful);
    }

    @Test
    public void scanFileThatDoesContainRequiredContent() throws Exception {
        RequireFilesContent ffc = new RequireFilesContent();

        assertTrue(ffc.scanFile(new File("src/it/RequireFilesContent/content-not-found/src/foo/bar"), new String[] { "i'm", "inside" }).successful);
    }
}
