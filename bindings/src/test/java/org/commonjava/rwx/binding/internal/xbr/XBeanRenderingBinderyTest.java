/**
 * Copyright (C) 2010 Red Hat, Inc. (jdcasey@commonjava.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.rwx.binding.internal.xbr;

import org.commonjava.rwx.binding.error.BindException;
import org.commonjava.rwx.binding.testutil.ComposedPersonResponse;
import org.commonjava.rwx.binding.testutil.ComposedPersonResponse3;
import org.commonjava.rwx.binding.testutil.ComposedPersonResponseWithFinalFields;
import org.commonjava.rwx.binding.testutil.InheritedPersonRequest;
import org.commonjava.rwx.binding.testutil.SimpleAddress;
import org.commonjava.rwx.binding.testutil.SimpleAddressMapResponse;
import org.commonjava.rwx.binding.testutil.SimpleConverterRequest;
import org.commonjava.rwx.binding.testutil.SimpleFinalFieldAddress;
import org.commonjava.rwx.binding.testutil.SimpleListRequest;
import org.commonjava.rwx.binding.testutil.SimplePersonRequest;
import org.commonjava.rwx.binding.testutil.TestObject;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.error.XmlRpcFaultException;
import org.commonjava.rwx.estream.model.Event;
import org.commonjava.rwx.estream.model.ResponseEvent;
import org.commonjava.rwx.impl.estream.EventStreamGeneratorImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore( "These tests rely on hand-scripted events, which are incredibly brittle and hard to get right" )
public class XBeanRenderingBinderyTest
{

    @Test
    public void simpleRequest()
        throws XmlRpcException
    {
        assertBindings( new SimplePersonRequest() );
    }

    @Test
    public void simpleConverterRequest()
        throws XmlRpcException, ParseException
    {
        assertBindings( new SimpleConverterRequest() );
    }

    @Test
    public void simpleListRequest()
        throws XmlRpcException
    {
        assertBindings( new SimpleListRequest() );
    }

    @Test
    public void simpleAddressMapResponse()
        throws XmlRpcException
    {
        assertBindings( new SimpleAddressMapResponse() );
    }

    @Test( expected = BindException.class )
    public void invalidEntryPoint()
        throws XmlRpcException
    {
        final SimpleAddress address = new SimpleAddress();
        final XBeanRenderingBindery bindery = new XBeanRenderingBindery( address.recipes() );
        bindery.parse( new EventStreamGeneratorImpl( address.events() ), SimpleAddress.class );
    }

    @Test( expected = XmlRpcFaultException.class )
    public void fault()
        throws XmlRpcException
    {
        final XBeanRenderingBindery bindery = new XBeanRenderingBindery( new SimplePersonRequest().recipes() );

        final List<Event<?>> events = new ArrayList<Event<?>>(
                Arrays.asList( new ResponseEvent( true ), new ResponseEvent( 101, "test fault" ),
                               new ResponseEvent( false ) ) );

        bindery.parse( new EventStreamGeneratorImpl( events ), SimplePersonRequest.class );
    }

    @Test
    public void requestWithInheritedFields()
        throws XmlRpcException
    {
        assertBindings( new InheritedPersonRequest() );
    }

    @Test
    public void responseWithStructParam()
        throws XmlRpcException
    {
        assertBindings( new ComposedPersonResponse() );
    }

    @Test
    public void responseWithArrayParam()
        throws XmlRpcException
    {
        assertBindings( new ComposedPersonResponse3() );
    }

    @Test
    public void responseWithConstructorAnnotations()
        throws XmlRpcException
    {
        assertBindings( new ComposedPersonResponseWithFinalFields( "foo", "foo@nowhere.com",
                                                                   new SimpleFinalFieldAddress( "123 Sesame St",
                                                                                                "Little Big Fork",
                                                                                                "NV", "01234" ) ) );
    }

    @SuppressWarnings( "unchecked" )
    private <T extends TestObject> void assertBindings( final T object )
        throws XmlRpcException
    {
        final XBeanRenderingBindery bindery = new XBeanRenderingBindery( object.recipes() );
        final T result = (T) bindery.parse( new EventStreamGeneratorImpl( object.events() ), object.getClass() );

        assertEquals( object, result );
    }

}
