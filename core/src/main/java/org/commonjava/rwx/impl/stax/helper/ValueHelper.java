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
package org.commonjava.rwx.impl.stax.helper;

import org.commonjava.rwx.error.CoercionException;
import org.commonjava.rwx.error.XmlRpcException;
import org.commonjava.rwx.impl.TrackingXmlRpcListener;
import org.commonjava.rwx.spi.XmlRpcListener;
import org.commonjava.rwx.vocab.ValueType;
import org.commonjava.rwx.vocab.XmlRpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * NOTE: This is a STATEFUL helper. It is NOT threadsafe!
 */
public class ValueHelper
    implements XMLStreamConstants
{

    private Object value;

    private boolean valueSet = false;

    private ValueType type;

    private boolean enableEvents = false;

    public ValueHelper()
    {
        enableEvents = true;
    }

    public ValueHelper( final boolean enableEvents )
    {
        this.enableEvents = enableEvents;
    }

    public Object getValue()
    {
        return value;
    }

    public ValueType getValueType()
    {
        return type;
    }

    public Object parse( final XMLStreamReader reader, final XmlRpcListener listener )
        throws XMLStreamException, XmlRpcException
    {
        int evt;
        Logger logger = LoggerFactory.getLogger( getClass() );
        do
        {
            evt = reader.next();
            if ( !valueSet && evt == CHARACTERS )
            {
                final String src = reader.getText();

                type = ValueType.STRING;
                value = src;

                logger.trace( "Parsed string value: '{}'", value );
            }
            else if ( evt == START_ELEMENT )
            {
                final String tag = reader.getName().getLocalPart();
                if ( XmlRpcConstants.VALUE.equals( tag ) )
                {
                    throw new XmlRpcException( "Nested " + XmlRpcConstants.VALUE + " elements detected." );
                }
                else if ( XmlRpcConstants.STRUCT.equals( tag ) )
                {
                    logger.trace( "Handing off to StructHelper at:\n{}\nWith listener: {}", reader.getLocation(), listener );
                    value = StructHelper.parse( reader, listener, enableEvents );
                    type = ValueType.STRUCT;
                    valueSet = true;
                }
                else if ( XmlRpcConstants.ARRAY.equals( tag ) )
                {
                    logger.trace( "Handing off to ArrayHelper at:\n{}\nWith listener: {}", reader.getLocation(), listener );
                    value = ArrayHelper.parse( reader, listener, enableEvents );
                    type = ValueType.ARRAY;
                    valueSet = true;
                }
                else
                {
                    parseSimpleValue( reader, listener );
                }
            }
            else if ( evt == XMLStreamReader.END_ELEMENT
                && XmlRpcConstants.VALUE.equals( reader.getName().getLocalPart() ) )
            {
                break;
            }
        }
        while ( evt != END_DOCUMENT );

        if ( enableEvents )
        {
            listener.value( value, type );
        }
        return value;
    }

    private void parseSimpleValue( final XMLStreamReader reader, final XmlRpcListener listener )
        throws XMLStreamException, CoercionException
    {
        type = ValueType.typeOf( reader.getName().getLocalPart() );

        final String src = reader.getElementText().trim();

        value = type == null ? src : type.coercion().fromString( src );
        valueSet = true;
    }

}
