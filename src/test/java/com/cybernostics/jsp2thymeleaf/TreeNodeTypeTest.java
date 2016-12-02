package com.cybernostics.jsp2thymeleaf;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jason
 */
public class TreeNodeTypeTest
{
    
    public TreeNodeTypeTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of values method, of class TreeNodeType.
     */
    @Test
    public void testValues()
    {
        TreeNodeType nodeType = TreeNodeType.valueOf(com.cybernostics.forks.jsp2x.JspParser.JSP_EXPRESSION);
        System.out.println(nodeType);
         
    }
    
}
