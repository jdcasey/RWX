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
package org.commonjava.rwx.impl.estream;

import static org.commonjava.rwx.impl.estream.testutil.EventAssertions.assertEvents;

import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.estream.model.ArrayEvent;
import org.commonjava.rwx.estream.model.Event;
import org.commonjava.rwx.estream.model.ParameterEvent;
import org.commonjava.rwx.estream.model.RequestEvent;
import org.commonjava.rwx.estream.model.ResponseEvent;
import org.commonjava.rwx.estream.model.StructEvent;
import org.commonjava.rwx.estream.model.ValueEvent;
import org.commonjava.rwx.impl.estream.EventStreamParserImpl;
import org.commonjava.rwx.impl.estream.testutil.ExtList;
import org.commonjava.rwx.vocab.EventType;
import org.commonjava.rwx.vocab.ValueType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EventStreamParserTest
{

    @Test
    public void simpleRequest()
            throws XmlRpcException
    {
        final List<Event<?>> check =
            new ExtList<Event<?>>().with( new RequestEvent( true ) )
                                   .with( new RequestEvent( "foo" ) )
                                   .with( new RequestEvent( false ) );

        final EventStreamParserImpl parser = new EventStreamParserImpl();
        parser.startRequest().requestMethod( "foo" ).endRequest();

        assertEvents( check, parser.getEvents() );
    }

    @Test
    public void requestWithOneParam()
        throws XmlRpcException
    {
        final List<Event<?>> check =
            new ExtList<Event<?>>().with( new RequestEvent( true ) )
                                   .with( new RequestEvent( "foo" ) )
                                   .with( new ParameterEvent( 0 ) )
                                   .with( new ValueEvent( "foo", ValueType.STRING ) )
                                   .with( new ParameterEvent( 0, "foo", ValueType.STRING ) )
                                   .with( new ParameterEvent() )
                                   .with( new RequestEvent( false ) );

        final EventStreamParserImpl parser = new EventStreamParserImpl();
        parser.startRequest()
              .requestMethod( "foo" )
              .startParameter( 0 )
              .value( "foo", ValueType.STRING )
              .parameter( 0, "foo", ValueType.STRING )
              .endParameter()
              .endRequest();

        assertEvents( check, parser.getEvents() );
    }

    @Test
    public void simpleResponse()
            throws XmlRpcException
    {
        final List<Event<?>> check =
            new ExtList<Event<?>>().with( new ResponseEvent( true ) )
                                   .with( new ResponseEvent( 101, "foo" ) )
                                   .with( new ResponseEvent( false ) );

        final EventStreamParserImpl parser = new EventStreamParserImpl();
        parser.startResponse().fault( 101, "foo" ).endResponse();

        assertEvents( check, parser.getEvents() );
    }

    @Test
    public void simpleOneElementArray()
        throws XmlRpcException
    {
        final List<Event<?>> check =
            new ExtList<Event<?>>().with( new ArrayEvent( EventType.START_ARRAY ) )
                                   .with( new ArrayEvent( 0 ) )
                                   .with( new ValueEvent( "foo", ValueType.STRING ) )
                                   .with( new ArrayEvent( 0, "foo", ValueType.STRING ) )
                                   .with( new ArrayEvent( EventType.END_ARRAY_ELEMENT ) )
                                   .with( new ArrayEvent( EventType.END_ARRAY ) );

        final EventStreamParserImpl parser = new EventStreamParserImpl();
        parser.startArray()
              .startArrayElement( 0 )
              .value( "foo", ValueType.STRING )
              .arrayElement( 0, "foo", ValueType.STRING )
              .endArrayElement()
              .endArray();

        assertEvents( check, parser.getEvents() );
    }

    @Test
    public void simpleOneMemberStruct()
        throws XmlRpcException
    {
        final List<Event<?>> check =
            new ExtList<Event<?>>().with( new StructEvent( EventType.START_STRUCT ) )
                                   .with( new StructEvent( "key" ) )
                                   .with( new ValueEvent( "foo", ValueType.STRING ) )
                                   .with( new StructEvent( "key", "foo", ValueType.STRING ) )
                                   .with( new StructEvent( EventType.END_STRUCT_MEMBER ) )
                                   .with( new StructEvent( EventType.END_STRUCT ) );

        final EventStreamParserImpl parser = new EventStreamParserImpl();
        parser.startStruct()
              .startStructMember( "key" )
              .value( "foo", ValueType.STRING )
              .structMember( "key", "foo", ValueType.STRING )
              .endStructMember()
              .endStruct();

        assertEvents( check, parser.getEvents() );
    }

    @Test
    public void simpleStructWithEmbeddedArray()
        throws XmlRpcException
    {
        final List<Event<?>> check =
            new ExtList<Event<?>>().with( new StructEvent( EventType.START_STRUCT ) )
                                   .with( new StructEvent( "key" ) )
                                   .with( new ArrayEvent( EventType.START_ARRAY ) )
                                   .with( new ArrayEvent( 0 ) )
                                   .with( new ValueEvent( "foo", ValueType.STRING ) )
                                   .with( new ArrayEvent( 0, "foo", ValueType.STRING ) )
                                   .with( new ArrayEvent( EventType.END_ARRAY_ELEMENT ) )
                                   .with( new ArrayEvent( EventType.END_ARRAY ) )
                                   .with( new StructEvent( "key", Arrays.asList( "foo" ), ValueType.ARRAY ) )
                                   .with( new StructEvent( EventType.END_STRUCT_MEMBER ) )
                                   .with( new StructEvent( EventType.END_STRUCT ) );

        final EventStreamParserImpl parser = new EventStreamParserImpl();
        parser.startStruct()
              .startStructMember( "key" )
              .startArray()
              .startArrayElement( 0 )
              .value( "foo", ValueType.STRING )
              .arrayElement( 0, "foo", ValueType.STRING )
              .endArrayElement()
              .endArray()
              .structMember( "key", new ExtList<String>( "foo" ), ValueType.ARRAY )
              .endStructMember()
              .endStruct();

        assertEvents( check, parser.getEvents() );
    }

}
