package com.dmsg;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Unit test for simple
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws UnknownHostException {

        InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostAddress().toString();//获得本机IP
        System.out.println(ip);
        System.out.println(addr.getHostName());


    }
}
