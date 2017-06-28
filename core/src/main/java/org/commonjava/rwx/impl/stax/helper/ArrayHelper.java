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
import java.util.ArrayList;
import java.util.List;

public class ArrayHelper
    implements XMLStreamConstants
{

    public static List<Object> parse( final XMLStreamReader reader, final TrackingXmlRpcListener listener )
        throws XMLStreamException, XmlRpcException
    {
        return parse( reader, listener, true );
    }

    public static List<Object> parse( final XMLStreamReader reader, final XmlRpcListener listener,
                                      final boolean enableEvents )
        throws XMLStreamException, XmlRpcException
    {
        if ( enableEvents )
        {
            listener.startArray();
        }

        final List<Object> values = new ArrayList<>();

        int count = 0;
        int type;

        Logger logger = LoggerFactory.getLogger( ArrayHelper.class );
        do
        {
            type = reader.nextTag();
            if ( type == START_ELEMENT )
            {
                if ( XmlRpcConstants.VALUE.equals( reader.getName().getLocalPart() ) )
                {
                    if ( enableEvents )
                    {
                        listener.startArrayElement( count );
                    }

                    logger.trace( "Handing off to ValueHelper at: {}", reader.getLocation() );
                    final ValueHelper vh = new ValueHelper();
                    vh.parse( reader, listener );

                    final Object value = vh.getValue();
                    final ValueType vt = vh.getValueType();

                    values.add( value );

                    if ( enableEvents )
                    {
                        logger.trace( "Firing arrayElement({}, {}, {}) on {}", count, value, vt, listener );
                        listener.arrayElement( count, value, vt );
                        listener.endArrayElement();
                    }

                    count++;
                }
            }
            else if ( type == XMLStreamReader.END_ELEMENT
                && XmlRpcConstants.ARRAY.equals( reader.getName().getLocalPart() ) )
            {
                break;
            }
        }
        while ( type != END_DOCUMENT );

        if ( enableEvents )
        {
            listener.endArray();
        }

        return values;
    }

}
