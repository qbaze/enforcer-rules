package org.apache.maven.plugins.enforcer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.junit.Test;

public class AbstractFileContentRuleTest {

    //    @Test(expected = NullPointerException.class)
    //    public void failWhenExecuteWithNullContents() throws EnforcerRuleException {
    //        AbstractFileContentRule rule = mock(AbstractFileContentRule.class);
    //        doCallRealMethod().when(rule).execute(any(EnforcerRuleHelper.class));
    //
    //        rule.contents = null;
    //        rule.includes = new String[] { "/test/test" };
    //
    //        rule.execute(null);
    //    }
    //
    //    @Test(expected = NullPointerException.class)
    //    public void failWhenExecuteWithNullIncludes() throws EnforcerRuleException {
    //        AbstractFileContentRule rule = mock(AbstractFileContentRule.class);
    //        doCallRealMethod().when(rule).execute(any(EnforcerRuleHelper.class));
    //
    //        rule.contents = new String[] { "test" };
    //        rule.includes = null;
    //
    //        rule.execute(null);
    //    }

    @Test(expected = IllegalArgumentException.class)
    public void failWhenExecuteWithEmptyContents() throws EnforcerRuleException {
        AbstractFileContentRule rule = mock(AbstractFileContentRule.class);
        doCallRealMethod().when(rule).execute(any(EnforcerRuleHelper.class));

        rule.contents = new String[] {};
        rule.includes = new String[] { "/test/test" };

        rule.execute(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failWhenExecuteWithEmptyIncludes() throws EnforcerRuleException {
        AbstractFileContentRule rule = mock(AbstractFileContentRule.class);
        doCallRealMethod().when(rule).execute(any(EnforcerRuleHelper.class));

        rule.contents = new String[] { "test" };
        rule.includes = new String[] {};

        rule.execute(null);
    }

}
