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

import static org.commonjava.rwx.binding.anno.AnnotationUtils.hasAnnotation;

import org.commonjava.rwx.binding.anno.ArrayPart;
import org.commonjava.rwx.binding.internal.xbr.XBRBindingContext;
import org.commonjava.rwx.binding.spi.Binder;
import org.commonjava.rwx.binding.spi.BindingContext;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;

public class ArrayWrapperBinder
    extends AbstractBinder
    implements Binder
{

    private Object value;

    public ArrayWrapperBinder( final Binder parent, final Class<?> valueType, final XBRBindingContext context )
    {
        super( parent, valueType, context );
    }

    @Override
    public XmlRpcListener arrayElement( final int index, final Object value, final ValueType type )
        throws XmlRpcException
    {
        if ( this.value == null )
        {
            this.value = value;
        }

        return this;
    }

    @Override
    protected Binder endArrayInternal()
        throws XmlRpcException
    {
        @SuppressWarnings( "unchecked" )
        final ValueType vt = hasAnnotation( getType(), ArrayPart.class ) ? ValueType.ARRAY : ValueType.STRUCT;
        setValue( value, vt );

        return this;
    }

    @Override
    protected Binder startArrayInternal()
        throws XmlRpcException
    {
        return this;
    }

    @Override
    protected Binder startArrayElementInternal( final int index )
        throws XmlRpcException
    {
        return this;
    }

    @Override
    protected Binder valueInternal( final Object value, final ValueType type )
        throws XmlRpcException
    {
        return this;
    }

    @Override
    protected Binder endArrayElementInternal()
        throws XmlRpcException
    {
        return this;
    }
}
