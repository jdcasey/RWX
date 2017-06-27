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

import org.apache.xbean.recipe.ObjectRecipe;
import org.commonjava.rwx.binding.internal.xbr.XBRBindingContext;
import org.commonjava.rwx.binding.mapping.ArrayMapping;
import org.commonjava.rwx.binding.mapping.FieldBinding;
import org.commonjava.rwx.binding.spi.Binder;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.error.XmlRpcFaultException;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class MessageBinder
    extends AbstractMappingBinder<ArrayMapping>
    implements Binder
{

    private final ObjectRecipe recipe;

    private FieldBinding currentField;

    private boolean ignore = false;

    public MessageBinder( final Class<?> type, final ArrayMapping mapping, final XBRBindingContext context )
    {
        super( null, type, mapping, context );
        recipe = XBRBindingContext.setupObjectRecipe( mapping );
    }

    public Object create()
    {
        return recipe.create();
    }

    @Override
    public XmlRpcListener parameter( final int index, final Object value, final ValueType type )
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.info( "Parameter: @{}, value: {}, type: {}", index, value, type );

        if ( !ignore && currentField == null )
        {
            final FieldBinding binding = getMapping().getFieldBinding( index );
            logger.debug( "SET: {}.{} = {}", getMapping().getObjectType().getName(), binding.getFieldName(), value );
            recipe.setProperty( binding.getFieldName(), value );
        }

        return this;
    }

    @Override
    protected Binder startParameterInternal( final int index )
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "Start parameter: {}", index );

        final FieldBinding binding = getMapping().getFieldBinding( index );
        if ( binding == null )
        {
            logger.trace( "No field binding for: {}", index );
            ignore = true;
            return this;
        }

        final Field field = getBindingContext().findField( binding, getType() );

        final Binder binder = getBindingContext().newBinder( this, field );
        if ( binder != null )
        {
            currentField = binding;
            logger.trace( "Current param binder: {} for field: {}", currentField, field );
            return binder;
        }

        return this;
    }

    @Override
    protected Binder valueInternal( final Object value, final ValueType type )
        throws XmlRpcException
    {
        if ( !ignore && currentField != null )
        {
            Logger logger = LoggerFactory.getLogger( getClass() );
            logger.debug( "SET: {}.{} = {}", getMapping().getObjectType().getName(), currentField.getFieldName(), value );
            recipe.setProperty( currentField.getFieldName(), value );
        }

        return this;
    }

    @Override
    public XmlRpcListener fault( final int code, final String message )
        throws XmlRpcException
    {
        throw new XmlRpcFaultException( code, message );
    }

    @Override
    protected Binder endParameterInternal()
        throws XmlRpcException
    {
        currentField = null;
        ignore = false;
        return this;
    }

}
