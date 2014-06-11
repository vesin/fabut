package eu.execom.fabut;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public class ReportTest extends AbstractFabutRepositoryAssertTest {
	private static final String TEST = "test";

	@Before
	public void before() {
		System.out.println("Start test!!");
	}

	@After
	public void after() {
		System.out.println("After test!!");
	}

}
