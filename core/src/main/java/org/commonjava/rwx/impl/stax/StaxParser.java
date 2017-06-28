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
package org.commonjava.rwx.impl.stax;

import org.apache.commons.lang.StringUtils;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.impl.TrackingXmlRpcListener;
import org.commonjava.rwx.impl.estream.EventStreamParserImpl;
import org.commonjava.rwx.impl.stax.helper.RequestHelper;
import org.commonjava.rwx.impl.stax.helper.ResponseHelper;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.spi.XmlRpcParser;
import org.commonjava.rwx.vocab.XmlRpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class StaxParser
    implements XmlRpcParser
{

    private final XMLInputFactory factory;

    private XMLStreamReader reader;

    public StaxParser( final InputStream in )
        throws XmlRpcException
    {
        factory = XMLInputFactory.newInstance();
        try
        {
            reader = factory.createXMLStreamReader( in );
        }
        catch ( final XMLStreamException e )
        {
            throw new XmlRpcException( "Failed to initialize stream reader: " + e.getMessage(), e );
        }
    }

    public StaxParser( final Reader in )
        throws XmlRpcException
    {
        factory = XMLInputFactory.newInstance();
        try
        {
            reader = factory.createXMLStreamReader( in );
        }
        catch ( final XMLStreamException e )
        {
            throw new XmlRpcException( "Failed to initialize stream reader: " + e.getMessage(), e );
        }
    }

    public StaxParser( final String in )
        throws XmlRpcException
    {
        factory = XMLInputFactory.newInstance();
        try
        {
            reader = factory.createXMLStreamReader( new StringReader( in ) );
        }
        catch ( final XMLStreamException e )
        {
            throw new XmlRpcException( "Failed to initialize stream reader: " + e.getMessage(), e );
        }
    }

    public void parse( final XmlRpcListener l )
        throws XmlRpcException
    {
        Logger logger = LoggerFactory.getLogger( getClass() );
        logger.trace( "Wrapping '{}' for tracing, the proceeding with parse.", l );

        XmlRpcListener listener = l;
        TrackingXmlRpcListener tracker = null;
//        EventStreamParserImpl estream = null;
//        if ( logger.isTraceEnabled() )
//        {
            tracker = new TrackingXmlRpcListener( l );
            listener = tracker;
//            estream = new EventStreamParserImpl( tracker );
//            listener = estream;
//        }

        try
        {
            while ( reader.hasNext() )
            {
                final int tag = reader.next();
                if ( tag == XMLStreamReader.START_ELEMENT )
                {
                    logger.trace( "starting parse with: '{}'", listener );
                    final String tagName = reader.getName().getLocalPart();
                    if ( XmlRpcConstants.REQUEST.equals( tagName ) )
                    {
                        RequestHelper.parse( reader, listener );
                    }
                    else if ( XmlRpcConstants.RESPONSE.equals( tagName ) )
                    {
                        ResponseHelper.parse( reader, listener );
                    }
                }
            }
        }
        catch ( final XMLStreamException e )
        {
            throw new XmlRpcException( "Failed to parse input: " + e.getMessage(), e );
        }
        catch ( final RuntimeException e )
        {

        }
//        finally
//        {
//            if ( logger.isTraceEnabled() )
//            {
//                try
//                {
//                    logger.trace( "Message parse call trace:\n\n  {}\n\nEvent tree:\n\n  {}\n\n",
//                                  StringUtils.join( tracker.getCalls(), "\n  " ), estream.renderEventTree() );
//                }
//                catch ( Throwable t )
//                {
//                }
//            }
//        }
    }
}
