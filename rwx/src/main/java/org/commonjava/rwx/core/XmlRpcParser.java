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
package org.commonjava.rwx.core;

import org.apache.commons.lang.StringUtils;
import org.commonjava.rwx.error.CoercionException;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.vocab.ValueType;
import org.commonjava.rwx.model.Fault;
import org.commonjava.rwx.model.MethodCall;
import org.commonjava.rwx.model.MethodResponse;
import org.commonjava.rwx.model.RpcObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.commonjava.rwx.vocab.Nil.NIL_VALUE;
import static org.commonjava.rwx.vocab.XmlRpcConstants.*;

/**
 * XML-RPC request/response parser. This will parse input xml stream and return an RpcObject (MethodCall, MethodResponse or a Fault)
 * which represents a Map/List structure.
 *
 * Created by ruhan on 7/13/17.
 */
public class XmlRpcParser
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final XMLInputFactory factory;

    private XMLStreamReader reader;

    public XmlRpcParser( final InputStream in ) throws XmlRpcException
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

    /**
     * Parse method request or response. XML-RPC response has either fault or params element.
     * @return
     * @throws XmlRpcException
     */
    public RpcObject parse() throws XmlRpcException, XMLStreamException
    {
        RpcObject ret = null;

        int level = 0;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( REQUEST ) )
                {
                    ret = parseRequest( reader );
                    level--;
                }
                else if ( localName.equals( RESPONSE ) )
                {
                    ret = parseResponse( reader );
                    level--;
                }
                else
                {
                    throw new XmlRpcException( "Invalid XML-RPC root element: " + localName );
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return ret;
    }

    private MethodCall parseRequest( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        MethodCall ret = new MethodCall();

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( PARAMS ) )
                {
                    List<Object> params = parseParams( reader );
                    ret.setParams( params );
                    level--;
                }
                else if ( localName.equals( METHOD_NAME ) )
                {
                    event = reader.next();
                    if ( event == XMLStreamConstants.CHARACTERS )
                    {
                        String text = reader.getText();
                        if ( StringUtils.isNotBlank( text ) )
                        {
                            ret.setMethodName( text.trim() );
                            logger.trace( "Read methodName: " + text );
                        }
                    }
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return ret;
    }

    private RpcObject parseResponse( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        RpcObject ret = null;

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( PARAMS ) )
                {
                    MethodResponse response = new MethodResponse();
                    List<Object> params = parseParams( reader );
                    response.setParams( params );
                    ret = response;
                    level--;
                }
                else if ( localName.equals( FAULT ) )
                {
                    Fault fault = new Fault();
                    Object value = parseFault( reader );
                    fault.setValue( value );
                    ret = fault;
                    level--;
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return ret;
    }

    private List<Object> parseParams( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        List<Object> ret = new ArrayList<>();

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( PARAM ) )
                {
                    Object param = parseParam( reader );
                    ret.add( param );
                    level--;
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return ret;
    }

    private Object parseFault( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        return parseParam( reader ); // fault and param have same structure
    }

    private Object parseParam( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        Object ret = null;

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( VALUE ) )
                {
                    ret = parseValue( reader );
                    level--;
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return ret;
    }

    private Object parseValue( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        Object ret = null;

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( ARRAY ) )
                {
                    ret = parseArray( reader );
                    level--;
                }
                else if ( localName.equals( STRUCT ) )
                {
                    ret = parseStruct( reader );
                    level--;
                }
                else if ( localName.equals( NIL ) )
                {
                    ret = parseNil( reader );
                    level--;
                }
                else
                {
                    ret = parsePrimitive( localName, reader ); // xml-rpc primitives, string, int, etc.
                    level--;
                }
            }
            else if ( event == XMLStreamConstants.CHARACTERS ) // default string value, takes form of <value>str</value>
            {
                String text = reader.getText();
                if ( StringUtils.isNotBlank( text ) )
                {
                    ret = text.trim();
                    logger.trace( "Read value: " + text );
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return ret;
    }

    private Map<String, Object> parseStruct( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        Map<String, Object> ret = new HashMap<>();

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( MEMBER ) )
                {
                    Map.Entry<String, Object> kv = parseMember( reader );
                    ret.put( kv.getKey(), kv.getValue() );
                    level--;
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );
        return ret;
    }

    private Map.Entry<String, Object> parseMember( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        String key = null;
        Object value = null;

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( NAME ) )
                {
                    event = reader.next();
                    if ( event == XMLStreamConstants.CHARACTERS )
                    {
                        key = reader.getText().trim();
                        logger.trace( "Read key: " + key );
                    }
                }
                else if ( localName.equals( VALUE ) )
                {
                    value = parseValue( reader );
                    level--;
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return new SimpleEntry<>( key, value );
    }

    private List<Object> parseArray( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        List<Object> ret = null;

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( DATA ) )
                {
                    ret = parseData( reader );
                    level--;
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return ret;
    }

    private List<Object> parseData( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        List<Object> ret = new ArrayList<>();

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.START_ELEMENT )
            {
                level++;
                String localName = reader.getLocalName();

                logger.trace( "Start <" + localName + ">" );

                if ( localName.equals( VALUE ) )
                {
                    ret.add( parseValue( reader ) );
                    level--;
                }
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return ret;
    }

    private Object parsePrimitive( String type, XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        Object ret = null;

        ValueType vt = ValueType.typeOf( type );
        StringBuilder sb = null;

        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.CHARACTERS )
            {
                String text = reader.getText();
                logger.trace( "Read value: " + text + ", type=" + type );
                if ( sb == null )
                {
                    sb = new StringBuilder();
                }
                sb.append( text );
            }
            else if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        if ( sb != null )
        {
            ret = vt.coercion().fromString( sb.toString() );
        }
        return ret;
    }

    private Object parseNil( XMLStreamReader reader ) throws XMLStreamException, CoercionException
    {
        int level = 1;
        do
        {
            int event = reader.next();
            if ( event == XMLStreamConstants.END_ELEMENT )
            {
                logger.trace( "End </" + reader.getLocalName() + ">" );
                level--;
            }
        }
        while ( level > 0 );

        return NIL_VALUE;
    }

}
