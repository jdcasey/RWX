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

import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.estream.EventStreamParser;
import org.commonjava.rwx.estream.model.ArrayEvent;
import org.commonjava.rwx.estream.model.Event;
import org.commonjava.rwx.estream.model.ParameterEvent;
import org.commonjava.rwx.estream.model.RequestEvent;
import org.commonjava.rwx.estream.model.ResponseEvent;
import org.commonjava.rwx.estream.model.StructEvent;
import org.commonjava.rwx.estream.model.ValueEvent;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.EventType;
import org.commonjava.rwx.vocab.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EventStreamParserImpl
    implements EventStreamParser
{

    private final List<Event<?>> events = new ArrayList<Event<?>>();

    private final XmlRpcListener delegate;

    public EventStreamParserImpl()
    {
        this.delegate = null;
    }

    public EventStreamParserImpl( XmlRpcListener delegate )
    {
        this.delegate = delegate;
    }

    @Override
    public List<Event<?>> getEvents()
    {
        return events;
    }

    @Override
    public void clearEvents()
    {
        events.clear();
    }

    private void withDelegate( DelegateOp<XmlRpcListener> operation )
            throws XmlRpcException
    {
        if ( delegate != null )
        {
            Logger logger = LoggerFactory.getLogger( getClass() );
            logger.trace( "Applying operation to delegate: {}", delegate );
            operation.execute( delegate );
        }
    }

    private void addEvent( Event<?> event )
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.debug( "{}", event );
        events.add( event );
    }

    @Override
    public EventStreamParserImpl arrayElement( final int index, final Object value, final ValueType type )
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.arrayElement( index, value, type ) );
        addEvent( new ArrayEvent( index, value, type ) );

        return this;
    }

    @Override
    public EventStreamParserImpl endArray()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.endArray() );
        addEvent( new ArrayEvent( EventType.END_ARRAY ) );
        return this;
    }

    @Override
    public EventStreamParserImpl endParameter()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.endParameter() );
        addEvent( new ParameterEvent() );
        return this;
    }

    @Override
    public EventStreamParserImpl endRequest()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.endRequest() );
        addEvent( new RequestEvent( false ) );
        return this;
    }

    @Override
    public EventStreamParserImpl endResponse()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.endResponse() );
        addEvent( new ResponseEvent( false ) );
        return this;
    }

    @Override
    public EventStreamParserImpl endStruct()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.endStruct() );
        addEvent( new StructEvent( EventType.END_STRUCT ) );
        return this;
    }

    @Override
    public EventStreamParserImpl fault( final int code, final String message )
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.fault( code, message ) );
        addEvent( new ResponseEvent( code, message ) );
        return this;
    }

    @Override
    public EventStreamParserImpl parameter( final int index, final Object value, final ValueType type )
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.parameter( index, value, type ) );
        addEvent( new ParameterEvent( index, value, type ) );
        return this;
    }

    @Override
    public EventStreamParserImpl requestMethod( final String methodName )
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.requestMethod( methodName ) );
        addEvent( new RequestEvent( methodName ) );
        return this;
    }

    @Override
    public EventStreamParserImpl startArray()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.startArray() );
        addEvent( new ArrayEvent( EventType.START_ARRAY ) );
        return this;
    }

    @Override
    public EventStreamParserImpl startParameter( final int index )
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.startParameter( index ) );
        addEvent( new ParameterEvent( index ) );
        return this;
    }

    @Override
    public EventStreamParserImpl startRequest()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.startRequest() );
        addEvent( new RequestEvent( true ) );
        return this;
    }

    @Override
    public EventStreamParserImpl startResponse()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.startResponse() );
        addEvent( new ResponseEvent( true ) );
        return this;
    }

    @Override
    public EventStreamParserImpl startStruct()
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.startStruct() );
        addEvent( new StructEvent( EventType.START_STRUCT ) );
        return this;
    }

    @Override
    public EventStreamParserImpl structMember( final String key, final Object value, final ValueType type )
            throws XmlRpcException
    {
        withDelegate( ( d ) -> d.structMember( key, value, type ) );
        addEvent( new StructEvent( key, value, type ) );
        return this;
    }

    @Override
    public EventStreamParserImpl endArrayElement()
        throws XmlRpcException
    {
        withDelegate( ( d ) -> d.endArrayElement() );
        addEvent( new ArrayEvent( EventType.END_ARRAY_ELEMENT ) );
        return this;
    }

    @Override
    public EventStreamParserImpl endStructMember()
        throws XmlRpcException
    {
        withDelegate( ( d ) -> d.endStructMember() );
        addEvent( new StructEvent( EventType.END_STRUCT_MEMBER ) );
        return this;
    }

    @Override
    public EventStreamParserImpl startArrayElement( final int index )
        throws XmlRpcException
    {
        withDelegate( ( d ) -> d.startArrayElement( index ) );
        addEvent( new ArrayEvent( index ) );
        return this;
    }

    @Override
    public EventStreamParserImpl startStructMember( final String key )
        throws XmlRpcException
    {
        withDelegate( ( d ) -> d.startStructMember( key ) );
        addEvent( new StructEvent( key ) );
        return this;
    }

    @Override
    public EventStreamParserImpl value( final Object value, final ValueType type )
        throws XmlRpcException
    {
        withDelegate( ( d ) -> d.value( value, type ) );
        addEvent( new ValueEvent( value, type ) );
        return this;
    }

    public CharSequence renderEventTree()
    {
        StringBuilder sb = new StringBuilder();
        final AtomicInteger indent = new AtomicInteger( 0 );
        events.forEach( (event)->{
            sb.append('\n');
            int ident = indent.get();

            switch ( event.getEventType() )
            {
                case END_ARRAY:
                case END_ARRAY_ELEMENT:
                case END_PARAMETER:
                case END_REQUEST:
                case END_RESPONSE:
                case END_STRUCT:
                case END_STRUCT_MEMBER:
                {
                    ident--;
                    indent.decrementAndGet();
                    break;
                }

                case START_ARRAY:
                case START_ARRAY_ELEMENT:
                case START_PARAMETER:
                case START_REQUEST:
                case START_RESPONSE:
                case START_STRUCT:
                case START_STRUCT_MEMBER:
                {
                    indent.incrementAndGet();
                    break;
                }
            }

            for(int i=0; i<ident; i++)
            {
                sb.append( "  " );
            }
            sb.append( event );
        } );

        return sb;
    }

    private interface DelegateOp<T>
    {
        void execute(XmlRpcListener delegate) throws XmlRpcException;
    }
}
