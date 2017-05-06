package com.github.publickey.test.pojo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractPojoTesterTest extends AbstractPojoTester {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRegularPojo() throws Exception {
		super.testPojo(RegularPojo.class, "compute", "isValid");
	}

	@Test(expected = AssertionError.class)
	public void testRegularPojoBorkenSetter() throws Exception {
		super.testPojo(RegularPojoBorkenSetter.class);
	}

	@Test(expected = AssertionError.class)
	public void testRegularPojoBrokenHashCode() throws Exception {
		super.testPojo(RegularPojoBrokenHashCode.class);
	}

	@Test(expected = AssertionError.class)
	public void testRegularPojoBrokenEquals() throws Exception {
		super.testPojo(RegularPojoBrokenEquals.class);
	}

	@Test
	public void testMultiConstructorPojo() throws Exception {
		super.testPojoAllConstructors(MultiConstructorPojo.class);
	}

	@Test
	public void testImmutablePojo() throws Exception {
		super.testPojoAllConstructors(ImmutablePojo.class);
	}

	@Test
	public void testCreatorMethodPojo() throws Exception {
		super.testPojo(CreatorMethodPojo.class);
	}

	@Test
	public void testEnumPojo() throws Exception {
		super.testPojoAllConstructors(EnumPojo.class);
	}
}
