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
package org.commonjava.rwx.impl;

import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TrackingXmlRpcListener
        implements XmlRpcListener
{

    private XmlRpcListener current;

    private XmlRpcListener last;

    private final XmlRpcListener root;

    public TrackingXmlRpcListener( final XmlRpcListener root )
    {
        this.root = root;
        current = root;
        last = null;
    }

    public XmlRpcListener getRoot()
    {
        return root;
    }

    public XmlRpcListener getCurrent()
    {
        return current;
    }

    public XmlRpcListener getLast()
    {
        return last;
    }

    public XmlRpcListener arrayElement( final int index, final Object value, final ValueType type )
            throws XmlRpcException
    {
        return proceed( () -> current.arrayElement( index, value, type ) );
    }

    public XmlRpcListener endArray()
            throws XmlRpcException
    {
        return proceed( () -> current.endArray() );
    }

    public XmlRpcListener endArrayElement()
            throws XmlRpcException
    {
        return proceed( () -> current.endArrayElement() );
    }

    public XmlRpcListener endParameter()
            throws XmlRpcException
    {
        return proceed( () -> current.endParameter() );
    }

    public XmlRpcListener endRequest()
            throws XmlRpcException
    {
        return proceed( () -> current.endRequest() );
    }

    public XmlRpcListener endResponse()
            throws XmlRpcException
    {
        return proceed( () -> current.endResponse() );
    }

    public XmlRpcListener endStruct()
            throws XmlRpcException
    {
        return proceed( () -> current.endStruct() );
    }

    public XmlRpcListener endStructMember()
            throws XmlRpcException
    {
        return proceed( () -> current.endStructMember() );
    }

    public XmlRpcListener fault( final int code, final String message )
            throws XmlRpcException
    {
        return proceed( () -> current.fault( code, message ) );
    }

    public XmlRpcListener parameter( final int index, final Object value, final ValueType type )
            throws XmlRpcException
    {
        return proceed( () -> current.parameter( index, value, type ) );
    }

    public XmlRpcListener requestMethod( final String methodName )
            throws XmlRpcException
    {
        return proceed( () -> current.requestMethod( methodName ) );
    }

    public XmlRpcListener startArray()
            throws XmlRpcException
    {
        return proceed( () -> current.startArray() );
    }

    public XmlRpcListener startArrayElement( final int index )
            throws XmlRpcException
    {
        return proceed( () -> current.startArrayElement( index ) );
    }

    public XmlRpcListener startParameter( final int index )
            throws XmlRpcException
    {
        return proceed( () -> current.startParameter( index ) );
    }

    public XmlRpcListener startRequest()
            throws XmlRpcException
    {
        return proceed( () -> current.startRequest() );
    }

    public XmlRpcListener startResponse()
            throws XmlRpcException
    {
        return proceed( () -> current.startResponse() );
    }

    public XmlRpcListener startStruct()
            throws XmlRpcException
    {
        return proceed( () -> current.startStruct() );
    }

    public XmlRpcListener startStructMember( final String key )
            throws XmlRpcException
    {
        return proceed( () -> current.startStructMember( key ) );
    }

    public XmlRpcListener structMember( final String key, final Object value, final ValueType type )
            throws XmlRpcException
    {
        return proceed( () -> current.structMember( key, value, type ) );
    }

    public XmlRpcListener value( final Object value, final ValueType type )
            throws XmlRpcException
    {
        return proceed( () -> current.value( value, type ) );
    }

    @Override
    public String toString()
    {
        return "TrackingXmlRpcListener@" + hashCode() + "{" + "current=" + current + ", last=" + last + ", root=" + root + '}';
    }

    private List<XmlRpcCall> calls = new ArrayList<>();

    public static final class XmlRpcCall
    {
        private StackTraceElement frame;
        private XmlRpcListener last;
        private XmlRpcListener current;
        private XmlRpcListener next;

        public XmlRpcCall( StackTraceElement frame, XmlRpcListener last, XmlRpcListener current, XmlRpcListener next )
        {
            this.frame = frame;
            this.last = last;
            this.current = current;
            this.next = next;
        }

        public StackTraceElement getFrame()
        {
            return frame;
        }

        public XmlRpcListener getLast()
        {
            return last;
        }

        public XmlRpcListener getCurrent()
        {
            return current;
        }

        public XmlRpcListener getNext()
        {
            return next;
        }

        @Override
        public String toString()
        {
            return "XmlRpcCall{\n\t" +
                    "frame=" + frame +
                    "\n\tlast=" + last +
                    "\n\tcurrent=" + current +
                    "\n\tnext=" + next +
                    "\n}";
        }
    }

    private XmlRpcListener proceed(RecordingOp<XmlRpcListener> supplier )
            throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        StackTraceElement lastFrame = null;
        if ( logger.isTraceEnabled() )
        {
            lastFrame = Thread.currentThread().getStackTrace()[2];
            logger.trace( "CALLING: {}.{}\n  Last={} ->\n  Current={}", lastFrame.getClassName(), lastFrame.getMethodName(), last,
                          current );
        }

        XmlRpcListener next = supplier.execute();

        if ( logger.isTraceEnabled() )
        {
            calls.add( new XmlRpcCall( lastFrame, last, current, next ) );

            logger.trace( "CALLED: {}.{}\n  Last={} ->\n  Current={} ->\n  Next={}", lastFrame.getClassName(), lastFrame.getMethodName(), last, current,
                          next );
        }

        logger.debug( "Last={} ->\n  Current={} ->\n  Next={}", last, current, next );
        last = current;
        current = next;

        logger.debug( "NEXT: {}", next );
        return next;
    }

    public List<XmlRpcCall> getCalls()
    {
        return calls;
    }

    public interface RecordingOp<T>
    {
        XmlRpcListener execute()
                throws XmlRpcException;
    }
}
