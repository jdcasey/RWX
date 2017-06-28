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
package org.commonjava.rwx.binding.internal.xbr.helper;

import org.commonjava.rwx.binding.internal.xbr.XBRBindingContext;
import org.commonjava.rwx.binding.spi.Binder;
import org.commonjava.rwx.binding.spi.BindingContext;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.spi.AbstractXmlRpcListener;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBinder
    extends AbstractXmlRpcListener
    implements Binder
{

    private final Class<?> type;

    private Object value;

    private final XBRBindingContext context;

    private final Binder parent;

    private int count = 0;

    private ValueType valueType;

    protected AbstractBinder( final Binder parent, final Class<?> type, final XBRBindingContext context )
    {
        this.parent = parent;
        this.context = context;
        this.type = type;

        if ( parent == null )
        {
            // this is a top-level binder, and the top-level doesn't have a params event
            count=1;
        }
    }

    protected final void setValue( final Object value, final ValueType valueType )
    {
        this.value = value;
        this.valueType = valueType;
    }

    public final Binder getParent()
    {
        return parent;
    }

    public final BindingContext getBindingContext()
    {
        return context;
    }

    public final Class<?> getType()
    {
        return type;
    }

    @Override
    public final XmlRpcListener endArray()
        throws XmlRpcException
    {
        return decrement( endArrayInternal() );
    }

    protected Binder endArrayInternal()
        throws XmlRpcException
    {
        return this;
    }

    @Override
    public final XmlRpcListener endArrayElement()
        throws XmlRpcException
    {
        return decrement( endArrayElementInternal() );
    }

    protected Binder endArrayElementInternal()
        throws XmlRpcException
    {
        return this;
    }

    @Override
    public final XmlRpcListener endStruct()
        throws XmlRpcException
    {
        return decrement( endStructInternal() );
    }

    protected Binder endStructInternal()
        throws XmlRpcException
    {
        return this;
    }

    @Override
    public final XmlRpcListener endStructMember()
        throws XmlRpcException
    {
        return decrement( endStructMemberInternal() );
    }

    protected Binder endStructMemberInternal()
        throws XmlRpcException
    {
        return this;
    }

    @Override
    public final XmlRpcListener startArray()
        throws XmlRpcException
    {
        return increment( startArrayInternal() );
    }

    protected Binder startArrayInternal()
        throws XmlRpcException
    {
        return new CollectionBinder( this, List.class, null, context );
    }

    @Override
    public final XmlRpcListener startArrayElement( final int index )
        throws XmlRpcException
    {
        return increment( startArrayElementInternal( index ) );
    }

    protected Binder startArrayElementInternal( final int index )
        throws XmlRpcException
    {
        return this;
    }

    @Override
    public final XmlRpcListener startStruct()
        throws XmlRpcException
    {
        return increment( startStructInternal() );
    }

    protected Binder startStructInternal()
        throws XmlRpcException
    {
        return new MapBinder( this, HashMap.class, null, context );
    }

    @Override
    public final XmlRpcListener startStructMember( final String key )
        throws XmlRpcException
    {
        return increment( startStructMemberInternal( key ) );
    }

    protected Binder startStructMemberInternal( final String key )
        throws XmlRpcException
    {
        return this;
    }

    @Override
    public XmlRpcListener structMember( String k, Object v, ValueType t )
            throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "Got member: '{}' with {} value: {}", k, t, v );
        logger.trace( "Handing off to internal value call." );
        return structMemberInternal( k, v, t );
    }

    protected Binder structMemberInternal( String key, Object v, ValueType t )
            throws XmlRpcException
    {
        return this;
    }

    @Override
    public XmlRpcListener parameter( final int index, final Object value, final ValueType type )
            throws XmlRpcException
    {
        return parameterInternal( index, value, type );
    }

    protected XmlRpcListener parameterInternal( final int index, final Object value, final ValueType type )
            throws XmlRpcException
    {
        return this;
    }


    @Override
    public XmlRpcListener arrayElement( int i, Object v, ValueType t )
            throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "Got array[{}] {} value: {}", i, t, v );
        logger.trace( "Handing off to internal value call." );
        return arrayElementInternal( i, v, t );
    }

    protected Binder arrayElementInternal( int i, Object v, ValueType t )
            throws XmlRpcException
    {
        return this;
    }

    private final Binder decrement( final Binder binder )
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "{} DECREMENT nesting count: {} to {} with binder: {}\nFrom: {}.{}", this, count, (count-1), binder, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName() );
        count--;

        return binder;
    }

    private final Binder increment( final Binder binder )
    {
        if ( this == binder )
        {
            Logger logger = LoggerFactory.getLogger( getClass() );
            logger.trace( "{} INCREMENT nesting count: {} to {} with binder: {}\nFrom: {}.{}", this, count, (count+1), binder, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName() );
            count++;
        }
        return binder;
    }

    @Override
    public XmlRpcListener value( final Object v, final ValueType t )
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "{} Got {} value: {} (nesting count={})", this, t, v, count );
        if ( count < 1 )
        {
            logger.trace( "{} Passing value back to parent: {}", this, parent );
            parent.value( value, valueType );
            return parent;
        }
        else
        {
            logger.trace( "{} Handing off to internal value call.", this );
            return valueInternal( v, t );
        }
    }

    protected Binder valueInternal( final Object value, final ValueType type )
        throws XmlRpcException
    {
//        Binder parent = getParent();
//
//        Logger logger = LoggerFactory.getLogger( getClass() );
//        logger.trace( "Got value: {}. Setting on parent, then returning parent: {}", parent );
//        parent.value( value, type );
        return this;
    }

    @Override
    public final XmlRpcListener endParameter()
        throws XmlRpcException
    {
        return decrement( endParameterInternal() );
    }

    protected Binder endParameterInternal()
        throws XmlRpcException
    {
        return this;
    }

    @Override
    public final XmlRpcListener startParameter( final int index )
        throws XmlRpcException
    {
        return increment( startParameterInternal( index ) );
    }

    protected Binder startParameterInternal( final int index )
        throws XmlRpcException
    {
        return this;
    }

}
