/*
 *  Copyright (c) 2010 Red Hat, Inc.
 *  
 *  This program is licensed to you under Version 3 only of the GNU
 *  General Public License as published by the Free Software 
 *  Foundation. This program is distributed in the hope that it will be 
 *  useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 *  PURPOSE.
 *  
 *  See the GNU General Public License Version 3 for more details.
 *  You should have received a copy of the GNU General Public License 
 *  Version 3 along with this program. 
 *  
 *  If not, see http://www.gnu.org/licenses/.
 */

package org.commonjava.rwx.impl.estream;

import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.impl.estream.model.ArrayEvent;
import org.commonjava.rwx.impl.estream.model.Event;
import org.commonjava.rwx.impl.estream.model.ParameterEvent;
import org.commonjava.rwx.impl.estream.model.RequestEvent;
import org.commonjava.rwx.impl.estream.model.ResponseEvent;
import org.commonjava.rwx.impl.estream.model.StructEvent;
import org.commonjava.rwx.impl.estream.model.ValueEvent;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.EventType;
import org.commonjava.rwx.vocab.ValueType;

import java.util.ArrayList;
import java.util.List;

public class EventStreamParser
    implements XmlRpcListener
{

    private final List<Event<?>> events = new ArrayList<Event<?>>();

    public List<Event<?>> getEvents()
    {
        return events;
    }

    @Override
    public EventStreamParser arrayElement( final int index, final Object value, final ValueType type )
    {
        events.add( new ArrayEvent( index, value, type ) );
        return this;
    }

    @Override
    public EventStreamParser endArray()
    {
        events.add( new ArrayEvent( EventType.END_ARRAY ) );
        return this;
    }

    @Override
    public EventStreamParser endParameter()
    {
        events.add( new ParameterEvent() );
        return this;
    }

    @Override
    public EventStreamParser endRequest()
    {
        events.add( new RequestEvent( false ) );
        return this;
    }

    @Override
    public EventStreamParser endResponse()
    {
        events.add( new ResponseEvent( false ) );
        return this;
    }

    @Override
    public EventStreamParser endStruct()
    {
        events.add( new StructEvent( EventType.END_STRUCT ) );
        return this;
    }

    @Override
    public EventStreamParser fault( final int code, final String message )
    {
        events.add( new ResponseEvent( code, message ) );
        return this;
    }

    @Override
    public EventStreamParser parameter( final int index, final Object value, final ValueType type )
    {
        events.add( new ParameterEvent( index, value, type ) );
        return this;
    }

    @Override
    public EventStreamParser requestMethod( final String methodName )
    {
        events.add( new RequestEvent( methodName ) );
        return this;
    }

    @Override
    public EventStreamParser startArray()
    {
        events.add( new ArrayEvent( EventType.START_ARRAY ) );
        return this;
    }

    @Override
    public EventStreamParser startParameter( final int index )
    {
        events.add( new ParameterEvent( index ) );
        return this;
    }

    @Override
    public EventStreamParser startRequest()
    {
        events.add( new RequestEvent( true ) );
        return this;
    }

    @Override
    public EventStreamParser startResponse()
    {
        events.add( new ResponseEvent( true ) );
        return this;
    }

    @Override
    public EventStreamParser startStruct()
    {
        events.add( new StructEvent( EventType.START_STRUCT ) );
        return this;
    }

    @Override
    public EventStreamParser structMember( final String key, final Object value, final ValueType type )
    {
        events.add( new StructEvent( key, value, type ) );
        return this;
    }

    @Override
    public EventStreamParser endArrayElement()
        throws XmlRpcException
    {
        events.add( new ArrayEvent( EventType.END_ARRAY_ELEMENT ) );
        return this;
    }

    @Override
    public EventStreamParser endStructMember()
        throws XmlRpcException
    {
        events.add( new StructEvent( EventType.END_STRUCT_MEMBER ) );
        return this;
    }

    @Override
    public EventStreamParser startArrayElement( final int index )
        throws XmlRpcException
    {
        events.add( new ArrayEvent( index ) );
        return this;
    }

    @Override
    public EventStreamParser startStructMember( final String key )
        throws XmlRpcException
    {
        events.add( new StructEvent( key ) );
        return this;
    }

    @Override
    public EventStreamParser value( final Object value, final ValueType type )
        throws XmlRpcException
    {
        events.add( new ValueEvent( value, type ) );
        return this;
    }

}