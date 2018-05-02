package org.apache.maven.plugins.enforcer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class ForbidFilesContentTest {

    @Test
    public void scanFileThatDoesntContainForbiddenContent() throws Exception {
        AbstractFileContentRule ffc = new ForbidFilesContent();

        assertTrue(ffc.scanFile(new File("src/it/ForbidFilesContent/content-not-found/src/foo/bar"), new String[] { "fasola", "groch", "marchewka" }).successful);
    }

    @Test
    public void scanFileThatDoesContainForbiddenContent() throws Exception {
        AbstractFileContentRule ffc = new ForbidFilesContent();

        assertFalse(ffc.scanFile(new File("src/it/ForbidFilesContent/content-not-found/src/foo/bar"), new String[] { "fasola", "i'm", "marchewka" }).successful);
    }
}
