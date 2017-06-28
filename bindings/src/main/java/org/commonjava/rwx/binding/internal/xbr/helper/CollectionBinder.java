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

import static org.commonjava.rwx.binding.anno.AnnotationUtils.getContainsType;

import org.apache.xbean.recipe.CollectionRecipe;
import org.commonjava.rwx.binding.anno.Converter;
import org.commonjava.rwx.binding.internal.xbr.XBRBinderInstantiator;
import org.commonjava.rwx.binding.internal.xbr.XBRBindingContext;
import org.commonjava.rwx.binding.spi.Binder;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CollectionBinder
    extends AbstractBinder
    implements Binder
{

    private List<Object> values;

    private final Class<?> collectionType;

    private int currentIndex;

    private final Converter bindVia;

    public CollectionBinder( final Binder parent, final Class<?> collectionType, final Field field,
                             final XBRBindingContext context )
    {
        super( parent, getContainsType( field ), context );
        Converter bv = null;
        if ( field != null )
        {
            bv = field.getAnnotation( Converter.class );
            if ( bv == null )
            {
                bv = field.getType().getAnnotation( Converter.class );
            }
        }
        this.bindVia = bv;
        this.collectionType = collectionType;
    }

    public CollectionBinder( final Binder parent, final Class<?> collectionType, final Class<?> valueType,
                             final Field field, final XBRBindingContext context )
    {
        super( parent, valueType, context );
        Converter bv = null;
        if ( field != null )
        {
            bv = field.getAnnotation( Converter.class );
            if ( bv == null )
            {
                bv = field.getType().getAnnotation( Converter.class );
            }
        }
        this.bindVia = bv;
        this.collectionType = collectionType;
    }

    @Override
    public XmlRpcListener arrayElement( final int index, final Object value, final ValueType type )
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "\n\n\nARRAY VAL: @{} == {}", index, value );
        if ( currentIndex < 0 )
        {
            addValue( index, value );
        }

        return this;
    }

    private void addValue( final int index, final Object value )
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        try
        {
            if ( values == null )
            {
                logger.trace( "{} Setting up values list.", this );
                values = new ArrayList<>();
            }

            int startSz = values.size();
            for( int i=startSz; i<=index; i++)
            {
                logger.trace( "{} Padding value {} as null", this, i );
                values.add( null );
            }

            logger.trace( "{} Setting value at {} (out of {} values) to: {}", this, index, values.size(), value );
            values.set( index, value );
        }
        catch ( RuntimeException r )
        {
            logger.error( String.format( "Failed to set array value at: %s to: %s.", index, value ), r );
            throw r;
        }
    }

    protected List<Object> getValues()
    {
        return values;
    }

    @Override
    protected Binder endArrayInternal()
        throws XmlRpcException
    {
        setValue( create(), ValueType.ARRAY );

        return this;
    }

    protected Object create()
    {
        final CollectionRecipe recipe = new CollectionRecipe( collectionType );
        recipe.addAll( getValues() );
        return recipe.create();
    }

    @Override
    protected Binder startArrayInternal()
        throws XmlRpcException
    {
        if ( values == null )
        {
            values = new ArrayList<Object>();
            return this;
        }

        return super.startArrayInternal();
    }

//    @Override
//    protected XmlRpcListener parameterInternal( final int index, final Object value, final ValueType type )
//            throws XmlRpcException
//    {
//        return arrayElementInternal( index, value, type );
//    }
//
//    @Override
//    protected Binder endParameterInternal()
//            throws XmlRpcException
//    {
//        return endArrayElementInternal();
//    }
//
//    @Override
//    protected Binder startParameterInternal( final int index )
//            throws XmlRpcException
//    {
//        return startArrayElementInternal( index );
//    }

    @Override
    protected Binder startArrayElementInternal( final int index )
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "{} START array element @ {}", this, index );

        logger.trace( "{} SET currentIndex to: {}", this, index );
        currentIndex = index;

        final Binder binder;
        if ( bindVia != null )
        {
            binder = XBRBinderInstantiator.newValueBinder( bindVia, this, getType(), getBindingContext() );
        }
        else
        {
            binder = getBindingContext().newBinder( this, getType() );
        }

        if ( binder != null )
        {
//            currentIndex = index;
            return binder;
        }

        return this;
    }

    @Override
    protected Binder valueInternal( final Object value, final ValueType type )
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "\n\n\n{} ARRAY VAL: @{} == {}\n\n", this, currentIndex, value );
        if ( currentIndex > -1 )
        {
            addValue( currentIndex, value );
        }

        return this;
    }

    @Override
    protected Binder endArrayElementInternal()
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "{} END array element @ {}", this, currentIndex );
        currentIndex = -1;
        return this;
    }

}
