package com.arif.impl;

import org.junit.Test;

public class JiraServiceImplTest {

	@Test
	public void test_getJiraIssues() {
		JiraServiceImpl jiraServiceImpl = new JiraServiceImpl();
		jiraServiceImpl.getJiraIssues("am046752");
	}
}
