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
package org.commonjava.rwx.binding.spi.composed;

import org.commonjava.rwx.binding.spi.Bindery;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.spi.XmlRpcGenerator;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class CompositionBindery
    implements Bindery
{

    private final RenderingBinderyDelegate renderer;

    private final ParsingBinderyDelegate parser;

    public CompositionBindery( final RenderingBinderyDelegate renderer, final ParsingBinderyDelegate parser )
    {
        this.renderer = renderer;
        this.parser = parser;
    }

    public <T> T parse( final InputStream in, final Class<T> type )
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "CompositionBindery delegating parse to: {}", parser );
        return parser.parse( in, type );
    }

    public <T> T parse( final Reader in, final Class<T> type )
        throws XmlRpcException
    {
        return parser.parse( in, type );
    }

    public <T> T parse( final String in, final Class<T> type )
        throws XmlRpcException
    {
        return parser.parse( in, type );
    }

    public <T> T parse( final XmlRpcGenerator in, final Class<T> type )
        throws XmlRpcException
    {
        return parser.parse( in, type );
    }

    public void render( final OutputStream out, final Object value )
        throws XmlRpcException
    {
        renderer.render( out, value );
    }

    public void render( final Writer out, final Object value )
        throws XmlRpcException
    {
        renderer.render( out, value );
    }

    public void render( final XmlRpcListener out, final Object value )
        throws XmlRpcException
    {
        renderer.render( out, value );
    }

    public String renderString( final Object value )
        throws XmlRpcException
    {
        return renderer.renderString( value );
    }

}
