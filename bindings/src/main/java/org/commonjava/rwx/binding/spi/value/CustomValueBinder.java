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
package org.commonjava.rwx.binding.spi.value;

import org.commonjava.rwx.binding.internal.xbr.XBRBindingContext;
import org.commonjava.rwx.binding.internal.xbr.helper.AbstractBinder;
import org.commonjava.rwx.binding.spi.Binder;
import org.commonjava.rwx.binding.spi.BindingContext;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CustomValueBinder
    extends AbstractBinder
    implements ValueBinder
{

    protected CustomValueBinder( final Binder parent, final Class<?> type, final XBRBindingContext context )
    {
        super( parent, type, context );
    }

    protected abstract Object getResult( Object v, ValueType t )
            throws XmlRpcException;

    protected abstract ValueType getResultType( Object v, ValueType t )
            throws XmlRpcException;

    @Override
    public final XmlRpcListener value( Object v, ValueType t )
            throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "Got value: {} (class: {}), with type: {}", v, (v == null ? "NULL" : v.getClass().getName()), t );

        ValueType type = getResultType(v, t);
        Object result = getResult( v, t );

        Binder parent = getParent();
        logger.trace( "Assigning value: {} with type: {} in parent: {}", result, type, parent );
        parent.value( result, type );

        return parent;
    }

    @Override
    protected final Binder endArrayInternal()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder endArrayElementInternal()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder endStructInternal()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder endStructMemberInternal()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder startArrayInternal()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder startArrayElementInternal( int index )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder startStructInternal()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder startStructMemberInternal( String key )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener structMember( String k, Object v, ValueType t )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected Binder structMemberInternal( String key, Object v, ValueType t )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener arrayElement( int i, Object v, ValueType t )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder arrayElementInternal( int i, Object v, ValueType t )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder valueInternal( Object value, ValueType type )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder endParameterInternal()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    protected final Binder startParameterInternal( int index )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener endRequest()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener endResponse()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener fault( int code, String message )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener parameter( int index, Object value, ValueType type )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener requestMethod( String methodName )
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener startRequest()
            throws XmlRpcException
    {
        throw forbidden();
    }

    @Override
    public final XmlRpcListener startResponse()
            throws XmlRpcException
    {
        throw forbidden();
    }
}
